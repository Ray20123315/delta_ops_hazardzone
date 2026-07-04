/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.network.squad;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.function.Supplier;

public class ClientboundShowRequestScreenPacket {
    public final java.util.UUID senderUuid;
    public final String senderName;
    public final String requestType; // "INVITE" or "APPLY"

    public ClientboundShowRequestScreenPacket(java.util.UUID senderUuid, String senderName, String requestType) {
        this.senderUuid = senderUuid;
        this.senderName = senderName;
        this.requestType = requestType;
    }

    public static void encode(ClientboundShowRequestScreenPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.senderUuid != null);
        if (pkt.senderUuid != null) buf.writeUUID(pkt.senderUuid);
        buf.writeUtf(pkt.senderName != null ? pkt.senderName : "");
        buf.writeUtf(pkt.requestType);
    }

    public static ClientboundShowRequestScreenPacket decode(FriendlyByteBuf buf) {
        java.util.UUID u = null;
        if (buf.readBoolean()) u = buf.readUUID();
        String name = buf.readUtf();
        String type = buf.readUtf();
        return new ClientboundShowRequestScreenPacket(u, name, type);
    }

    public static void handle(ClientboundShowRequestScreenPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        // client side only
        ctx.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft mc = Minecraft.getInstance();
                mc.execute(() -> {
                    mc.setScreen(new com.deltaops.client.squad.SquadRequestScreen(pkt.senderUuid, pkt.senderName, pkt.requestType));
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}
