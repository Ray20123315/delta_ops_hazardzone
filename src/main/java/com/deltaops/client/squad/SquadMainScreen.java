/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.client.squad;

import com.deltaops.network.ModNetwork;
import com.deltaops.network.squad.ClientboundSquadStatusPacket;
import com.deltaops.network.squad.ServerboundSquadActionPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SquadMainScreen extends Screen {
    private EditBox targetNameInput;
    private UUID leaderUuid;
    private final List<String> memberNames = new ArrayList<>();
    private final List<Boolean> readyStates = new ArrayList<>();
    private String statusText = "等待小隊資料...";

    public SquadMainScreen() {
        super(Component.literal("小隊面板"));
    }

    @Override
    protected void init() {
        super.init();
        int centerX = width / 2;
        int top = height / 5;
        targetNameInput = new EditBox(font, centerX - 110, top + 24, 220, 20, Component.literal("玩家名稱"));
        targetNameInput.setValue("");
        targetNameInput.setHint(Component.literal("輸入玩家名稱"));
        addRenderableWidget(targetNameInput);
        addRenderableWidget(Button.builder(Component.literal("建立小隊"), btn -> sendAction(ServerboundSquadActionPacket.Action.CREATE, null)).bounds(centerX - 210, top + 60, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("離開小隊"), btn -> sendAction(ServerboundSquadActionPacket.Action.LEAVE, null)).bounds(centerX - 105, top + 60, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("切換準備"), btn -> sendAction(ServerboundSquadActionPacket.Action.TOGGLE_READY, null)).bounds(centerX + 5, top + 60, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("發車"), btn -> sendAction(ServerboundSquadActionPacket.Action.LAUNCH, null)).bounds(centerX + 110, top + 60, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("邀請玩家"), btn -> sendAction(ServerboundSquadActionPacket.Action.INVITE, targetNameInput.getValue())).bounds(centerX - 210, top + 90, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("踢出玩家"), btn -> sendAction(ServerboundSquadActionPacket.Action.KICK, targetNameInput.getValue())).bounds(centerX - 105, top + 90, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("移交隊長"), btn -> sendAction(ServerboundSquadActionPacket.Action.TRANSFER, targetNameInput.getValue())).bounds(centerX + 5, top + 90, 100, 20).build());
        addRenderableWidget(Button.builder(Component.literal("切換自動配對"), btn -> sendAction(ServerboundSquadActionPacket.Action.TOGGLE_AUTO_MATCH, null)).bounds(centerX + 110, top + 90, 100, 20).build());
    }

    private void sendAction(ServerboundSquadActionPacket.Action action, String target) {
        ModNetwork.CHANNEL.sendToServer(new ServerboundSquadActionPacket(action, target));
    }

    public void updateSquadStatus(ClientboundSquadStatusPacket pkt) {
        this.leaderUuid = pkt.leaderUuid;
        this.memberNames.clear();
        this.readyStates.clear();
        this.memberNames.addAll(pkt.memberNames);
        this.readyStates.addAll(pkt.readyStates);
        this.statusText = "隊長: " + (pkt.leaderUuid != null ? pkt.leaderUuid.toString().substring(0, 8) : "未知") + "，成員: " + pkt.memberNames.size();
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (targetNameInput.mouseClicked(mx, my, button)) {
            return true;
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean charTyped(char chr, int keyCode) {
        if (targetNameInput.charTyped(chr, keyCode)) {
            return true;
        }
        return super.charTyped(chr, keyCode);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (targetNameInput.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(GuiGraphics gui, int mouseX, int mouseY, float delta) {
        renderBackground(gui);
        super.render(gui, mouseX, mouseY, delta);
        gui.drawString(font, Component.literal("小隊管理功能：建立、邀請、踢出、移交、切換準備、發車"), width / 2 - 180, height / 5 - 20, 0xFFFFFF);
        gui.drawString(font, Component.literal("輸入目標玩家名稱後，再執行邀請 / 踢出 / 移交。"), width / 2 - 180, height / 5 - 8, 0xAAAAAA);
        gui.drawString(font, Component.literal(this.statusText), width / 2 - 180, height / 5 + 140, 0xCCCCCC);
        int listStartY = height / 5 + 160;
        for (int i = 0; i < memberNames.size(); i++) {
            String name = memberNames.get(i);
            boolean ready = i < readyStates.size() && readyStates.get(i);
            String label = name + (ready ? " (已準備)" : " (未準備)");
            if (leaderUuid != null && i < memberNames.size() && name != null && name.length() > 0 && memberNames.get(i).equals(name) && leaderUuid != null) {
                // no player UUID mapping in client packet aside from position, so indicate leader by index if available
            }
            gui.drawString(font, Component.literal(label), width / 2 - 180, listStartY + i * 12, ready ? 0x55FF55 : 0xFF5555);
        }
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return true;
    }
}
