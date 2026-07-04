/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.team;

import com.deltaops.DeltaOpsMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class SpectatorHandler {
    private static final Set<UUID> ACTIVE_BATTLE_PLAYERS = ConcurrentHashMap.newKeySet();

    public static void markBattlePlayer(ServerPlayer player) {
        if (player != null) {
            ACTIVE_BATTLE_PLAYERS.add(player.getUUID());
        }
    }

    public static void clearBattlePlayer(ServerPlayer player) {
        if (player != null) {
            ACTIVE_BATTLE_PLAYERS.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        LivingEntity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer player)) {
            return;
        }

        if (!ACTIVE_BATTLE_PLAYERS.contains(player.getUUID())) {
            return;
        }

        event.setCanceled(true);
        player.setGameMode(GameType.SPECTATOR);
        player.getServer().getPlayerList().broadcastSystemMessage(
                net.minecraft.network.chat.Component.literal("§7" + player.getGameProfile().getName() + " 已陣亡，進入觀戰模式。"),
                false
        );

        for (Player candidate : player.level().players()) {
            if (candidate instanceof ServerPlayer teammate && !teammate.equals(player) && teammate.isAlive()) {
                player.setCamera(teammate);
                break;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        if (!ACTIVE_BATTLE_PLAYERS.contains(player.getUUID())) {
            return;
        }

        clearBattlePlayer(player);
        player.getInventory().clearContent();
        player.getEnderChestInventory().clearContent();
        player.getServer().getPlayerList().broadcastSystemMessage(
                net.minecraft.network.chat.Component.literal("§c" + player.getGameProfile().getName() + " 於對局中強退，戰鬥失敗。"),
                false
        );
    }
}
