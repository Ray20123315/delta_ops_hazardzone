package com.deltaops.inventory;

import com.deltaops.item.ItemSizeHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 網格化背包核心 - 使用二維陣列記錄物品佔用狀態
 */
public class GridInventory {
    private final int width, height;
    private final boolean[][] grid;
    private final List<GridItemStack> items = new ArrayList<>();

    public GridInventory(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new boolean[width][height];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public List<GridItemStack> getItems() { return items; }

    public boolean canPlace(ItemStack stack, int x, int y, boolean rotated) {
        int w = rotated ? ItemSizeHelper.getHeight(stack) : ItemSizeHelper.getWidth(stack);
        int h = rotated ? ItemSizeHelper.getWidth(stack) : ItemSizeHelper.getHeight(stack);
        return canFit(x, y, w, h);
    }

    private boolean canFit(int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > width || y + h > height) return false;
        for (int dx = 0; dx < w; dx++)
            for (int dy = 0; dy < h; dy++)
                if (grid[x + dx][y + dy]) return false;
        return true;
    }

    public boolean placeItem(ItemStack stack, int x, int y, boolean rotated) {
        if (!canPlace(stack, x, y, rotated)) return false;
        int w = rotated ? ItemSizeHelper.getHeight(stack) : ItemSizeHelper.getWidth(stack);
        int h = rotated ? ItemSizeHelper.getWidth(stack) : ItemSizeHelper.getHeight(stack);
        for (int dx = 0; dx < w; dx++)
            for (int dy = 0; dy < h; dy++)
                grid[x + dx][y + dy] = true;
        items.add(new GridItemStack(stack, x, y, rotated));
        return true;
    }

    @Nullable
    public GridItemStack removeItem(int x, int y) {
        var found = findItemAt(x, y);
        if (found.isEmpty()) return null;
        GridItemStack g = found.get();
        int w = g.getEffectiveWidth(), h = g.getEffectiveHeight();
        for (int dx = 0; dx < w; dx++)
            for (int dy = 0; dy < h; dy++)
                grid[g.getGridX() + dx][g.getGridY() + dy] = false;
        items.remove(g);
        return g;
    }

    public boolean rotateItem(int x, int y) {
        var found = findItemAt(x, y);
        if (found.isEmpty()) return false;
        GridItemStack g = found.get();
        clearGrid(g);
        boolean newRot = !g.isRotated();
        int w = newRot ? ItemSizeHelper.getHeight(g.getItemStack()) : ItemSizeHelper.getWidth(g.getItemStack());
        int h = newRot ? ItemSizeHelper.getWidth(g.getItemStack()) : ItemSizeHelper.getHeight(g.getItemStack());
        int nx = Math.min(g.getGridX(), width - w);
        int ny = Math.min(g.getGridY(), height - h);
        if (!canFit(nx, ny, w, h)) {
            placeItem(g.getItemStack(), g.getGridX(), g.getGridY(), g.isRotated());
            return false;
        }
        g.setGridX(nx); g.setGridY(ny); g.setRotated(newRot);
        for (int dx = 0; dx < w; dx++)
            for (int dy = 0; dy < h; dy++)
                grid[nx + dx][ny + dy] = true;
        return true;
    }

    private void clearGrid(GridItemStack g) {
        int w = g.getEffectiveWidth(), h = g.getEffectiveHeight();
        for (int dx = 0; dx < w; dx++)
            for (int dy = 0; dy < h; dy++)
                grid[g.getGridX() + dx][g.getGridY() + dy] = false;
    }

    public Optional<GridItemStack> findItemAt(int x, int y) {
        return items.stream().filter(it -> {
            int w = it.getEffectiveWidth(), h = it.getEffectiveHeight();
            return x >= it.getGridX() && x < it.getGridX() + w && y >= it.getGridY() && y < it.getGridY() + h;
        }).findFirst();
    }

    public List<ItemStack> clear() {
        List<ItemStack> dropped = new ArrayList<>();
        items.forEach(i -> dropped.add(i.getItemStack()));
        items.clear();
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++)
                grid[x][y] = false;
        return dropped;
    }

    public boolean isEmpty() { return items.isEmpty(); }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("w", width); tag.putInt("h", height);
        ListTag list = new ListTag();
        items.forEach(i -> list.add(i.serializeNBT()));
        tag.put("items", list);
        return tag;
    }

    public static GridInventory deserializeNBT(CompoundTag tag) {
        GridInventory inv = new GridInventory(tag.getInt("w"), tag.getInt("h"));
        ListTag list = tag.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            GridItemStack g = GridItemStack.deserializeNBT(list.getCompound(i));
            inv.items.add(g);
            int w = g.getEffectiveWidth(), h = g.getEffectiveHeight();
            for (int dx = 0; dx < w; dx++)
                for (int dy = 0; dy < h; dy++)
                    inv.grid[g.getGridX() + dx][g.getGridY() + dy] = true;
        }
        return inv;
    }
}