/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.lobby;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatchmakingEngine {
    private static final Queue<UUID> soloPool = new ConcurrentLinkedQueue<>(); // player UUIDs
    private static final Queue<UUID> squadPool = new ConcurrentLinkedQueue<>(); // squad UUIDs
    private static final List<Match> upcomingMatches = Collections.synchronizedList(new ArrayList<>());

    public static void startMatchByLeader(ServerPlayer leader) {
        if (leader == null) return;
        LobbySquadManager.Squad squad = LobbySquadManager.getSquadByPlayer(leader.getUUID());
        if (squad == null) return;

        if (!squad.fillTeammates) {
            // immediate match with only squad members
            List<ServerPlayer> players = gatherPlayersFromSquad(squad);
            launchMatch(players);
            return;
        }

        // auto-fill behavior: add squad to squadPool and attempt to fill
        squadPool.add(squad.squadId);
        attemptFillAndLaunch();
    }

    private static List<ServerPlayer> gatherPlayersFromSquad(LobbySquadManager.Squad squad) {
        List<ServerPlayer> result = new ArrayList<>();
        if (squad == null) return result;
        MinecraftServer server = java.util.Objects.requireNonNullElseGet(net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer(), () -> null);
        if (server == null) return result;
        for (UUID member : squad.members) {
            ServerPlayer sp = server.getPlayerList().getPlayer(member);
            if (sp != null) result.add(sp);
        }
        return result;
    }

    private static void attemptFillAndLaunch() {
        // very basic fill: if there are at least two squads, merge them; or fill with solos
        while (!squadPool.isEmpty()) {
            UUID squadId = squadPool.poll();
            LobbySquadManager.Squad s = LobbySquadManager.getSquad(squadId);
            if (s == null) continue;
            int needed = s.limit - s.members.size();
            List<ServerPlayer> players = gatherPlayersFromSquad(s);
            // try to fill from soloPool
            while (needed > 0 && !soloPool.isEmpty()) {
                UUID solo = soloPool.poll();
                ServerPlayer sp = java.util.Objects.requireNonNullElseGet(net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer(), () -> null).getPlayerList().getPlayer(solo);
                if (sp != null) {
                    players.add(sp);
                    needed--;
                }
            }
            // if still need, try to merge other squads
            while (needed > 0 && !squadPool.isEmpty()) {
                UUID otherId = squadPool.poll();
                LobbySquadManager.Squad other = LobbySquadManager.getSquad(otherId);
                if (other == null) continue;
                List<ServerPlayer> otherPlayers = gatherPlayersFromSquad(other);
                for (ServerPlayer p : otherPlayers) {
                    if (needed <= 0) break;
                    players.add(p);
                    needed--;
                }
            }

            // launch even if not full
            launchMatch(players);
        }
    }

    private static void launchMatch(List<ServerPlayer> players) {
        launchMatch(players, null, null);
    }

    /**
     * Launch match and teleport players. If mapName is provided and a spawn is configured, teleport to that spawn.
     */
    public static void launchMatch(List<ServerPlayer> players, String mapName) {
        launchMatch(players, mapName, null);
    }

    public static void launchMatch(List<ServerPlayer> players, String mapName, ServerPlayer leader) {
        if (players == null || players.isEmpty()) return;
        if (leader != null && mapName != null && !mapName.isBlank()) {
            MapDefinition mapDefinition = HazardMapRegistry.getMap(mapName);
            if (mapDefinition != null) {
                int threshold = mapDefinition.minGearValue();
                for (ServerPlayer member : players) {
                    int gearValue = GearValueEvaluator.calculatePlayerGearValue(member);
                    if (gearValue < threshold) {
                        if (leader != null) {
                            leader.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                    String.format("§c[Delta Ops] ❌ 無法部署！隊員 [%s] 的戰備值 (%d) 未達地圖 [%s] 的最低要求 (%d 哈夫幣)。",
                                            member.getGameProfile().getName(), gearValue, mapDefinition.displayName(), threshold)
                            ));
                        }
                        member.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                                "§c[Delta Ops] ⚠️ 你的戰備值過低，無法進入此高難度區域！"
                        ));
                        return;
                    }
                }
            }
        }

        MinecraftServer server = java.util.Objects.requireNonNullElseGet(net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer(), () -> null);
        if (server == null) return;
        net.minecraft.server.level.ServerLevel target = server.overworld();

        // try to use configured spawn point
        net.minecraft.server.level.ServerLevel finalTarget = target;
        if (mapName != null) {
            Optional<com.deltaops.location.LocationDatabase.SpawnPoint> opt = com.deltaops.location.LocationDatabase.getRandomSpawnPoint(mapName);
            if (opt.isPresent()) {
                com.deltaops.location.LocationDatabase.SpawnPoint sp = opt.get();
                int idx = 0;
                for (ServerPlayer p : players) {
                    try {
                        p.teleportTo(finalTarget, sp.x + idx * 0.5, sp.y, sp.z + idx * 0.5, sp.yaw, sp.pitch);
                    } catch (Exception ignored) {}
                    idx++;
                }
                return;
            }
        }

        // fallback: world spawn
        BlockPos spawn = target.getSharedSpawnPos();
        int idx = 0;
        for (ServerPlayer p : players) {
            try {
                p.teleportTo(target, spawn.getX() + idx * 2, spawn.getY(), spawn.getZ() + idx * 2, p.getYRot(), p.getXRot());
            } catch (Exception ignored) {}
            idx++;
        }
    }

    private static class Match {
        public final UUID matchId = UUID.randomUUID();
        public final List<UUID> playerUuids = new ArrayList<>();
    }

    public static void enqueueSoloPlayer(ServerPlayer player) {
        if (player == null) return;
        soloPool.add(player.getUUID());
    }
}
