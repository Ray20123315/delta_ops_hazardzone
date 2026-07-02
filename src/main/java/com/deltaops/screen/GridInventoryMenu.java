package com.deltaops.screen;

import com.deltaops.capability.ModCapabilities;
import com.deltaops.inventory.GridInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GridInventoryMenu extends AbstractContainerMenu {
    private final GridInventory gridInv;
    private final boolean secure;
    private static final int SLOT = 18, GAP = 14;

    public GridInventoryMenu(int id, Inventory pi, boolean secure) {
        super(secure ? ModMenuTypes.SECURE.get() : ModMenuTypes.GRID.get(), id);
        this.secure = secure;
        this.gridInv = pi.player.getCapability(secure ? ModCapabilities.SECURE_CONTAINER : ModCapabilities.GRID_INVENTORY)
                .orElse(new GridInventory(10, 6));

        int rows = secure ? 3 : 6;
        int invY = 18 + rows * SLOT + GAP;

        for (int r = 0; r < 3; r++)
            for (int c = 0; c < 9; c++)
                addSlot(new Slot(pi, c + r * 9 + 9, 8 + c * SLOT, invY + r * SLOT));
        for (int c = 0; c < 9; c++)
            addSlot(new Slot(pi, c, 8 + c * SLOT, invY + 3 * SLOT + 4));
    }

    public GridInventory getGridInventory() { return gridInv; }
    public boolean isSecureContainer() { return secure; }

    @Override @NotNull public ItemStack quickMoveStack(@NotNull Player p, int i) { return ItemStack.EMPTY; }
    @Override public boolean stillValid(@NotNull Player p) { return true; }
}