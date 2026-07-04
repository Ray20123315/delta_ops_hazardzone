/* Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.container;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class FixedLootManager {
    private static final Map<String, List<FixedAnchor>> FIXED_ANCHORS = new ConcurrentHashMap<>();

    public static void registerFixedNode(String mapId, BlockPos pos, String containerType) {
        if (mapId == null || mapId.isBlank() || pos == null || containerType == null || containerType.isBlank()) {
            return;
        }
        FIXED_ANCHORS.computeIfAbsent(mapId, ignored -> new ArrayList<>())
                .add(new FixedAnchor(mapId, pos, containerType));
    }

    public static List<FixedAnchor> getFixedAnchors(String mapId) {
        if (mapId == null || mapId.isBlank()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(FIXED_ANCHORS.getOrDefault(mapId, Collections.emptyList()));
    }

    public static boolean hasAnchorAt(BlockPos pos) {
        if (pos == null) {
            return false;
        }
        for (List<FixedAnchor> anchors : FIXED_ANCHORS.values()) {
            for (FixedAnchor anchor : anchors) {
                if (anchor.pos().equals(pos)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void spawnFixedAnchors(ServerLevel level, String mapId) {
        if (level == null || mapId == null || mapId.isBlank()) {
            return;
        }
        for (FixedAnchor anchor : getFixedAnchors(mapId)) {
            ContainerVariant variant = ContainerVariant.fromName(anchor.containerType());
            if (variant == null) {
                variant = ContainerVariant.HACKER_PC;
            }
            LootContainerSpawner.spawnFixedHighValue(level, anchor.pos(), variant);
        }
    }

    public static record FixedAnchor(String mapId, BlockPos pos, String containerType) {
    }
}
