/* Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.container;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import com.deltaops.block.ModBlocks;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GridDistributionEngine {
    private static final Random RANDOM = new Random();
    private static final List<ContainerVariant> DEFAULT_VARIANTS = List.of(
            ContainerVariant.WEAPON_CASE,
            ContainerVariant.MEDICAL_BAG,
            ContainerVariant.AMMO_BOX,
            ContainerVariant.TOOLBOX,
            ContainerVariant.HIKING_BAG,
            ContainerVariant.TRAVEL_BAG,
            ContainerVariant.LOCKER,
            ContainerVariant.PC_CASE,
            ContainerVariant.DRAWER,
            ContainerVariant.FIELD_CRATE,
            ContainerVariant.EXPRESS_BOX,
            ContainerVariant.CAR_STORAGE,
            ContainerVariant.DESERT_CHEST
    );

    public static void distributeRandomLoot(ServerLevel level, AABB fullZone, double densityFactor, int gridSize) {
        if (level == null || fullZone == null || densityFactor <= 0.0 || gridSize <= 0) {
            return;
        }

        int minX = (int) Math.floor(fullZone.minX);
        int minZ = (int) Math.floor(fullZone.minZ);
        int maxX = (int) Math.ceil(fullZone.maxX);
        int maxZ = (int) Math.ceil(fullZone.maxZ);

        int width = Math.max(1, maxX - minX);
        int depth = Math.max(1, maxZ - minZ);
        int totalCellsX = Math.max(1, (int) Math.ceil((double) width / gridSize));
        int totalCellsZ = Math.max(1, (int) Math.ceil((double) depth / gridSize));
        int cellCount = totalCellsX * totalCellsZ;
        int totalLoot = Math.max(1, (int) Math.round(width * depth * densityFactor));
        int maxPerCell = Math.min(3, Math.max(1, (int) Math.ceil((double) totalLoot / cellCount)));

        for (int cellX = 0; cellX < totalCellsX; cellX++) {
            for (int cellZ = 0; cellZ < totalCellsZ; cellZ++) {
                int cellMinX = minX + cellX * gridSize;
                int cellMinZ = minZ + cellZ * gridSize;
                int cellMaxX = Math.min(maxX, cellMinX + gridSize);
                int cellMaxZ = Math.min(maxZ, cellMinZ + gridSize);

                int placed = 0;
                for (int attempt = 0; attempt < maxPerCell * 4 && placed < maxPerCell; attempt++) {
                    BlockPos candidate = new BlockPos(
                            cellMinX + RANDOM.nextInt(Math.max(1, cellMaxX - cellMinX)),
                            0,
                            cellMinZ + RANDOM.nextInt(Math.max(1, cellMaxZ - cellMinZ))
                    );
                    BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate);
                    BlockPos spawn = surface.below();
                    if (!isValidSpawnLocation(level, spawn, fullZone)) {
                        continue;
                    }
                    if (FixedLootManager.hasAnchorAt(spawn)) {
                        continue;
                    }

                    ContainerVariant variant = DEFAULT_VARIANTS.get(RANDOM.nextInt(DEFAULT_VARIANTS.size()));
                    BlockState state = level.getBlockState(spawn);
                    if (!state.isAir()) {
                        continue;
                    }

                    level.setBlockAndUpdate(spawn, ModBlocks.TACTICAL_CONTAINER.get().defaultBlockState().setValue(TacticalContainerBlock.VARIANT, variant));
                    if (level.getBlockEntity(spawn) instanceof TacticalContainerBlockEntity entity) {
                        entity.setVariant(variant);
                        entity.populateLootIfEmpty();
                    }
                    placed++;
                }
            }
        }
    }

    private static boolean isValidSpawnLocation(ServerLevel level, BlockPos spawn, AABB fullZone) {
        if (level == null || spawn == null || fullZone == null) {
            return false;
        }
        if (spawn.getY() <= 0 || spawn.getY() > level.getMaxBuildHeight()) {
            return false;
        }
        if (!level.getBlockState(spawn).isAir()) {
            return false;
        }
        if (spawn.getX() < fullZone.minX || spawn.getX() > fullZone.maxX || spawn.getZ() < fullZone.minZ || spawn.getZ() > fullZone.maxZ) {
            return false;
        }
        BlockPos below = spawn.below();
        if (below.getY() < 0) {
            return false;
        }
        BlockState ground = level.getBlockState(below);
        return ground.isFaceSturdy(level, below, net.minecraft.core.Direction.UP);
    }
}
