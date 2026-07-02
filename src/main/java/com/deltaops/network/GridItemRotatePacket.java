package com.deltaops.network;

import com.deltaops.capability.ModCapabilities;
import com.deltaops.network.GridInventorySyncPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemRotatePacket {
    private final int gx, gy;
    private final boolean secure;
    public GridItemRotatePacket(int gx, int gy, boolean secure) { this.gx = gx; this.gy = gy; this.secure = secure; }
    public static void encode(GridItemRotatePacket p, FriendlyByteBuf b) { b.writeInt(p.gx); b.writeInt(p.gy); b.writeBoolean(p.secure); }
    public static GridItemRotatePacket decode(FriendlyByteBuf b) { return new GridItemRotatePacket(b.readInt(), b.readInt(), b.readBoolean()); }

    public static void handle(GridItemRotatePacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            (p.secure ? player.getCapability(ModCapabilities.SECURE_CONTAINER) : player.getCapability(ModCapabilities.GRID_INVENTORY))
                    .ifPresent(inv -> {
                        if (inv.rotateItem(p.gx, p.gy)) {
                            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new GridInventorySyncPacket(inv, p.secure));
                        }
                    });
        });
        ctx.get().setPacketHandled(true);
    }
}