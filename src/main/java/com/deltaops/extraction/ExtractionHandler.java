package com.deltaops.extraction;

import com.deltaops.DeltaOpsMod;
import com.deltaops.block.ModBlocks;
import com.deltaops.capability.ModCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

/**
 * 動態撤離點系統 - 支援世界存檔 JSON、管理員指令、Actionbar 倒數
 */
@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class ExtractionHandler {
    private static final int EXTRACTION_TICKS = 15 * 20;
    private static final Map<UUID, Integer> extracting = new HashMap<>();

    public static boolean isInExtractionZone(Player player) {
        if (player.level().isClientSide) return false;
        BlockPos p = player.blockPosition();
        for (int x = -3; x <= 3; x++)
            for (int y = -3; y <= 3; y++)
                for (int z = -3; z <= 3; z++) {
                    BlockState s = player.level().getBlockState(p.offset(x, y, z));
                    if (s.is(ModBlocks.EXTRACTION_POINT.get())) return true;
                }
        return false;
    }

    public static void onPlayerDeath(Player player) {
        if (player.level().isClientSide) return;
        player.getCapability(ModCapabilities.GRID_INVENTORY).ifPresent(inv -> {
            for (var gi : inv.getItems()) {
                ItemStack stack = gi.getItemStack();
                if (!stack.isEmpty()) player.drop(stack, true);
            }
            inv.clear();
        });
        extracting.remove(player.getUUID());
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (event.player.level().isClientSide) return;

        Player player = event.player;
        UUID uuid = player.getUUID();

        if (isInExtractionZone(player)) {
            int ticks = extracting.getOrDefault(uuid, EXTRACTION_TICKS) - 1;
            extracting.put(uuid, ticks);

            // Actionbar 顯示倒數
            if (ticks % 5 == 0 || ticks <= 20) {
                int sec = ticks / 20 + 1;
                player.displayClientMessage(
                        Component.translatable("message." + DeltaOpsMod.MOD_ID + ".extracting", sec), true);
            }

            if (ticks <= 0) {
                extracting.remove(uuid);
                if (player instanceof ServerPlayer sp) {
                    MinecraftServer server = sp.server;
                    ServerLevel overworld = server.overworld();
                    BlockPos spawn = overworld.getSharedSpawnPos();
                    sp.teleportTo(overworld, spawn.getX(), spawn.getY(), spawn.getZ(), sp.getYRot(), sp.getXRot());
                    player.displayClientMessage(
                            Component.translatable("message." + DeltaOpsMod.MOD_ID + ".extraction_success"), false);
                }
            }
        } else if (extracting.containsKey(uuid)) {
            extracting.remove(uuid);
            player.displayClientMessage(
                    Component.translatable("message." + DeltaOpsMod.MOD_ID + ".extraction_cancelled"), false);
        }
    }
}