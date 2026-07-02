package com.deltaops.network;

import com.deltaops.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenGridInventoryPacket {
    private final boolean secure;
    public OpenGridInventoryPacket(boolean secure) { this.secure = secure; }
    public static void encode(OpenGridInventoryPacket p, FriendlyByteBuf b) { b.writeBoolean(p.secure); }
    public static OpenGridInventoryPacket decode(FriendlyByteBuf b) { return new OpenGridInventoryPacket(b.readBoolean()); }

    public static void handle(OpenGridInventoryPacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            player.openMenu(p.secure ? ModMenuTypes.SECURE_PROVIDER : ModMenuTypes.GRID_PROVIDER);
        });
        ctx.get().setPacketHandled(true);
    }
}