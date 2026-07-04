/* Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.lobby;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GearValueEvaluator {
    public static int calculatePlayerGearValue(ServerPlayer player) {
        if (player == null) {
            return 0;
        }

        Inventory inventory = player.getInventory();
        if (inventory == null) {
            return 0;
        }

        long totalCount = 0L;
        for (ItemStack stack : inventory.items) {
            if (!stack.isEmpty()) {
                totalCount += stack.getCount();
            }
        }
        for (ItemStack stack : inventory.armor) {
            if (!stack.isEmpty()) {
                totalCount += stack.getCount();
            }
        }
        for (ItemStack stack : inventory.offhand) {
            if (!stack.isEmpty()) {
                totalCount += stack.getCount();
            }
        }

        return (int) Math.min(Integer.MAX_VALUE, totalCount * 1000L);
    }
}
