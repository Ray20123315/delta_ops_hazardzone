package com.deltaops.capability;

import com.deltaops.DeltaOpsMod;
import com.deltaops.operator.Operator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class OperatorCapability {
    public static final Capability<OperatorData> OPERATOR_DATA = CapabilityManager.get(new CapabilityToken<>() {});
    public static final ResourceLocation OPERATOR_KEY = new ResourceLocation(DeltaOpsMod.MOD_ID, "operator");

    @SubscribeEvent
    public static void onRegister(RegisterCapabilitiesEvent e) {
        e.register(OperatorData.class);
    }

    @SubscribeEvent
    public static void onAttach(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof Player) {
            e.addCapability(OPERATOR_KEY, new Provider());
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone e) {
        if (!e.isWasDeath()) return;
        Player original = e.getOriginal();
        Player clone = e.getEntity();
        original.reviveCaps();
        clone.reviveCaps();
        try {
            var src = original.getCapability(OPERATOR_DATA).orElse(new OperatorData());
            var dst = clone.getCapability(OPERATOR_DATA).orElse(new OperatorData());
            dst.setSelectedOperatorId(src.getSelectedOperatorId());
        } finally {
            original.invalidateCaps();
            clone.invalidateCaps();
        }
    }

    public static class OperatorData {
        private String selectedOperatorId = "";

        public String getSelectedOperatorId() {
            return selectedOperatorId;
        }

        public void setSelectedOperatorId(String selectedOperatorId) {
            this.selectedOperatorId = selectedOperatorId;
        }

        public boolean hasSelectedOperator() {
            return selectedOperatorId != null && !selectedOperatorId.isEmpty();
        }
    }

    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final OperatorData data = new OperatorData();
        private final LazyOptional<OperatorData> optional = LazyOptional.of(() -> data);

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            return cap == OPERATOR_DATA ? optional.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag = new CompoundTag();
            tag.putString("selectedOperatorId", data.getSelectedOperatorId());
            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            data.setSelectedOperatorId(nbt.getString("selectedOperatorId"));
        }
    }
}