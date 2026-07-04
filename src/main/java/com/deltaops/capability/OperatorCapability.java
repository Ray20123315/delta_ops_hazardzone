package com.deltaops.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// 已經拔掉打架的 EventBusSubscriber 了
public class OperatorCapability {
    
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
            // 從統一管理的 ModCapabilities 拿取 OPERATOR_DATA
            return cap == ModCapabilities.OPERATOR_DATA ? optional.cast() : LazyOptional.empty();
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