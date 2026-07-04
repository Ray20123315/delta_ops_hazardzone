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

public class ClientboundOpenSquadScreenPacket {
    public ClientboundOpenSquadScreenPacket() {}

    public static void encode(ClientboundOpenSquadScreenPacket pkt, FriendlyByteBuf buf) {}

    public static ClientboundOpenSquadScreenPacket decode(FriendlyByteBuf buf) { return new ClientboundOpenSquadScreenPacket(); }

    public static void handle(ClientboundOpenSquadScreenPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                Minecraft mc = Minecraft.getInstance();
                mc.execute(() -> mc.setScreen(new com.deltaops.client.squad.SquadMainScreen()));
            }
        });
        ctx.setPacketHandled(true);
    }
}
