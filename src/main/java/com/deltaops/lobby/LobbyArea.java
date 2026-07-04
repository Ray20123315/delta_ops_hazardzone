/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.lobby;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class LobbyArea {
    public static boolean isInLobby(ServerPlayer player) {
        if (player == null || player.server == null) return false;
        Level overworld = player.server.overworld();
        if (overworld == null || player.level() != overworld) return false;
        BlockPos spawn = overworld.getSharedSpawnPos();
        return player.blockPosition().distSqr(spawn) <= 2000L * 2000L;
    }
}
