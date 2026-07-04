/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.container;

import com.deltaops.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

public class TacticalContainerBlockEntity extends BlockEntity implements MenuProvider {
    private ContainerVariant variant;
    private ItemStackHandler mainInventory;
    private ItemStackHandler hiddenInventory;
    private boolean hiddenLayerUnlocked;

    public TacticalContainerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TACTICAL_CONTAINER_BLOCK_ENTITY.get(), pos, state);
        this.variant = state.hasProperty(TacticalContainerBlock.VARIANT)
                ? state.getValue(TacticalContainerBlock.VARIANT)
                : ContainerVariant.LARGE_SAFE;
        this.mainInventory = new ItemStackHandler(this.variant.getWidth() * this.variant.getHeight());
        this.hiddenInventory = this.variant.supportsHiddenLayer() ? new ItemStackHandler(3) : new ItemStackHandler(0);
        this.hiddenLayerUnlocked = false;
    }

    public ContainerVariant getVariant() {
        return this.variant != null ? this.variant : ContainerVariant.LARGE_SAFE;
    }

    public void setVariant(ContainerVariant variant) {
        if (variant == null) {
            return;
        }

        this.variant = variant;
        ItemStackHandler previousMain = this.mainInventory;
        ItemStackHandler previousHidden = this.hiddenInventory;
        this.mainInventory = new ItemStackHandler(this.variant.getWidth() * this.variant.getHeight());
        this.hiddenInventory = this.variant.supportsHiddenLayer() ? new ItemStackHandler(3) : new ItemStackHandler(0);
        this.hiddenLayerUnlocked = false;

        for (int index = 0; index < Math.min(previousMain.getSlots(), this.mainInventory.getSlots()); index++) {
            ItemStack stack = previousMain.getStackInSlot(index);
            if (!stack.isEmpty()) {
                this.mainInventory.setStackInSlot(index, stack.copy());
            }
        }

        if (this.variant.supportsHiddenLayer()) {
            for (int index = 0; index < Math.min(previousHidden.getSlots(), this.hiddenInventory.getSlots()); index++) {
                ItemStack stack = previousHidden.getStackInSlot(index);
                if (!stack.isEmpty()) {
                    this.hiddenInventory.setStackInSlot(index, stack.copy());
                }
            }
        }

        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public void populateLootIfEmpty() {
        if (this.level == null || this.level.isClientSide) {
            return;
        }
        if (this.mainInventory.getSlots() == 0) {
            return;
        }
        boolean empty = true;
        for (int i = 0; i < this.mainInventory.getSlots(); i++) {
            if (!this.mainInventory.getStackInSlot(i).isEmpty()) {
                empty = false;
                break;
            }
        }
        if (!empty) {
            return;
        }

        java.util.Optional<String> picked = com.deltaops.loot.TacticalLootMatrix.generateLoot(this.getVariant());
        if (picked.isEmpty()) {
            return;
        }

        net.minecraft.resources.ResourceLocation itemId = new net.minecraft.resources.ResourceLocation(picked.get());
        net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(itemId);
        if (item == null || item == net.minecraft.world.item.Items.AIR) {
            return;
        }

        this.mainInventory.setStackInSlot(0, new net.minecraft.world.item.ItemStack(item, 1));
        setChanged();
        if (this.level != null) {
            this.level.sendBlockUpdated(this.worldPosition, this.getBlockState(), this.getBlockState(), 3);
        }
    }

    public ItemStackHandler getMainInventory() {
        return this.mainInventory;
    }

    public ItemStackHandler getHiddenInventory() {
        return this.hiddenInventory;
    }

    public boolean isHiddenLayerUnlocked() {
        return this.hiddenLayerUnlocked;
    }

    public void setHiddenLayerUnlocked(boolean hiddenLayerUnlocked) {
        this.hiddenLayerUnlocked = hiddenLayerUnlocked;
        setChanged();
    }

    public boolean supportsHiddenLayer() {
        return this.variant != null && this.variant.supportsHiddenLayer();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString("variant", this.getVariant().getSerializedName());
        tag.putBoolean("hiddenLayerUnlocked", this.hiddenLayerUnlocked);
        tag.put("mainInventory", this.mainInventory.serializeNBT());
        if (this.hiddenInventory.getSlots() > 0) {
            tag.put("hiddenInventory", this.hiddenInventory.serializeNBT());
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.variant = ContainerVariant.fromName(tag.getString("variant"));
        this.mainInventory = new ItemStackHandler(this.variant.getWidth() * this.variant.getHeight());
        this.hiddenInventory = this.variant.supportsHiddenLayer() ? new ItemStackHandler(3) : new ItemStackHandler(0);
        this.hiddenLayerUnlocked = tag.getBoolean("hiddenLayerUnlocked");

        if (tag.contains("mainInventory")) {
            this.mainInventory.deserializeNBT(tag.getCompound("mainInventory"));
        }
        if (this.hiddenInventory.getSlots() > 0 && tag.contains("hiddenInventory")) {
            this.hiddenInventory.deserializeNBT(tag.getCompound("hiddenInventory"));
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal(this.getVariant().getDisplayName());
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new TacticalContainerMenu(id, inventory, this.getVariant(), this.mainInventory, this.hiddenInventory, this.hiddenLayerUnlocked);
    }
}
