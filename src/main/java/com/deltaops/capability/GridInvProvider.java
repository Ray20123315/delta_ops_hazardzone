package com.deltaops.capability;

import com.deltaops.inventory.GridInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GridInvProvider implements ICapabilitySerializable<CompoundTag> {
    private final GridInventory inv;
    private final LazyOptional<GridInventory> opt;
    private final boolean secure;

    public GridInvProvider(boolean secure) {
        this.secure = secure;
        this.inv = new GridInventory(secure ? 3 : 10, secure ? 3 : 6);
        this.opt = LazyOptional.of(() -> inv);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (secure) return cap == ModCapabilities.SECURE_CONTAINER ? opt.cast() : LazyOptional.empty();
        return cap == ModCapabilities.GRID_INVENTORY ? opt.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() { return inv.serializeNBT(); }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        GridInventory loaded = GridInventory.deserializeNBT(nbt);
        inv.getItems().clear();
        inv.getItems().addAll(loaded.getItems());
    }
}