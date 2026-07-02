package com.deltaops.network;

import com.deltaops.DeltaOpsMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ModNetwork {
    private static final String VER = "1.0";
    private static int id = 0;
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DeltaOpsMod.MOD_ID, "main"),
            () -> VER, VER::equals, VER::equals);

    public static void register() {
        CHANNEL.registerMessage(id++, GridInventorySyncPacket.class, GridInventorySyncPacket::encode, GridInventorySyncPacket::decode, GridInventorySyncPacket::handle);
        CHANNEL.registerMessage(id++, GridItemPlacePacket.class, GridItemPlacePacket::encode, GridItemPlacePacket::decode, GridItemPlacePacket::handle);
        CHANNEL.registerMessage(id++, GridItemRemovePacket.class, GridItemRemovePacket::encode, GridItemRemovePacket::decode, GridItemRemovePacket::handle);
        CHANNEL.registerMessage(id++, GridItemRotatePacket.class, GridItemRotatePacket::encode, GridItemRotatePacket::decode, GridItemRotatePacket::handle);
        CHANNEL.registerMessage(id++, OpenGridInventoryPacket.class, OpenGridInventoryPacket::encode, OpenGridInventoryPacket::decode, OpenGridInventoryPacket::handle);
    }
}