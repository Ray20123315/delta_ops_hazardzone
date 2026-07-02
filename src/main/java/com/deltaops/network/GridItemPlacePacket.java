package com.deltaops.network;

import com.deltaops.capability.ModCapabilities;
import com.deltaops.network.GridInventorySyncPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemPlacePacket {
    private final int slot, gx, gy;
    private final boolean rotated, secure;

    public GridItemPlacePacket(int slot, int gx, int gy, boolean rotated, boolean secure) { this.slot = slot; this.gx = gx; this.gy = gy; this.rotated = rotated; this.secure = secure; }
    public static void encode(GridItemPlacePacket p, FriendlyByteBuf b) { b.writeInt(p.slot); b.writeInt(p.gx); b.writeInt(p.gy); b.writeBoolean(p.rotated); b.writeBoolean(p.secure); }
    public static GridItemPlacePacket decode(FriendlyByteBuf b) { return new GridItemPlacePacket(b.readInt(), b.readInt(), b.readInt(), b.readBoolean(), b.readBoolean()); }

    public static void handle(GridItemPlacePacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            ItemStack held = player.getInventory().getItem(p.slot);
            if (held.isEmpty()) return;
            (p.secure ? player.getCapability(ModCapabilities.SECURE_CONTAINER) : player.getCapability(ModCapabilities.GRID_INVENTORY))
                    .ifPresent(inv -> {
                        if (inv.placeItem(held, p.gx, p.gy, p.rotated)) {
                            player.getInventory().setItem(p.slot, ItemStack.EMPTY);
                            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new GridInventorySyncPacket(inv, p.secure));
                        }
                    });
        });
        ctx.get().setPacketHandled(true);
    }
}