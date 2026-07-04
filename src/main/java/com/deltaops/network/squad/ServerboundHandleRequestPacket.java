/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.network.squad;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class ServerboundHandleRequestPacket {
    public final boolean accepted;
    public final UUID senderUuid; // the original sender of the request
    public final String type; // INVITE or APPLY

    public ServerboundHandleRequestPacket(boolean accepted, UUID senderUuid, String type) {
        this.accepted = accepted;
        this.senderUuid = senderUuid;
        this.type = type;
    }

    public static void encode(ServerboundHandleRequestPacket pkt, FriendlyByteBuf buf) {
        buf.writeBoolean(pkt.accepted);
        buf.writeBoolean(pkt.senderUuid != null);
        if (pkt.senderUuid != null) buf.writeUUID(pkt.senderUuid);
        buf.writeUtf(pkt.type != null ? pkt.type : "");
    }

    public static ServerboundHandleRequestPacket decode(FriendlyByteBuf buf) {
        boolean acc = buf.readBoolean();
        UUID s = null;
        if (buf.readBoolean()) s = buf.readUUID();
        String t = buf.readUtf();
        return new ServerboundHandleRequestPacket(acc, s, t);
    }

    public static void handle(ServerboundHandleRequestPacket pkt, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            var player = ctx.getSender();
            if (player == null) return;
            var pending = com.deltaops.lobby.LobbySquadManager.consumePending(player.getUUID());
            if (pending == null) return;
            if (pkt.senderUuid != null && !pending.sender.equals(pkt.senderUuid)) {
                return;
            }
            if (pkt.accepted) {
                if (pending.type == com.deltaops.lobby.LobbySquadManager.RequestType.INVITE) {
                    ServerPlayerHelper.safeAddToSenderSquad(pending.sender, player);
                } else {
                    ServerPlayerHelper.safeAddApplicantToLeaderSquad(pending.sender, player);
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    // helper inner class to avoid cross-file heavy deps
    private static class ServerPlayerHelper {
        static void safeAddToSenderSquad(UUID senderUuid, net.minecraft.server.level.ServerPlayer target) {
            var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            net.minecraft.server.level.ServerPlayer sender = server.getPlayerList().getPlayer(senderUuid);
            if (sender == null) return;
            com.deltaops.lobby.LobbySquadManager.Squad squad = com.deltaops.lobby.LobbySquadManager.getSquadByPlayer(sender.getUUID());
            if (squad == null) {
                squad = com.deltaops.lobby.LobbySquadManager.createOrGetSquad(sender);
            }
            if (squad != null) {
                com.deltaops.lobby.LobbySquadManager.joinSquad(target, squad.squadId);
            }
        }

        static void safeAddApplicantToLeaderSquad(UUID applicantUuid, net.minecraft.server.level.ServerPlayer leader) {
            var server = net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
            if (server == null) return;
            net.minecraft.server.level.ServerPlayer applicant = server.getPlayerList().getPlayer(applicantUuid);
            if (applicant == null) return;
            com.deltaops.lobby.LobbySquadManager.Squad leaderSquad = com.deltaops.lobby.LobbySquadManager.getSquadByPlayer(leader.getUUID());
            if (leaderSquad == null) return;
            com.deltaops.lobby.LobbySquadManager.joinSquad(applicant, leaderSquad.squadId);
        }
    }
}
