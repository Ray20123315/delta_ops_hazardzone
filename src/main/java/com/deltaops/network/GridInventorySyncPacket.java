package com.deltaops.network;

import com.deltaops.capability.ModCapabilities;
import com.deltaops.inventory.GridInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class GridInventorySyncPacket {
    private final CompoundTag data;
    private final boolean secure;

    public GridInventorySyncPacket(GridInventory inv, boolean secure) {
        this.data = inv.serializeNBT();
        this.secure = secure;
    }

    public GridInventorySyncPacket(CompoundTag data, boolean secure) { this.data = data; this.secure = secure; }

    public static void encode(GridInventorySyncPacket p, FriendlyByteBuf b) { b.writeNbt(p.data); b.writeBoolean(p.secure); }
    public static GridInventorySyncPacket decode(FriendlyByteBuf b) { return new GridInventorySyncPacket(b.readNbt(), b.readBoolean()); }

    public static void handle(GridInventorySyncPacket p, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            GridInventory inv = GridInventory.deserializeNBT(p.data);
            (p.secure ? player.getCapability(ModCapabilities.SECURE_CONTAINER) : player.getCapability(ModCapabilities.GRID_INVENTORY))
                    .ifPresent(g -> { g.getItems().clear(); g.getItems().addAll(inv.getItems()); });
        });
        ctx.get().setPacketHandled(true);
    }
}