package com.deltaops.capability;

import com.deltaops.DeltaOpsMod;
import com.deltaops.health.BodyPartHealth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModCapabilities {
    public static final Capability<BodyPartHealth> BODY_PART = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation BODY_KEY = new ResourceLocation(DeltaOpsMod.MOD_ID, "body");

    // 🟢 MOD 總線：專門處理遊戲啟動時的「註冊」
    @Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegister(RegisterCapabilitiesEvent e) {
            e.register(BodyPartHealth.class);
        }
    }

    // 🟢 FORGE 總線：專門處理遊戲進行中的「附著」
    @Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onAttach(AttachCapabilitiesEvent<Entity> e) {
            if (e.getObject() instanceof Player) {
                e.addCapability(BODY_KEY, new BodyPartHealth.Provider());
            }
        }
    }
}
