package com.deltaops.screen;

import com.deltaops.item.ItemSizeHelper;
import com.deltaops.inventory.GridItemStack;
import com.deltaops.inventory.GridInventory;
import com.deltaops.network.GridItemPlacePacket;
import com.deltaops.network.GridItemRemovePacket;
import com.deltaops.network.GridItemRotatePacket;
import com.deltaops.network.ModNetwork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

/**
 * 網格背包 GUI - 純程式碼渲染，零自訂材質
 * 使用半透明色塊、線條與文字繪製網格
 */
public class GridInventoryScreen extends AbstractContainerScreen<GridInventoryMenu> {
    private static final int GRID_LEFT = 8, GRID_TOP = 18, SLOT = 18;
    private final boolean secure;
    private GridInventory grid;

    public GridInventoryScreen(GridInventoryMenu menu, Inventory pi, Component title) {
        super(menu, pi, title);
        this.secure = menu.isSecureContainer();
        this.grid = menu.getGridInventory();
        int rows = secure ? 3 : 6;
        this.imageWidth = 176;
        this.imageHeight = 114 + rows * SLOT + 36;
    }

    @Override
    protected void init() {
        super.init();
        titleLabelX = 8;
        titleLabelY = 6;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics g, float pt, int mx, int my) {
        int x = (width - imageWidth) / 2, y = (height - imageHeight) / 2;
        int cols = secure ? 3 : 10, rows = secure ? 3 : 6;

        // 背景
        g.fill(x, y, x + imageWidth, y + imageHeight, 0xC0101010);

        // 網格
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int sx = x + GRID_LEFT + c * SLOT, sy = y + GRID_TOP + r * SLOT;
                boolean occ = grid.findItemAt(c, r).isPresent();
                g.fill(sx, sy, sx + SLOT, sy + SLOT, occ ? 0x30303030 : 0x18FFFFFF);
                // 邊框
                g.fill(sx, sy, sx + SLOT, sy + 1, 0x40FFFFFF);
                g.fill(sx, sy + SLOT - 1, sx + SLOT, sy + SLOT, 0x40FFFFFF);
                g.fill(sx, sy, sx + 1, sy + SLOT, 0x40FFFFFF);
                g.fill(sx + SLOT - 1, sy, sx + SLOT, sy + SLOT, 0x40FFFFFF);
            }
        }

        // 物品
        for (GridItemStack gi : grid.getItems()) {
            ItemStack stack = gi.getItemStack();
            int ix = x + GRID_LEFT + gi.getGridX() * SLOT;
            int iy = y + GRID_TOP + gi.getGridY() * SLOT;
            int iw = gi.getEffectiveWidth() * SLOT, ih = gi.getEffectiveHeight() * SLOT;

            g.fill(ix, iy, ix + iw, iy + ih, 0x80404040);
            g.renderItem(stack, ix + 1, iy + 1);
            g.renderItemDecorations(font, stack, ix + 1, iy + 1);

            // 稀有度邊框
            int rarity = ItemSizeHelper.getRarityLevel(stack);
            int color = switch (rarity < 4 ? rarity : 4) {
                case 1 -> 0xFF00FF00; case 2 -> 0xFF5555FF; case 3 -> 0xFFFF55FF; case 4 -> 0xFFFFD700;
                default -> 0xFFFFFFFF;
            };
            g.fill(ix, iy, ix + iw, iy + 1, color);
            g.fill(ix, iy + ih - 1, ix + iw, iy + ih, color);
            g.fill(ix, iy, ix + 1, iy + ih, color);
            g.fill(ix + iw - 1, iy, ix + iw, iy + ih, color);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int btn) {
        if (btn == 0) {
            int gx = (int) ((mx - leftPos - GRID_LEFT) / SLOT);
            int gy = (int) ((my - topPos - GRID_TOP) / SLOT);
            int mx2 = secure ? 3 : 10, my2 = secure ? 3 : 6;
            if (gx >= 0 && gy >= 0 && gx < mx2 && gy < my2) {
                if (grid.findItemAt(gx, gy).isPresent()) {
                    ModNetwork.CHANNEL.sendToServer(new GridItemRemovePacket(gx, gy, secure));
                } else if (!menu.getCarried().isEmpty()) {
                    int slot = getHoveredSlot(mx, my);
                    if (slot >= 0) ModNetwork.CHANNEL.sendToServer(new GridItemPlacePacket(slot, gx, gy, false, secure));
                }
            }
        }
        return super.mouseClicked(mx, my, btn);
    }

    @Override
    public boolean keyPressed(int kc, int sc, int mod) {
        if (kc == GLFW.GLFW_KEY_R) {
            double mx = minecraft.mouseHandler.xpos(), my = minecraft.mouseHandler.ypos();
            int gx = (int) ((mx - leftPos - GRID_LEFT) / SLOT);
            int gy = (int) ((my - topPos - GRID_TOP) / SLOT);
            int mx2 = secure ? 3 : 10, my2 = secure ? 3 : 6;
            if (gx >= 0 && gy >= 0 && gx < mx2 && gy < my2) {
                ModNetwork.CHANNEL.sendToServer(new GridItemRotatePacket(gx, gy, secure));
                return true;
            }
        }
        return super.keyPressed(kc, sc, mod);
    }

    private int getHoveredSlot(double mx, double my) {
        int rows = secure ? 3 : 6;
        int rx = (int) (mx - leftPos - 8);
        int ry = (int) (my - topPos - (GRID_TOP + rows * SLOT + 14));
        if (rx >= 0 && ry >= 0) { int c = rx / SLOT, r = ry / SLOT; if (c < 9 && r < 4) return r < 3 ? c + r * 9 + 9 : c; }
        return -1;
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics g, int mx, int my) {
        g.drawString(font, title, titleLabelX, titleLabelY, 0xFFFFFF);
        int rows = secure ? 3 : 6;
        g.drawString(font, playerInventoryTitle, 8, GRID_TOP + rows * SLOT + 4, 0xFFFFFF);
    }

    @Override
    public void render(@NotNull GuiGraphics g, int mx, int my, float delta) {
        renderBackground(g);
        super.render(g, mx, my, delta);
        renderTooltip(g, mx, my);
    }
}