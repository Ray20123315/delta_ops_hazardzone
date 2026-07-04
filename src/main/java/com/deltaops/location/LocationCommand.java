/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.location;

import com.deltaops.DeltaOpsMod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID)
public class LocationCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> d = event.getDispatcher();

        var lobbySet = Commands.literal("lobby").then(Commands.literal("set").requires(src -> src.hasPermission(2)).executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayerOrException();
            var lvl = p.level();
            LocationDatabase.setLobby((net.minecraft.server.level.ServerLevel) lvl, p.blockPosition(), p.getYRot(), p.getXRot());
            p.sendSystemMessage(Component.literal("大廳出生點已成功部署"));
            return 1;
        }));

        var spawnAdd = Commands.literal("add").then(Commands.argument("mapName", StringArgumentType.string()).requires(src -> src.hasPermission(2)).executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayerOrException();
            String map = StringArgumentType.getString(ctx, "mapName");
            LocationDatabase.addSpawnPoint(map, p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot());
            p.sendSystemMessage(Component.literal("已將當前位置加入地圖 '" + map + "' 的出生點列表。"));
            return 1;
        }));

        var spawnList = Commands.literal("list").then(Commands.argument("mapName", StringArgumentType.string()).requires(src -> src.hasPermission(2)).executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayerOrException();
            String map = StringArgumentType.getString(ctx, "mapName");
            List<LocationDatabase.SpawnPoint> pts = LocationDatabase.listSpawnPoints(map);
            if (pts.isEmpty()) {
                p.sendSystemMessage(Component.literal("地圖 '" + map + "' 無出生點。"));
                return 1;
            }
            int idx = 0;
            for (LocationDatabase.SpawnPoint sp : pts) {
                p.sendSystemMessage(Component.literal("[" + idx + "] x=" + sp.x + " y=" + sp.y + " z=" + sp.z + " yaw=" + sp.yaw + " pitch=" + sp.pitch));
                idx++;
            }
            return 1;
        }));

        var spawnRemove = Commands.literal("remove").then(Commands.argument("mapName", StringArgumentType.string()).then(Commands.argument("index", IntegerArgumentType.integer()).requires(src -> src.hasPermission(2)).executes(ctx -> {
            ServerPlayer p = ctx.getSource().getPlayerOrException();
            String map = StringArgumentType.getString(ctx, "mapName");
            int idx = IntegerArgumentType.getInteger(ctx, "index");
            boolean ok = LocationDatabase.removeSpawnPoint(map, idx);
            p.sendSystemMessage(Component.literal(ok ? "已移除" : "索引錯誤或沒有該地圖"));
            return ok ? 1 : 0;
        })));

        var spawnRoot = Commands.literal("spawn").then(spawnAdd).then(spawnList).then(spawnRemove);

        d.register(Commands.literal("dt").then(lobbySet).then(spawnRoot));
    }
}
