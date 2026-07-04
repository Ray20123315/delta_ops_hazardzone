/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.client.squad;

import com.deltaops.network.ModNetwork;
import com.deltaops.network.squad.ServerboundHandleRequestPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.UUID;

public class SquadRequestScreen extends Screen {
    private final UUID senderUuid;
    private final String senderName;
    private final String requestType;

    public SquadRequestScreen(UUID senderUuid, String senderName, String requestType) {
        super(Component.literal("小隊邀請/申請"));
        this.senderUuid = senderUuid;
        this.senderName = senderName;
        this.requestType = requestType;
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int top = height / 3;
        addRenderableWidget(Button.builder(Component.literal("接受"), btn -> handleResponse(true)).bounds(centerX - 110, top + 60, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("拒絕"), btn -> handleResponse(false)).bounds(centerX + 10, top + 60, 100, 20).build());
    }

    private void handleResponse(boolean accepted) {
        ModNetwork.CHANNEL.sendToServer(new ServerboundHandleRequestPacket(accepted, senderUuid, requestType));
        minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, delta);
        int centerX = width / 2;
        int top = height / 3;
        String verb = "INVITE".equals(requestType) ? "邀請你加入小隊" : "申請加入你的隊伍";
        gui.drawString(font, Component.literal(senderName + " " + verb), centerX - 110, top, 0xFFFFFF);
        gui.drawString(font, Component.literal("請選擇 接受 或 拒絕。"), centerX - 110, top + 14, 0xAAAAAA);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}
