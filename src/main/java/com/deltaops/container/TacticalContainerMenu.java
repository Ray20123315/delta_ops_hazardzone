/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class TacticalContainerMenu extends AbstractContainerMenu {
    private final ContainerVariant variant;
    private final ItemStackHandler mainInventory;
    private final ItemStackHandler hiddenInventory;
    private final boolean hiddenLayerUnlocked;

    public TacticalContainerMenu(int containerId, Inventory playerInventory, ContainerVariant variant,
                                 ItemStackHandler mainInventory, ItemStackHandler hiddenInventory,
                                 boolean hiddenLayerUnlocked) {
        super(null, containerId);
        this.variant = variant != null ? variant : ContainerVariant.LARGE_SAFE;
        this.mainInventory = mainInventory != null ? mainInventory : new ItemStackHandler(this.variant.getWidth() * this.variant.getHeight());
        this.hiddenInventory = hiddenInventory != null ? hiddenInventory : (this.variant.supportsHiddenLayer() ? new ItemStackHandler(3) : new ItemStackHandler(0));
        this.hiddenLayerUnlocked = hiddenLayerUnlocked;

        this.addMainInventorySlots();
        this.addHiddenInventorySlots();
        this.addPlayerInventorySlots(playerInventory);
    }

    private void addMainInventorySlots() {
        int width = this.variant.getWidth();
        int height = this.variant.getHeight();
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int index = row * width + col;
                int x = 8 + col * 18;
                int y = 20 + row * 18;
                this.addSlot(new SlotItemHandler(this.mainInventory, index, x, y));
            }
        }
    }

    private void addHiddenInventorySlots() {
        if (!this.hiddenLayerUnlocked || !this.variant.supportsHiddenLayer()) {
            return;
        }

        int baseX = 8 + this.variant.getWidth() * 18 + 20;
        for (int index = 0; index < 3; index++) {
            this.addSlot(new SlotItemHandler(this.hiddenInventory, index, baseX, 20 + index * 18));
        }
    }

    private void addPlayerInventorySlots(Inventory playerInventory) {
        int startX = 8;
        int startY = 20 + this.variant.getHeight() * 18 + 16;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col + 9;
                int x = startX + col * 18;
                int y = startY + row * 18;
                this.addSlot(new Slot(playerInventory, slotIndex, x, y));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, startX + col * 18, startY + 58));
        }
    }

    public ContainerVariant getVariant() {
        return this.variant;
    }

    public boolean isHiddenLayerUnlocked() {
        return this.hiddenLayerUnlocked;
    }

    public int getMainSlotCount() {
        return this.mainInventory.getSlots();
    }

    public int getHiddenSlotCount() {
        return this.hiddenInventory.getSlots();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            int containerSlots = this.mainInventory.getSlots() + this.hiddenInventory.getSlots();
            if (index < containerSlots) {
                if (!this.moveItemStackTo(stack, containerSlots, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, containerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }
}
