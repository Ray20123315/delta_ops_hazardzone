package com.deltaops.item;

import com.deltaops.DeltaOpsMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * NBT 尺寸輔助工具 - 所有物品的網格尺寸與稀有度儲存在 NBT 中
 * 未設定尺寸的物品防呆預設為 1x1，稀有度 0（白）
 */
public class ItemSizeHelper {
    private static final String KEY_WIDTH = DeltaOpsMod.MOD_ID + ":width";
    private static final String KEY_HEIGHT = DeltaOpsMod.MOD_ID + ":height";
    private static final String KEY_RARITY = DeltaOpsMod.MOD_ID + ":rarity";

    public static int getWidth(ItemStack stack) {
        if (stack.isEmpty()) return 1;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_WIDTH)) return tag.getInt(KEY_WIDTH);
        return 1;
    }

    public static int getHeight(ItemStack stack) {
        if (stack.isEmpty()) return 1;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_HEIGHT)) return tag.getInt(KEY_HEIGHT);
        return 1;
    }

    public static int getRarityLevel(ItemStack stack) {
        if (stack.isEmpty()) return 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY_RARITY)) return tag.getInt(KEY_RARITY);
        return 0;
    }

    public static void setSize(ItemStack stack, int width, int height) {
        stack.getOrCreateTag().putInt(KEY_WIDTH, Math.max(1, width));
        stack.getOrCreateTag().putInt(KEY_HEIGHT, Math.max(1, height));
    }

    public static void setRarity(ItemStack stack, int rarity) {
        stack.getOrCreateTag().putInt(KEY_RARITY, Math.max(0, Math.min(4, rarity)));
    }

    public static boolean hasSize(ItemStack stack) {
        if (stack.isEmpty()) return false;
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains(KEY_WIDTH) && tag.contains(KEY_HEIGHT);
    }
}