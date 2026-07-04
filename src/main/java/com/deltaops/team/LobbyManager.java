/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.team;

import com.deltaops.DeltaOpsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LobbyManager {
    private static final Map<UUID, BlockPos> PERSONAL_LOBBY_POSITIONS = new ConcurrentHashMap<>();

    public static void teleportToPersonalLobby(ServerPlayer player) {
        if (player == null) {
            return;
        }

        ServerLevel level = player.server.getLevel(Level.OVERWORLD);
        if (level == null) {
            return;
        }

        BlockPos pos = PERSONAL_LOBBY_POSITIONS.computeIfAbsent(player.getUUID(), uuid -> {
            int base = 5000 + (Math.abs(uuid.hashCode()) % 1000) * 10;
            return new BlockPos(base, 80, base);
        });

        player.teleportTo(level, pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, player.getYRot(), player.getXRot());
    }

    public static void syncToLeaderLobby(ServerPlayer member, ServerPlayer leader) {
        if (member == null || leader == null) {
            return;
        }

        ServerLevel level = leader.server.getLevel(Level.OVERWORLD);
        if (level == null) {
            return;
        }

        BlockPos leaderPos = leader.blockPosition();
        member.teleportTo(level, leaderPos.getX() + 0.5D, leaderPos.getY(), leaderPos.getZ() + 0.5D, member.getYRot(), member.getXRot());
    }
}
