package com.deltaops.health;

import com.deltaops.DeltaOpsMod;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.Map;

/**
 * 七大部位健康系統 Capability
 * 頭、胸、腹、左臂、右臂、左腿、右腿
 * 部位歸零（黑傷）時附加對應的負面效果
 */
@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class BodyPartHealth {
    public static final Capability<BodyPartHealth> BODY_PART = CapabilityManager.get(new CapabilityToken<>() {});

    public enum Part {
        HEAD("head"), CHEST("chest"), STOMACH("stomach"),
        LEFT_ARM("left_arm"), RIGHT_ARM("right_arm"),
        LEFT_LEG("left_leg"), RIGHT_LEG("right_leg");

        public final String id;
        Part(String id) { this.id = id; }
        public static Part fromId(String id) {
            for (Part p : values()) if (p.id.equals(id)) return p;
            return CHEST;
        }
    }

    // 每個部位最大 HP = 100
    private final Map<Part, Integer> partHealth = new EnumMap<>(Part.class);
    private boolean bleeding = false;
    private int bleedTicks = 0;
    private int painkillerTicks = 0; // 止痛藥持續時間（tick）

    public BodyPartHealth() {
        for (Part p : Part.values()) partHealth.put(p, 100);
    }

    public int getHealth(Part part) { return partHealth.getOrDefault(part, 100); }
    public void setHealth(Part part, int hp) { partHealth.put(part, Math.max(0, Math.min(100, hp))); }
    public void damage(Part part, int amount) { setHealth(part, getHealth(part) - amount); }
    public void heal(Part part, int amount) { setHealth(part, getHealth(part) + amount); }
    public boolean isBlacked(Part part) { return getHealth(part) <= 0; }
    public boolean isBleeding() { return bleeding; }
    public void setBleeding(boolean b) { this.bleeding = b; }

    public boolean hasPainkiller() { return painkillerTicks > 0; }
    public void applyPainkiller(int ticks) { painkillerTicks = Math.max(painkillerTicks, ticks); }

    /**
     * 手術包：將黑傷部位恢復到 50 HP
     */
    public boolean surgery(Part part) {
        if (!isBlacked(part)) return false;
        partHealth.put(part, 50);
        return true;
    }

    /**
     * 每 tick 更新：流血扣血 + 部位黑傷懲罰
     */
    public void tick(Player player) {
        // 止痛藥倒數
        if (painkillerTicks > 0) painkillerTicks--;

        // 流血效果
        if (bleeding) {
            bleedTicks++;
            if (bleedTicks >= 40) { // 每 2 秒扣 1 HP
                player.hurt(player.damageSources().generic(), 1.0f);
                bleedTicks = 0;
            }
        }

        // 部位黑傷懲罰（若有止痛藥則免疫）
        if (!hasPainkiller()) {
            if (isBlacked(Part.LEFT_LEG) || isBlacked(Part.RIGHT_LEG)) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30, 1, false, false));
            }
            if (isBlacked(Part.LEFT_ARM) || isBlacked(Part.RIGHT_ARM)) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30, 1, false, false));
            }
            if (isBlacked(Part.STOMACH)) {
                player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 30, 1, false, false));
            }
        }
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (Part p : Part.values()) tag.putInt(p.id, partHealth.getOrDefault(p, 100));
        tag.putBoolean("bleeding", bleeding);
        tag.putInt("painkiller", painkillerTicks);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (Part p : Part.values()) partHealth.put(p, tag.getInt(p.id));
        bleeding = tag.getBoolean("bleeding");
        painkillerTicks = tag.getInt("painkiller");
    }

    // ===== Capability Provider =====
    public static class Provider implements ICapabilitySerializable<CompoundTag> {
        private final BodyPartHealth health = new BodyPartHealth();
        private final LazyOptional<BodyPartHealth> opt = LazyOptional.of(() -> health);

        @Override public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction s) {
            return cap == BODY_PART ? opt.cast() : LazyOptional.empty();
        }
        @Override public CompoundTag serializeNBT() { return health.serializeNBT(); }
        @Override public void deserializeNBT(CompoundTag n) { health.deserializeNBT(n); }
    }

    public static BodyPartHealth get(Player player) {
        return player.getCapability(BODY_PART).orElse(new BodyPartHealth());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;
        get(event.player).tick(event.player);
    }
}