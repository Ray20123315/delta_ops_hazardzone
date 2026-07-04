/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loot;

import com.deltaops.DeltaOpsMod;
import com.deltaops.network.ModNetwork;
import com.deltaops.loot.ServerboundSaveItemTagPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AdminItemTaggingScreen extends AbstractContainerScreen<AdminItemTaggingMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(DeltaOpsMod.MOD_ID, "textures/gui/admin_tagging.png");
    private LootCategory selectedCategory = LootCategory.COLLECTIBLE;
    private ItemQuality selectedQuality = ItemQuality.WHITE;
    private Button saveButton;
    private Button categoryButton;
    private Button qualityButton;

    public AdminItemTaggingScreen(AdminItemTaggingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 320;
        this.imageHeight = 240;
    }

    @Override
    protected void init() {
        super.init();
        int x = this.leftPos + 180;
        int y = this.topPos + 60;
        this.categoryButton = this.addRenderableWidget(Button.builder(Component.literal("類別: " + this.selectedCategory.name()), button -> {
            this.selectedCategory = LootCategory.values()[(this.selectedCategory.ordinal() + 1) % LootCategory.values().length];
            updateCategoryButton();
        }).bounds(x, y, 120, 20).build());
        this.qualityButton = this.addRenderableWidget(Button.builder(Component.literal("品質: " + this.selectedQuality.name()), button -> {
            this.selectedQuality = ItemQuality.values()[(this.selectedQuality.ordinal() + 1) % ItemQuality.values().length];
            updateQualityButton();
        }).bounds(x, y + 24, 120, 20).build());
        this.saveButton = this.addRenderableWidget(Button.builder(Component.literal("保存標籤"), button -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return;
            }
            ItemStack held = mc.player.getMainHandItem();
            if (held.isEmpty()) {
                mc.player.sendSystemMessage(Component.literal("請手持要標籤的物品。"));
                return;
            }
            Item item = held.getItem();
            net.minecraft.resources.ResourceLocation itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item);
            if (itemId == null) {
                mc.player.sendSystemMessage(Component.literal("無法識別該物品。"));
                return;
            }
            ModNetwork.CHANNEL.sendToServer(new ServerboundSaveItemTagPacket(itemId.toString(), this.selectedCategory, this.selectedQuality));
            mc.player.sendSystemMessage(Component.literal("已送出標籤存檔請求：" + itemId + " -> " + this.selectedCategory.name() + " / " + this.selectedQuality.name()));
        }).bounds(x, y + 52, 120, 20).build());
    }

    private void updateCategoryButton() {
        if (this.categoryButton != null) {
            this.categoryButton.setMessage(Component.literal("類別: " + this.selectedCategory.name()));
        }
    }

    private void updateQualityButton() {
        if (this.qualityButton != null) {
            this.qualityButton.setMessage(Component.literal("品質: " + this.selectedQuality.name()));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "Loot Categories", this.leftPos + 12, this.topPos + 12, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, "品質", this.leftPos + 180, this.topPos + 40, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(this.font, Component.literal("請手持要標籤的物品，然後選擇類別和品質。"), this.leftPos + 12, this.topPos + 16, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, Component.literal("當前類別: " + this.selectedCategory.name()), this.leftPos + 12, this.topPos + 32, 0xAAAAAA, false);
        guiGraphics.drawString(this.font, Component.literal("當前品質: " + this.selectedQuality.name()), this.leftPos + 12, this.topPos + 48, 0xAAAAAA, false);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
