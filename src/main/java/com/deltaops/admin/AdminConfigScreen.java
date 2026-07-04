package com.deltaops.admin;

import com.deltaops.DeltaOpsMod;
import com.deltaops.lobby.HazardMapRegistry;
import com.deltaops.network.ModNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class AdminConfigScreen extends AbstractContainerScreen<AdminConfigMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(DeltaOpsMod.MOD_ID, "textures/gui/admin_config.png");
    private int selectedMapIndex = 0;
    private final List<String> mapIds = new ArrayList<>(HazardMapRegistry.getAllMaps().keySet());
    private EditBox itemIdBox;
    private EditBox priceBox;

    public AdminConfigScreen(AdminConfigMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 256;
        this.imageHeight = 220;
    }

    @Override
    protected void init() {
        super.init();
        int baseX = this.leftPos + 24;
        int baseY = this.topPos + 38;

        this.addRenderableWidget(Button.builder(Component.literal("上一個地圖"), button -> {
            this.selectedMapIndex = (this.selectedMapIndex + this.mapIds.size() - 1) % this.mapIds.size();
        }).bounds(baseX, baseY, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("下一個地圖"), button -> {
            this.selectedMapIndex = (this.selectedMapIndex + 1) % this.mapIds.size();
        }).bounds(baseX + 88, baseY, 80, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("設置此地圖撤離點"), button -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String mapId = this.mapIds.get(this.selectedMapIndex);
                ModNetwork.CHANNEL.sendToServer(new ServerboundAdminConfigActionPacket(ServerboundAdminConfigActionPacket.Action.SET_EXTRACTION_POINT, mapId));
                mc.player.sendSystemMessage(Component.literal("已請求服務端設置地圖 " + mapId + " 的撤離點。"));
            }
        }).bounds(baseX, baseY + 28, 180, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("重載物價表"), button -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                ModNetwork.CHANNEL.sendToServer(new ServerboundAdminConfigActionPacket(ServerboundAdminConfigActionPacket.Action.RELOAD_PRICES, ""));
                mc.player.sendSystemMessage(Component.literal("已請求服務端重載物價表。"));
            }
        }).bounds(baseX, baseY + 56, 180, 20).build());

        this.itemIdBox = this.addRenderableWidget(new EditBox(this.font, baseX, baseY + 84, 120, 18, Component.literal("物品 id")));
        this.itemIdBox.setMaxLength(128);
        this.itemIdBox.setHint(Component.literal("minecraft:diamond"));

        this.priceBox = this.addRenderableWidget(new EditBox(this.font, baseX + 128, baseY + 84, 52, 18, Component.literal("價格")));
        this.priceBox.setMaxLength(16);
        this.priceBox.setHint(Component.literal("1800"));

        this.addRenderableWidget(Button.builder(Component.literal("保存價格"), button -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                String itemId = this.itemIdBox.getValue();
                String priceText = this.priceBox.getValue();
                try {
                    long price = Long.parseLong(priceText);
                    ModNetwork.CHANNEL.sendToServer(new ServerboundAdminConfigActionPacket(ServerboundAdminConfigActionPacket.Action.SET_ITEM_PRICE, "", itemId, price));
                    mc.player.sendSystemMessage(Component.literal("已更新物品價格：" + itemId + " = " + price));
                } catch (NumberFormatException ignored) {
                    mc.player.sendSystemMessage(Component.literal("價格必須是數字。"));
                }
            }
        }).bounds(baseX, baseY + 108, 180, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("關閉"), button -> Minecraft.getInstance().setScreen(null)).bounds(baseX, baseY + 136, 180, 20).build());
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, "管理員設定", this.leftPos + 24, this.topPos + 18, 0xFFFFFF, false);
        String currentMap = this.mapIds.isEmpty() ? "無地圖" : this.mapIds.get(this.selectedMapIndex);
        guiGraphics.drawString(this.font, "當前地圖: " + currentMap, this.leftPos + 24, this.topPos + 28, 0xAAAAAA, false);

        guiGraphics.drawString(this.font, "目前已開放：", this.leftPos + 24, this.topPos + 150, 0xFFFFFF, false);
        guiGraphics.drawString(this.font, "• 選擇地圖與設定撤離點", this.leftPos + 24, this.topPos + 164, 0xAAAAAA, false);
        guiGraphics.drawString(this.font, "• 重載物價表與修改物品價格", this.leftPos + 24, this.topPos + 178, 0xAAAAAA, false);
        guiGraphics.drawString(this.font, "暫不可：安全箱等級、死亡掉落規則、撤離倒數與獎勵倍率", this.leftPos + 24, this.topPos + 192, 0xFFAA00, false);
    }
}
