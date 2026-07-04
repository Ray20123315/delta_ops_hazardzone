package com.deltaops.securebox;

import com.deltaops.DeltaOpsMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class SecureBoxCapabilityManager {
    private static final String TAG = "SecureBoxData";
    private static final String TAG_LEVEL = "SecureBoxLevel";

    public static ItemStackHandler getSecureBoxHandler(ServerPlayer player) {
        if (player == null) {
            return new ItemStackHandler(2);
        }

        int level = getSecureBoxLevel(player);
        int slots = getSlotCount(level);
        ItemStackHandler handler = new ItemStackHandler(slots);
        CompoundTag tag = player.getPersistentData().getCompound(TAG);
        if (tag.contains("Items", Tag.TAG_COMPOUND)) {
            handler.deserializeNBT(tag.getCompound("Items"));
        }
        return handler;
    }

    public static void saveSecureBoxHandler(ServerPlayer player, ItemStackHandler handler) {
        if (player == null || handler == null) {
            return;
        }

        CompoundTag tag = new CompoundTag();
        tag.put("Items", handler.serializeNBT());
        tag.putInt(TAG_LEVEL, getSecureBoxLevel(player));
        player.getPersistentData().put(TAG, tag);
    }

    public static int getSecureBoxLevel(ServerPlayer player) {
        if (player == null) {
            return 1;
        }

        CompoundTag data = player.getPersistentData();
        return Math.max(1, Math.min(4, data.getInt("SecureBoxLevel")));
    }

    public static void setSecureBoxLevel(ServerPlayer player, int level) {
        if (player == null) {
            return;
        }
        player.getPersistentData().putInt("SecureBoxLevel", Math.max(1, Math.min(4, level)));
    }

    public static void copySecureBoxToNewPlayer(Player oldPlayer, Player newPlayer) {
        if (oldPlayer == null || newPlayer == null) {
            return;
        }

        CompoundTag oldTag = oldPlayer.getPersistentData().getCompound(TAG);
        CompoundTag newTag = new CompoundTag();
        newTag.put("Items", oldTag.getCompound("Items"));
        newTag.putInt(TAG_LEVEL, oldTag.getInt(TAG_LEVEL));
        newPlayer.getPersistentData().put(TAG, newTag);
    }

    public static int getSlotCount(int level) {
        return switch (level) {
            case 2 -> 4;
            case 3 -> 6;
            case 4 -> 9;
            default -> 2;
        };
    }
}
