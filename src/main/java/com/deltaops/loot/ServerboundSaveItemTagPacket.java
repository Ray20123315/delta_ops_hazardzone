/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loot;

import com.deltaops.DeltaOpsMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundSaveItemTagPacket {
    private final String itemId;
    private final LootCategory category;
    private final ItemQuality quality;

    public ServerboundSaveItemTagPacket(String itemId, LootCategory category, ItemQuality quality) {
        this.itemId = itemId;
        this.category = category;
        this.quality = quality;
    }

    public static void encode(ServerboundSaveItemTagPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.itemId != null ? packet.itemId : "");
        buffer.writeUtf(packet.category != null ? packet.category.name() : LootCategory.COLLECTIBLE.name());
        buffer.writeUtf(packet.quality != null ? packet.quality.name() : ItemQuality.WHITE.name());
    }

    public static ServerboundSaveItemTagPacket decode(FriendlyByteBuf buffer) {
        return new ServerboundSaveItemTagPacket(buffer.readUtf(32767), LootCategory.valueOf(buffer.readUtf(32767)), ItemQuality.valueOf(buffer.readUtf(32767)));
    }

    public static void handle(ServerboundSaveItemTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.itemId == null || packet.itemId.isBlank()) {
                return;
            }
            GlobalLootDatabase.getInstance().setTag(packet.itemId, packet.category, packet.quality);
            DeltaOpsMod.LOGGER.info("Saved loot tag for {} -> {} / {}", packet.itemId, packet.category, packet.quality);
        });
        ctx.get().setPacketHandled(true);
    }
}
