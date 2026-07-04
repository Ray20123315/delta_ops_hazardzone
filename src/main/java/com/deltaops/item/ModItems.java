package com.deltaops.item;

import com.deltaops.DeltaOpsMod;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * 戰術物品註冊 - 零自訂材質，使用原版 Item 類別
 */
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, DeltaOpsMod.MOD_ID);

    // ===== 醫療道具（自訂類別） =====
    public static final RegistryObject<Item> BANDAGE = ITEMS.register("bandage", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MEDKIT = ITEMS.register("medkit", () -> new Item(new Item.Properties()));

    // ===== 彈藥 =====
    public static final RegistryObject<Item> RIFLE_MAG = ITEMS.register("rifle_mag", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PISTOL_MAG = ITEMS.register("pistol_mag", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> AMMO_BOX = ITEMS.register("ammo_box", () -> new Item(new Item.Properties()));

    // ===== 裝備 =====
    public static final RegistryObject<Item> TACTICAL_VEST = ITEMS.register("tactical_vest", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BACKPACK = ITEMS.register("backpack", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> NIGHT_VISION = ITEMS.register("night_vision", () -> new Item(new Item.Properties()));

    // ===== 高價值 =====
    public static final RegistryObject<Item> GOLD_BAR = ITEMS.register("gold_bar", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> INTELLIGENCE_DATA = ITEMS.register("intelligence_data", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> RELIC = ITEMS.register("relic", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> MEDICAL_SUPPLIES = ITEMS.register("medical_supplies", () -> new Item(new Item.Properties()));

    // ===== 武器 =====
    public static final RegistryObject<Item> ASSAULT_RIFLE = ITEMS.register("assault_rifle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SMG = ITEMS.register("smg", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SHOTGUN = ITEMS.register("shotgun", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> SNIPER_RIFLE = ITEMS.register("sniper_rifle", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PISTOL = ITEMS.register("pistol", () -> new Item(new Item.Properties()));

    // ===== 戰術容器工具 =====
    public static final RegistryObject<Item> BRAIN_COMPUTER_INTERFACE = ITEMS.register("brain_computer_interface", () -> new Item(new Item.Properties()));
}