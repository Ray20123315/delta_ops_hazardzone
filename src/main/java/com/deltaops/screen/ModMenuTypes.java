package com.deltaops.screen;

import com.deltaops.DeltaOpsMod;
import com.deltaops.container.TacticalContainerMenu;
import com.deltaops.loot.AdminItemTaggingMenu;
import com.deltaops.securebox.SecureBoxCapabilityManager;
import com.deltaops.securebox.SecureBoxMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.items.ItemStackHandler;
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
    public static final RegistryObject<MenuType<SecureBoxMenu>> SECURE_BOX =
            MENU_TYPES.register("secure_box", () -> IForgeMenuType.create((id, inv, data) -> {
                if (inv.player instanceof ServerPlayer serverPlayer) {
                    return new SecureBoxMenu(id, inv, SecureBoxCapabilityManager.getSecureBoxHandler(serverPlayer));
                }
                return new SecureBoxMenu(id, inv, new ItemStackHandler(2));
            }));

    public static final RegistryObject<MenuType<TacticalContainerMenu>> TACTICAL_CONTAINER =
            MENU_TYPES.register("tactical_container", () -> IForgeMenuType.create((id, inv, data) -> new TacticalContainerMenu(id, inv, com.deltaops.container.ContainerVariant.LARGE_SAFE, new ItemStackHandler(9), new ItemStackHandler(0), false)));

    public static final RegistryObject<MenuType<AdminItemTaggingMenu>> ADMIN_ITEM_TAGGING =
            MENU_TYPES.register("admin_item_tagging", () -> IForgeMenuType.create((id, inv, data) -> new AdminItemTaggingMenu(id, inv)));

    public static final MenuProvider GRID_PROVIDER = new MenuProvider() {
        @Override public Component getDisplayName() { return Component.translatable("container." + DeltaOpsMod.MOD_ID + ".grid"); }
        @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) { return new GridInventoryMenu(id, inv, false); }
    };

    public static final MenuProvider SECURE_PROVIDER = new MenuProvider() {
        @Override public Component getDisplayName() { return Component.translatable("container." + DeltaOpsMod.MOD_ID + ".secure"); }
        @Override public AbstractContainerMenu createMenu(int id, Inventory inv, Player p) { return new GridInventoryMenu(id, inv, true); }
    };
}