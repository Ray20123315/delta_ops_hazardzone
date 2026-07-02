package com.deltaops.network;

import com.deltaops.capability.ModCapabilities;
import com.deltaops.network.GridInventorySyncPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridItemRemovePacket {
    private final int gx, gy;
    private final boolean secure;
    public GridItemRemovePacket(int gx, int gy, boolean secure) { this.gx = gx; this.gy = gy; this.secure = secure; }
    public static void encode(GridItemRemovePacket p, FriendlyByteBuf b) { b.writeInt(p.gx); b.writeInt(p.gy); b.writeBoolean(p.secure); }
    public static GridItemRemovePacket decode(FriendlyByteBuf b) { return new GridItemRemovePacket(b.readInt(), b.readInt(), b.readBoolean()); }

    public static void handle(GridItemRemovePacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            (p.secure ? player.getCapability(ModCapabilities.SECURE_CONTAINER) : player.getCapability(ModCapabilities.GRID_INVENTORY))
                    .ifPresent(inv -> {
                        var removed = inv.removeItem(p.gx, p.gy);
                        if (removed != null) {
                            ItemStack stack = removed.getItemStack();
                            if (!player.getInventory().add(stack)) player.drop(stack, false);
                            ModNetwork.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new GridInventorySyncPacket(inv, p.secure));
                        }
                    });
        });
        ctx.get().setPacketHandled(true);
    }
}