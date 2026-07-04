package com.deltaops.block.entity;

import com.deltaops.DeltaOpsMod;
import com.deltaops.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LootCrateBlockEntity extends BlockEntity implements MenuProvider {
    private final SimpleContainer inv = new SimpleContainer(25);
    private boolean generated = false;

    public LootCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LOOT_CRATE.get(), pos, state);
    }

    public void generateLoot() {
        if (generated || level == null || level.isClientSide) return;
        generated = true;
        RandomSource r = level.getRandom();
        int count = 3 + r.nextInt(4);
        for (int i = 0; i < count; i++) {
            ItemStack stack = randomItem(r);
            if (stack.isEmpty()) continue;
            for (int a = 0; a < 25; a++) {
                int slot = r.nextInt(25);
                if (inv.getItem(slot).isEmpty()) {
                    inv.setItem(slot, stack);
                    break;
                }
            }
        }
        setChanged();
    }

    private ItemStack randomItem(RandomSource r) {
        int roll = r.nextInt(100);
        ItemStack stack;
        if (roll < 20) stack = new ItemStack(ModItems.BANDAGE.get());
        else if (roll < 35) stack = new ItemStack(ModItems.PISTOL_MAG.get());
        else if (roll < 45) stack = new ItemStack(ModItems.MEDKIT.get());
        else if (roll < 55) stack = new ItemStack(ModItems.RIFLE_MAG.get());
        else if (roll < 65) stack = new ItemStack(ModItems.MEDICAL_SUPPLIES.get());
        else if (roll < 75) stack = new ItemStack(ModItems.GOLD_BAR.get());
        else if (roll < 85) stack = new ItemStack(ModItems.ASSAULT_RIFLE.get());
        else if (roll < 92) stack = new ItemStack(ModItems.RELIC.get());
        else stack = new ItemStack(ModItems.RELIC.get());
        return stack;
    }

    @Override protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        CompoundTag invTag = new CompoundTag();
        for (int i = 0; i < 25; i++) {
            ItemStack stack = inv.getItem(i);
            if (!stack.isEmpty()) {
                invTag.put(String.valueOf(i), stack.save(new CompoundTag()));
            }
        }
        tag.put("inv", invTag);
        tag.putBoolean("gen", generated);
    }

    @Override public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        CompoundTag invTag = tag.getCompound("inv");
        for (int i = 0; i < 25; i++) {
            if (invTag.contains(String.valueOf(i))) {
                inv.setItem(i, ItemStack.of(invTag.getCompound(String.valueOf(i))));
            } else {
                inv.setItem(i, ItemStack.EMPTY);
            }
        }
        generated = tag.getBoolean("gen");
    }

    @Override public @NotNull Component getDisplayName() {
        return Component.translatable("container." + DeltaOpsMod.MOD_ID + ".loot_crate");
    }

    @Nullable @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory pi, @NotNull Player p) {
        generateLoot();
        return ChestMenu.threeRows(id, pi, inv);
    }
}
