/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.hud;

import com.deltaops.DeltaOpsMod;
import com.deltaops.team.TeamManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class TeamHudRenderer {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer localPlayer = mc.player;
        TeamManager.Team team = TeamManager.getTeamByPlayer(localPlayer);
        if (team == null) {
            return;
        }

        int x = 8;
        int y = 8;
        for (ServerPlayer member : team.members) {
            if (member == null || member.getUUID().equals(localPlayer.getUUID())) {
                continue;
            }

            int health = (int) Math.ceil(member.getHealth());
            event.getGuiGraphics().drawString(mc.font, member.getGameProfile().getName(), x, y, 0xFFFFFFFF, true);
            event.getGuiGraphics().drawString(mc.font, "HP: " + health, x, y + 10, 0xFF00FF00, true);
            y += 24;
        }
    }
}
