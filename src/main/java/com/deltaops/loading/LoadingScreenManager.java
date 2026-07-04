/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loading;

import com.deltaops.DeltaOpsMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LoadingScreenManager {
    private static TacticalLoadingScreen activeScreen;
    private static int remainingTicks;

    public static void showLoadingScreen(int durationTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }

        remainingTicks = Math.max(1, durationTicks);
        activeScreen = new TacticalLoadingScreen(durationTicks);
        mc.setScreen(activeScreen);
    }

    public static void hideLoadingScreen() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return;
        }

        if (mc.screen == activeScreen) {
            mc.setScreen(null);
        }
        activeScreen = null;
        remainingTicks = 0;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (activeScreen == null) {
            return;
        }

        if (remainingTicks > 0) {
            remainingTicks--;
        }

        if (remainingTicks <= 0) {
            hideLoadingScreen();
        }
    }
}
