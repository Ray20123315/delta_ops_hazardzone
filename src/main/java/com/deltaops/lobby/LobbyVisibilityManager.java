/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.lobby;

import com.deltaops.DeltaOpsMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class LobbyVisibilityManager {
    @SubscribeEvent
    public static void onStartTracking(PlayerEvent.StartTracking event) {
        if (!(event.getEntity() instanceof ServerPlayer tracking)) return;
        if (!(event.getTarget() instanceof ServerPlayer target)) return;

        // only apply lobby visibility if both are in the lobby area
        if (!isInLobby(tracking) || !isInLobby(target)) return;

        LobbySquadManager.Squad tSquad = LobbySquadManager.getSquadByPlayer(tracking.getUUID());
        LobbySquadManager.Squad oSquad = LobbySquadManager.getSquadByPlayer(target.getUUID());

        boolean sameSquad = tSquad != null && oSquad != null && tSquad.squadId.equals(oSquad.squadId);
        if (!sameSquad) {
            // cancel sending tracking packets so the players remain invisible to each other
            event.setCanceled(true);
        }
    }

    private static boolean isInLobby(ServerPlayer p) {
        if (p == null || p.server == null) return false;
        Level lobbyLevel = p.server.getLevel(Level.OVERWORLD);
        if (lobbyLevel == null) return false;
        // consider within 2000 blocks of world spawn as lobby
        BlockPos spawn = lobbyLevel.getSharedSpawnPos();
        return p.level() == lobbyLevel && p.blockPosition().distSqr(spawn) <= 2000L * 2000L;
    }
}
