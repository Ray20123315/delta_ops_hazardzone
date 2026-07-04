package com.deltaops.combat;

import com.deltaops.DeltaOpsMod;
import com.deltaops.config.ModConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityTravelToDimensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class ServerCombatValueManager {

    private static final Map<ResourceLocation, Integer> DEFAULT_COMBAT_VALUES = new HashMap<>();

    static {
        DEFAULT_COMBAT_VALUES.put(ResourceLocation.fromNamespaceAndPath("tacz", "assault_rifle"), 25_000);
        DEFAULT_COMBAT_VALUES.put(ResourceLocation.fromNamespaceAndPath("tacz", "sniper_rifle"), 40_000);
        DEFAULT_COMBAT_VALUES.put(ResourceLocation.fromNamespaceAndPath("delta_ops_hazardzone", "medkit"), 1_500);
        DEFAULT_COMBAT_VALUES.put(ResourceLocation.fromNamespaceAndPath("delta_ops_hazardzone", "surgery_kit"), 3_000);
        DEFAULT_COMBAT_VALUES.put(ResourceLocation.fromNamespaceAndPath("delta_ops_hazardzone", "tactical_backpack"), 12_000);
        DEFAULT_COMBAT_VALUES.put(ResourceLocation.fromNamespaceAndPath("delta_ops_hazardzone", "gold_bar"), 10_000);
    }

    public static long calculateServerSideValue(ServerPlayer player) {
        if (player == null) {
            return 0;
        }

        long totalValue = 0L;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.isEmpty()) {
                continue;
            }

            Item item = stack.getItem();
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id == null) {
                continue;
            }

            if ("minecraft".equals(id.getNamespace())) {
                continue;
            }

            totalValue += getCombatValue(stack, id);
        }

        return totalValue;
    }

    private static int getCombatValue(ItemStack stack, ResourceLocation itemId) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CombatValue", Tag.TAG_INT)) {
            return Math.max(0, tag.getInt("CombatValue"));
        }

        return DEFAULT_COMBAT_VALUES.getOrDefault(itemId, 0);
    }

    @SubscribeEvent
    public static void onEntityTravelToDimension(EntityTravelToDimensionEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        long combatValue = calculateServerSideValue(player);
        long requiredValue = ModConfig.DAM_REQUIRED_VALUE.get();
        if (combatValue < requiredValue) {
            event.setCanceled(true);
            player.displayClientMessage(
                    Component.literal("§c你的自訂物資戰備值不足，無法進入該地圖維度。"),
                    false
            );
        }
    }
}
