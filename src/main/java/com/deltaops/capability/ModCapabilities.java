package com.deltaops.capability;

import com.deltaops.DeltaOpsMod;
import com.deltaops.health.BodyPartHealth;
import com.deltaops.inventory.GridInventory;
import com.deltaops.securebox.SecureBoxCapabilityManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

public class ModCapabilities {
    public static final Capability<GridInventory> GRID_INVENTORY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<GridInventory> SECURE_CONTAINER = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<BodyPartHealth> BODY_PART = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<OperatorCapability.OperatorData> OPERATOR_DATA = CapabilityManager.get(new CapabilityToken<>() {});

    public static final ResourceLocation GRID_KEY = new ResourceLocation(DeltaOpsMod.MOD_ID, "grid");
    public static final ResourceLocation SECURE_KEY = new ResourceLocation(DeltaOpsMod.MOD_ID, "secure");
    public static final ResourceLocation BODY_KEY = new ResourceLocation(DeltaOpsMod.MOD_ID, "body");
    public static final ResourceLocation OPERATOR_KEY = new ResourceLocation(DeltaOpsMod.MOD_ID, "operator");

    // 🟢 MOD 總線：專門處理遊戲啟動時的「註冊」
    @Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModBusEvents {
        @SubscribeEvent
        public static void onRegister(RegisterCapabilitiesEvent e) {
            e.register(GridInventory.class);
            e.register(BodyPartHealth.class);
            e.register(OperatorCapability.OperatorData.class);
        }
    }

    // 🟢 FORGE 總線：專門處理遊戲進行中的「附著」與「死亡繼承」
    @Mod.EventBusSubscriber(modid = DeltaOpsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ForgeBusEvents {
        @SubscribeEvent
        public static void onAttach(AttachCapabilitiesEvent<Entity> e) {
            if (e.getObject() instanceof Player) {
                e.addCapability(GRID_KEY, new GridInvProvider(false));
                e.addCapability(SECURE_KEY, new GridInvProvider(true));
                e.addCapability(BODY_KEY, new BodyPartHealth.Provider());
                e.addCapability(OPERATOR_KEY, new OperatorCapability.Provider());
            }
        }

        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone e) {
            if (!e.isWasDeath()) return;
            Player o = e.getOriginal(); Player n = e.getEntity();
            o.reviveCaps(); n.reviveCaps();
            try {
                // 安全箱保留
                var src = o.getCapability(SECURE_CONTAINER).orElse(new GridInventory(3, 3));
                var dst = n.getCapability(SECURE_CONTAINER).orElse(new GridInventory(3, 3));
                dst.getItems().clear();
                dst.getItems().addAll(src.getItems());
                SecureBoxCapabilityManager.copySecureBoxToNewPlayer(o, n);

                // 保留特種兵選擇
                var opSrc = o.getCapability(OPERATOR_DATA).orElse(new OperatorCapability.OperatorData());
                var opDst = n.getCapability(OPERATOR_DATA).orElse(new OperatorCapability.OperatorData());
                opDst.setSelectedOperatorId(opSrc.getSelectedOperatorId());
            } finally { o.invalidateCaps(); n.invalidateCaps(); }
        }
    }
}