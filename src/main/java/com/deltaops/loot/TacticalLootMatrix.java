/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loot;

import com.deltaops.container.ContainerVariant;
import com.deltaops.container.ContainerVariant;
import java.util.Optional;
import java.util.Random;

public class TacticalLootMatrix {
    private static final Random RANDOM = new Random();

    public static ItemQuality rollQuality(ContainerVariant.ContainerTier tier) {
        if (tier == null) {
            return ItemQuality.WHITE;
        }
        return switch (tier) {
            case HIGH_TIER -> rollByPercent(60, ItemQuality.PURPLE, 30, ItemQuality.GOLD, 10, ItemQuality.RED);
            case MID_TIER -> rollByPercent(50, ItemQuality.BLUE, 40, ItemQuality.PURPLE, 10, ItemQuality.GOLD);
            case LOW_TIER -> rollByPercent(80, ItemQuality.BLUE, 19, ItemQuality.PURPLE, 1, ItemQuality.GOLD);
            case TRASH_TIER -> rollByPercent(70, ItemQuality.WHITE, 30, ItemQuality.GREEN);
        };
    }

    private static ItemQuality rollByPercent(int p1, ItemQuality q1, int p2, ItemQuality q2) {
        int roll = RANDOM.nextInt(100) + 1;
        return roll <= p1 ? q1 : q2;
    }

    private static ItemQuality rollByPercent(int p1, ItemQuality q1, int p2, ItemQuality q2, int p3, ItemQuality q3) {
        int roll = RANDOM.nextInt(100) + 1;
        if (roll <= p1) return q1;
        if (roll <= p1 + p2) return q2;
        return q3;
    }

    public static Optional<String> generateLoot(ContainerVariant variant) {
        if (variant == null) {
            return Optional.empty();
        }
        LootCategory category = variant.getLootCategory();
        ItemQuality quality = rollQuality(variant.getTier());
        return GlobalLootDatabase.getInstance().pickRandomItemId(category, quality);
    }
}
