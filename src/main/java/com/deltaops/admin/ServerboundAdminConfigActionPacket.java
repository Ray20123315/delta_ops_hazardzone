package com.deltaops.admin;

import com.deltaops.extraction.ExtractionPointManager;
import com.deltaops.lobby.EconomyManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundAdminConfigActionPacket {
    public enum Action {
        SET_EXTRACTION_POINT,
        RELOAD_PRICES,
        SET_ITEM_PRICE
    }

    private final Action action;
    private final String mapName;
    private final String itemId;
    private final long price;

    public ServerboundAdminConfigActionPacket(Action action, String mapName) {
        this(action, mapName, "", 0L);
    }

    public ServerboundAdminConfigActionPacket(Action action, String mapName, String itemId, long price) {
        this.action = action;
        this.mapName = mapName;
        this.itemId = itemId;
        this.price = price;
    }

    public static void encode(ServerboundAdminConfigActionPacket packet, FriendlyByteBuf buf) {
        buf.writeEnum(packet.action);
        buf.writeUtf(packet.mapName == null ? "" : packet.mapName);
        buf.writeUtf(packet.itemId == null ? "" : packet.itemId);
        buf.writeLong(packet.price);
    }

    public static ServerboundAdminConfigActionPacket decode(FriendlyByteBuf buf) {
        return new ServerboundAdminConfigActionPacket(buf.readEnum(Action.class), buf.readUtf(), buf.readUtf(), buf.readLong());
    }

    public static void handle(ServerboundAdminConfigActionPacket packet, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) {
                return;
            }
            if (packet.action == Action.SET_EXTRACTION_POINT) {
                ExtractionPointManager.registerExtractionPoint(player, packet.mapName);
            } else if (packet.action == Action.RELOAD_PRICES) {
                EconomyManager.reloadPrices();
            } else if (packet.action == Action.SET_ITEM_PRICE) {
                EconomyManager.setItemPrice(packet.itemId, packet.price);
            }
        });
        ctx.setPacketHandled(true);
    }
}
