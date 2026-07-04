/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loading;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class TacticalLoadingScreen extends Screen {
    private final int durationTicks;
    private final long startTimeMillis;

    public TacticalLoadingScreen(int durationTicks) {
        super(Component.literal("Tactical Loading"));
        this.durationTicks = Math.max(1, durationTicks);
        this.startTimeMillis = System.currentTimeMillis();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.fill(0, 0, this.width, this.height, 0xFF000000);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        Component title = Component.literal("§e§lTACTICAL LOADING...");
        Component subtitle = Component.literal("§7正在載入作戰區域物資，請稍候...");

        int titleWidth = this.font.width(title.getString().replaceAll("§[0-9a-fk-or]", ""));
        int subtitleWidth = this.font.width(subtitle.getString().replaceAll("§[0-9a-fk-or]", ""));

        guiGraphics.drawString(this.font, title, centerX - titleWidth / 2, centerY - 20, 0xFFFFEE00, true);
        guiGraphics.drawString(this.font, subtitle, centerX - subtitleWidth / 2, centerY + 4, 0xFFAAAAAA, true);

        long elapsed = System.currentTimeMillis() - this.startTimeMillis;
        int pulse = (int) ((elapsed / 120L) % 8L);
        String spinner = switch (pulse) {
            case 0 -> "◐";
            case 1 -> "◓";
            case 2 -> "◑";
            case 3 -> "◒";
            case 4 -> "◐";
            case 5 -> "◓";
            case 6 -> "◑";
            default -> "◒";
        };

        int spinnerWidth = this.font.width(spinner);
        guiGraphics.drawString(this.font, Component.literal("§7" + spinner), centerX - spinnerWidth / 2, centerY + 36, 0xFFFFFFFF, true);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    public int getDurationTicks() {
        return this.durationTicks;
    }
}
