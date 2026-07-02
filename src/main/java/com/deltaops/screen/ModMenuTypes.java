package com.deltaops.screen;

import com.deltaops.DeltaOpsMod;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, DeltaOpsMod.MOD_ID);

    public static final RegistryObject<MenuType<GridInventoryMenu>> GRID =
            MENU_TYPES.register("grid", () -> IForgeMenuType.create((id, inv, data) -> new GridInventoryMenu(id, inv, false)));
    public static final RegistryObject<MenuType<GridInventoryMenu>> SECURE =
            MENU_TYPES.register("secure", () -> IForgeMenuType.create((id, inv, data) -> new GridInventoryMenu(id, inv, true)));

    public static final MenuProvider GRID_PROVIDER = new MenuProvider() {
        @Override public Component getDisplayName() { return Component.translatable("container." + DeltaOpsMod.MOD_ID + ".grid"); }
        @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) { return new GridInventoryMenu(id, inv, false); }
    };

    public static final MenuProvider SECURE_PROVIDER = new MenuProvider() {
        @Override public Component getDisplayName() { return Component.translatable("container." + DeltaOpsMod.MOD_ID + ".secure"); }
        @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) { return new GridInventoryMenu(id, inv, true); }
    };
}