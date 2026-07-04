/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loot;

public enum LootCategory {
    COLLECTIBLE("collectible"),
    ELECTRONIC("electronic"),
    KEYCARD("keycard"),
    WEAPON("weapon"),
    MEDICAL("medical"),
    AMMO("ammo");

    private final String id;

    LootCategory(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static LootCategory fromId(String id) {
        for (LootCategory category : values()) {
            if (category.id.equalsIgnoreCase(id)) {
                return category;
            }
        }
        return COLLECTIBLE;
    }
}
