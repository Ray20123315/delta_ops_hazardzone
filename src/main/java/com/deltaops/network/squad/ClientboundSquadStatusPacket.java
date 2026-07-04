/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.network.squad;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundSquadStatusPacket {
    public final UUID leaderUuid;
    public final List<UUID> memberUuids;
    public final List<String> memberNames;
    public final List<Boolean> readyStates;

    public ClientboundSquadStatusPacket(UUID leaderUuid, List<UUID> memberUuids, List<String> memberNames, List<Boolean> readyStates) {
        this.leaderUuid = leaderUuid;
        this.memberUuids = memberUuids;
        this.memberNames = memberNames;
        this.readyStates = readyStates;
    }

    public static void encode(ClientboundSquadStatusPacket pkt, FriendlyByteBuf buf) {
        buf.writeUUID(pkt.leaderUuid != null ? pkt.leaderUuid : UUID.randomUUID());
        buf.writeInt(pkt.memberUuids.size());
        for (int i = 0; i < pkt.memberUuids.size(); i++) {
            buf.writeUUID(pkt.memberUuids.get(i));
            buf.writeUtf(pkt.memberNames.get(i));
            buf.writeBoolean(pkt.readyStates.get(i));
        }
    }

    public static ClientboundSquadStatusPacket decode(FriendlyByteBuf buf) {
        UUID leader = buf.readUUID();
        int size = buf.readInt();
        List<UUID> uuids = new ArrayList<>();
        List<String> names = new ArrayList<>();
        List<Boolean> ready = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            uuids.add(buf.readUUID());
            names.add(buf.readUtf(32767));
            ready.add(buf.readBoolean());
        }
        return new ClientboundSquadStatusPacket(leader, uuids, names, ready);
    }

    public static void handle(ClientboundSquadStatusPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.screen instanceof com.deltaops.client.squad.SquadMainScreen screen) {
                screen.updateSquadStatus(pkt);
            }
        });
        ctx.setPacketHandled(true);
    }
}
