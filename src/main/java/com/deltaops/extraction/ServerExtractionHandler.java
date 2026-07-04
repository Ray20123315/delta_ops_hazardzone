package com.deltaops.extraction;

import com.deltaops.DeltaOpsMod;
import com.deltaops.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class ServerExtractionHandler {
    public static boolean isServerLeverPulled = false;
    public static int serverGateTimer = 0;

    private static final int EXTRACTION_TICKS = 15 * 20;
    private static final Map<UUID, Integer> serverExtractionTimers = new HashMap<>();

    public static boolean isInExtractionZone(Player player) {
        if (player.level().isClientSide) {
            return false;
        }

        if (ModBlocks.EXTRACTION_POINT == null || !ModBlocks.EXTRACTION_POINT.isPresent()) {
            return false;
        }

        Block extractionBlock = ModBlocks.EXTRACTION_POINT.get();
        BlockPos p = player.blockPosition();
        for (int x = -3; x <= 3; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -3; z <= 3; z++) {
                    if (player.level().getBlockState(p.offset(x, y, z)).is(extractionBlock)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide) {
            return;
        }

        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);
        if (state.is(Blocks.LEVER) || (ModBlocks.EXTRACTION_POINT.isPresent() && state.is(ModBlocks.EXTRACTION_POINT.get()))) {
            isServerLeverPulled = true;
            serverGateTimer = 3600;
            if (event.getEntity() instanceof ServerPlayer player) {
                player.displayClientMessage(
                        Component.literal("§a拉閘已啟動，撤離倒數將於 15 秒後開始。"),
                        true
                );
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (serverGateTimer > 0) {
            serverGateTimer--;
        }

        if (serverGateTimer <= 0) {
            serverGateTimer = 0;
            isServerLeverPulled = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (event.player.level().isClientSide) {
            return;
        }

        Player player = event.player;
        UUID uuid = player.getUUID();

        if (isInExtractionZone(player) && isServerLeverPulled) {
            int ticks = serverExtractionTimers.getOrDefault(uuid, EXTRACTION_TICKS) - 1;
            serverExtractionTimers.put(uuid, ticks);

            if (ticks % 20 == 0 || ticks <= 20) {
                int seconds = ticks / 20 + 1;
                player.displayClientMessage(
                        Component.literal("§e撤離中：" + seconds + " 秒"),
                        true
                );
            }

            if (ticks <= 0) {
                serverExtractionTimers.remove(uuid);
                if (player instanceof ServerPlayer sp) {
                    MinecraftServer server = sp.server;
                    ServerLevel overworld = server.overworld();
                    BlockPos spawn = overworld.getSharedSpawnPos();
                    sp.teleportTo(overworld, spawn.getX() + 0.5D, spawn.getY(), spawn.getZ() + 0.5D, sp.getYRot(), sp.getXRot());
                    player.displayClientMessage(
                            Component.literal("§a撤離成功，已返回出生點。"),
                            false
                    );
                }
            }
        } else if (serverExtractionTimers.containsKey(uuid)) {
            serverExtractionTimers.remove(uuid);
            player.displayClientMessage(
                    Component.literal("§c撤離倒數已中斷。"),
                    true
            );
        }
    }
}
