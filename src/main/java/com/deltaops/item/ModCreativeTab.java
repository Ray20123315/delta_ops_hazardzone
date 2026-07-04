package com.deltaops.item;

import com.deltaops.DeltaOpsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * 創造模式物品欄 - 使用原版 Chest 作為圖示（零自訂材質）
 */
public class ModCreativeTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DeltaOpsMod.MOD_ID);

    public static final RegistryObject<CreativeModeTab> TAB = CREATIVE_TABS.register(DeltaOpsMod.MOD_ID,
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + DeltaOpsMod.MOD_ID))
                    .icon(() -> new ItemStack(Items.CHEST))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.BANDAGE.get());
                        output.accept(ModItems.MEDKIT.get());
                        output.accept(ModItems.RIFLE_MAG.get());
                        output.accept(ModItems.PISTOL_MAG.get());
                        output.accept(ModItems.AMMO_BOX.get());
                        output.accept(ModItems.TACTICAL_VEST.get());
                        output.accept(ModItems.BACKPACK.get());
                        output.accept(ModItems.NIGHT_VISION.get());
                        output.accept(ModItems.GOLD_BAR.get());
                        output.accept(ModItems.INTELLIGENCE_DATA.get());
                        output.accept(ModItems.RELIC.get());
                        output.accept(ModItems.MEDICAL_SUPPLIES.get());
                        output.accept(ModItems.ASSAULT_RIFLE.get());
                        output.accept(ModItems.SMG.get());
                        output.accept(ModItems.SHOTGUN.get());
                        output.accept(ModItems.SNIPER_RIFLE.get());
                        output.accept(ModItems.PISTOL.get());
                    })
                    .build());
}