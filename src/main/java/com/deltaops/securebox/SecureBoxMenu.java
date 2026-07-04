package com.deltaops.securebox;

import com.deltaops.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SecureBoxMenu extends AbstractContainerMenu {
    private final ItemStackHandler secureBoxHandler;
    private final Player player;

    public SecureBoxMenu(int id, Inventory inventory, ItemStackHandler handler) {
        super(ModMenuTypes.SECURE_BOX.get(), id);
        this.secureBoxHandler = handler;
        this.player = inventory.player;

        int rows = 2;
        int slots = handler.getSlots();
        if (slots <= 2) {
            rows = 1;
        } else if (slots <= 6) {
            rows = 2;
        } else {
            rows = 3;
        }

        int xOffset = 8;
        int yOffset = 18;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                if (index >= slots) {
                    break;
                }
                addSlot(new SlotItemHandler(handler, index, xOffset + col * 18, yOffset + row * 18));
            }
        }

        int playerInvY = yOffset + rows * 18 + 20;
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(inventory, col + row * 9 + 9, xOffset + col * 18, playerInvY + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(inventory, col, xOffset + col * 18, playerInvY + 58));
        }
    }

    public static SecureBoxMenu fromNetwork(int id, Inventory inventory, FriendlyByteBuf buf) {
        return new SecureBoxMenu(id, inventory, SecureBoxCapabilityManager.getSecureBoxHandler((net.minecraft.server.level.ServerPlayer) inventory.player));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();
            if (index < secureBoxHandler.getSlots()) {
                if (!this.moveItemStackTo(stack, secureBoxHandler.getSlots(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stack, 0, secureBoxHandler.getSlots(), false)) {
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

    @Override
    public void removed(Player player) {
        super.removed(player);
        if (!player.level().isClientSide && player instanceof ServerPlayer serverPlayer) {
            SecureBoxCapabilityManager.saveSecureBoxHandler(serverPlayer, secureBoxHandler);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
