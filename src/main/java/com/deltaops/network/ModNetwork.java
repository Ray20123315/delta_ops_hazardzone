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
        CHANNEL.registerMessage(id++, ServerboundOpenSecureBoxPacket.class, ServerboundOpenSecureBoxPacket::encode, ServerboundOpenSecureBoxPacket::decode, ServerboundOpenSecureBoxPacket::handle);
        CHANNEL.registerMessage(id++, ClientboundMatchStatusPacket.class, ClientboundMatchStatusPacket::encode, ClientboundMatchStatusPacket::decode, ClientboundMatchStatusPacket::handle);
        CHANNEL.registerMessage(id++, com.deltaops.loot.ServerboundSaveItemTagPacket.class, com.deltaops.loot.ServerboundSaveItemTagPacket::encode, com.deltaops.loot.ServerboundSaveItemTagPacket::decode, com.deltaops.loot.ServerboundSaveItemTagPacket::handle);
        // squad packets
        CHANNEL.registerMessage(id++, com.deltaops.network.squad.ServerboundSquadActionPacket.class, com.deltaops.network.squad.ServerboundSquadActionPacket::encode, com.deltaops.network.squad.ServerboundSquadActionPacket::decode, com.deltaops.network.squad.ServerboundSquadActionPacket::handle);
        CHANNEL.registerMessage(id++, com.deltaops.network.squad.ClientboundShowRequestScreenPacket.class, com.deltaops.network.squad.ClientboundShowRequestScreenPacket::encode, com.deltaops.network.squad.ClientboundShowRequestScreenPacket::decode, com.deltaops.network.squad.ClientboundShowRequestScreenPacket::handle);
        CHANNEL.registerMessage(id++, com.deltaops.network.squad.ServerboundHandleRequestPacket.class, com.deltaops.network.squad.ServerboundHandleRequestPacket::encode, com.deltaops.network.squad.ServerboundHandleRequestPacket::decode, com.deltaops.network.squad.ServerboundHandleRequestPacket::handle);
        CHANNEL.registerMessage(id++, com.deltaops.network.squad.ClientboundOpenSquadScreenPacket.class, com.deltaops.network.squad.ClientboundOpenSquadScreenPacket::encode, com.deltaops.network.squad.ClientboundOpenSquadScreenPacket::decode, com.deltaops.network.squad.ClientboundOpenSquadScreenPacket::handle);
        CHANNEL.registerMessage(id++, com.deltaops.network.squad.ClientboundSquadStatusPacket.class, com.deltaops.network.squad.ClientboundSquadStatusPacket::encode, com.deltaops.network.squad.ClientboundSquadStatusPacket::decode, com.deltaops.network.squad.ClientboundSquadStatusPacket::handle);
    }
}