/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.loot;

import com.deltaops.DeltaOpsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

public class GlobalLootDatabase {
    private static final GlobalLootDatabase INSTANCE = new GlobalLootDatabase();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type MAP_TYPE = new TypeToken<LinkedHashMap<String, ItemEntry>>() {}.getType();
    private static final Random RANDOM = new Random();

    private final Map<String, ItemEntry> entries = new LinkedHashMap<>();
    private final Path storagePath = Paths.get("config", DeltaOpsMod.MOD_ID, "item_qualities.json");

    private GlobalLootDatabase() {
    }

    public static GlobalLootDatabase getInstance() {
        return INSTANCE;
    }

    public void load() {
        try {
            if (Files.exists(storagePath)) {
                String json = Files.readString(storagePath, StandardCharsets.UTF_8);
                if (!json.isBlank()) {
                    Map<String, ItemEntry> loaded = GSON.fromJson(json, MAP_TYPE);
                    if (loaded != null) {
                        entries.clear();
                        entries.putAll(loaded);
                    }
                }
            }
        } catch (IOException ignored) {
        }
    }

    public void save() {
        try {
            Files.createDirectories(storagePath.getParent());
            Files.writeString(storagePath, GSON.toJson(entries), StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    public void setTag(String itemId, LootCategory category, ItemQuality quality) {
        if (itemId == null || itemId.isBlank()) {
            return;
        }
        entries.put(itemId, new ItemEntry(category, quality));
        save();
    }

    public Optional<ItemEntry> getEntry(String itemId) {
        if (itemId == null || itemId.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(entries.get(itemId));
    }

    public List<Map.Entry<String, ItemEntry>> getEntriesForCategory(LootCategory category) {
        List<Map.Entry<String, ItemEntry>> result = new ArrayList<>();
        for (Map.Entry<String, ItemEntry> entry : entries.entrySet()) {
            if (entry.getValue() != null && entry.getValue().category == category) {
                result.add(entry);
            }
        }
        return result;
    }

    public Optional<String> pickRandomItemId(LootCategory category, ItemQuality quality) {
        List<String> candidates = new ArrayList<>();
        for (Map.Entry<String, ItemEntry> entry : entries.entrySet()) {
            ItemEntry itemEntry = entry.getValue();
            if (itemEntry != null && itemEntry.category == category && itemEntry.quality == quality) {
                candidates.add(entry.getKey());
            }
        }
        if (candidates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(candidates.get(RANDOM.nextInt(candidates.size())));
    }

    public static class ItemEntry {
        private LootCategory category;
        private ItemQuality quality;

        public ItemEntry() {
            this(LootCategory.COLLECTIBLE, ItemQuality.WHITE);
        }

        public ItemEntry(LootCategory category, ItemQuality quality) {
            this.category = category;
            this.quality = quality;
        }

        public LootCategory getCategory() {
            return category;
        }

        public ItemQuality getQuality() {
            return quality;
        }
    }
}
