/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loot;

public enum ItemQuality {
    WHITE("white"),
    GREEN("green"),
    BLUE("blue"),
    PURPLE("purple"),
    GOLD("gold"),
    RED("red");

    private final String id;

    ItemQuality(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public static ItemQuality fromId(String id) {
        for (ItemQuality quality : values()) {
            if (quality.id.equalsIgnoreCase(id)) {
                return quality;
            }
        }
        return WHITE;
    }
}
