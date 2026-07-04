/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.container;

import com.deltaops.loot.LootCategory;
import net.minecraft.util.StringRepresentable;

public enum ContainerVariant implements StringRepresentable {
    LARGE_SAFE("large_safe", 3, 3, 0xFFFFD54F, true),
    SMALL_SAFE("small_safe", 2, 2, 0xFFFFE082, true),
    FLIGHT_CASE("flight_case", 3, 3, 0xFFFFD54F, true),
    SERVER("server", 4, 3, 0xFF4DD0E1, false),
    HACKER_PC("hacker_pc", 3, 2, 0xFF64B5F6, false),
    LAB_COAT("lab_coat", 2, 2, 0xFFB3E5FC, false),
    PREMIUM_BOX("premium_box", 3, 3, 0xFFFFE082, true),
    WEAPON_CASE("weapon_case", 5, 2, 0xFFB0BEC5, false),
    MEDICAL_BAG("medical_bag", 2, 2, 0xFFFFFFFF, false),
    AMMO_BOX("ammo_box", 2, 2, 0xFF90CAF9, false),
    TOOLBOX("toolbox", 2, 2, 0xFFBDBDBD, false),
    HIKING_BAG("hiking_bag", 4, 4, 0xFF82B1FF, false),
    TRAVEL_BAG("travel_bag", 3, 3, 0xFF80DEEA, false),
    LOCKER("locker", 2, 4, 0xFF757575, false),
    PC_CASE("pc_case", 2, 2, 0xFF7986CB, false),
    DRAWER("drawer", 2, 2, 0xFF9E9E9E, false),
    FIELD_CRATE("field_crate", 3, 3, 0xFFB0BEC5, false),
    HIDDEN_STASH("hidden_stash", 3, 3, 0xFFBA68C8, false),
    TRASH_CAN("trash_can", 2, 2, 0xFF212121, false),
    BIRD_NEST("bird_nest", 1, 2, 0xFF8D6E63, false),
    CEMENT_MIXER("cement_mixer", 4, 2, 0xFF4E342E, false),
    EXPRESS_BOX("express_box", 2, 2, 0xFF7E57C2, false),
    CAR_STORAGE("car_storage", 3, 2, 0xFFBCAAA4, false),
    DESERT_CHEST("desert_chest", 3, 3, 0xFFD4A017, true);

    private final String name;
    private final int width;
    private final int height;
    private final int glowColor;
    private final boolean hiddenLayerSupported;

    ContainerVariant(String name, int width, int height, int glowColor, boolean hiddenLayerSupported) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.glowColor = glowColor;
        this.hiddenLayerSupported = hiddenLayerSupported;
    }

    public String getInternalName() {
        return this.name;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getGlowColor() {
        return this.glowColor;
    }

    public boolean supportsHiddenLayer() {
        return this.hiddenLayerSupported;
    }

    public ContainerTier getTier() {
        return switch (this) {
            case LARGE_SAFE, SMALL_SAFE, FLIGHT_CASE, PREMIUM_BOX, DESERT_CHEST -> ContainerTier.HIGH_TIER;
            case SERVER, HACKER_PC, LAB_COAT, WEAPON_CASE, TRAVEL_BAG -> ContainerTier.MID_TIER;
            case MEDICAL_BAG, AMMO_BOX, TOOLBOX, HIKING_BAG, LOCKER, PC_CASE, DRAWER, FIELD_CRATE, EXPRESS_BOX, CAR_STORAGE -> ContainerTier.LOW_TIER;
            case HIDDEN_STASH, TRASH_CAN, BIRD_NEST, CEMENT_MIXER -> ContainerTier.TRASH_TIER;
        };
    }

    public LootCategory getLootCategory() {
        return switch (this) {
            case LARGE_SAFE, SMALL_SAFE, FLIGHT_CASE, PREMIUM_BOX, DESERT_CHEST -> LootCategory.COLLECTIBLE;
            case SERVER, HACKER_PC, LAB_COAT -> LootCategory.ELECTRONIC;
            case WEAPON_CASE, HIKING_BAG, TRAVEL_BAG, LOCKER, DRAWER, FIELD_CRATE, CAR_STORAGE -> LootCategory.WEAPON;
            case MEDICAL_BAG, AMMO_BOX, TOOLBOX, EXPRESS_BOX -> LootCategory.MEDICAL;
            case HIDDEN_STASH, TRASH_CAN, BIRD_NEST, CEMENT_MIXER -> LootCategory.KEYCARD;
            case PC_CASE -> LootCategory.AMMO;
        };
    }

    public static ContainerVariant fromName(String name) {
        for (ContainerVariant variant : values()) {
            if (variant.name.equalsIgnoreCase(name)) {
                return variant;
            }
        }
        return LARGE_SAFE;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.name.replace('_', ' ');
    }

    public enum ContainerTier {
        HIGH_TIER,
        MID_TIER,
        LOW_TIER,
        TRASH_TIER
    }
}
