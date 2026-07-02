package com.deltaops.inventory;

import com.deltaops.item.ItemSizeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class GridItemStack {
    private ItemStack itemStack;
    private int gridX, gridY;
    private boolean rotated;

    public GridItemStack(ItemStack stack, int x, int y) { this(stack, x, y, false); }

    public GridItemStack(ItemStack stack, int x, int y, boolean rotated) {
        this.itemStack = stack;
        this.gridX = x;
        this.gridY = y;
        this.rotated = rotated;
    }

    public ItemStack getItemStack() { return itemStack; }
    public void setItemStack(ItemStack s) { this.itemStack = s; }
    public int getGridX() { return gridX; }
    public void setGridX(int x) { this.gridX = x; }
    public int getGridY() { return gridY; }
    public void setGridY(int y) { this.gridY = y; }
    public boolean isRotated() { return rotated; }
    public void setRotated(boolean r) { this.rotated = r; }

    public int getEffectiveWidth() {
        int w = ItemSizeHelper.getWidth(itemStack);
        int h = ItemSizeHelper.getHeight(itemStack);
        return rotated ? h : w;
    }

    public int getEffectiveHeight() {
        int w = ItemSizeHelper.getWidth(itemStack);
        int h = ItemSizeHelper.getHeight(itemStack);
        return rotated ? w : h;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("item", itemStack.save(new CompoundTag()));
        tag.putInt("x", gridX);
        tag.putInt("y", gridY);
        tag.putBoolean("rotated", rotated);
        return tag;
    }

    public static GridItemStack deserializeNBT(CompoundTag tag) {
        return new GridItemStack(
                ItemStack.of(tag.getCompound("item")),
                tag.getInt("x"), tag.getInt("y"),
                tag.getBoolean("rotated"));
    }
}