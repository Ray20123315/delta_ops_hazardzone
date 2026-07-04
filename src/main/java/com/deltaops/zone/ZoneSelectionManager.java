/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.zone;

import com.deltaops.DeltaOpsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

public class ZoneSelectionManager {
    private static final Map<UUID, BlockPos> POS1 = new ConcurrentHashMap<>();
    private static final Map<UUID, BlockPos> POS2 = new ConcurrentHashMap<>();
    private static final Path STORAGE = Paths.get("config", DeltaOpsMod.MOD_ID, "zones.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type ZONE_MAP_TYPE = new TypeToken<LinkedHashMap<String, Zone>>() {}.getType();

    public enum SearchPriority { HIGH_PRIORITY, MID_PRIORITY, LOW_PRIORITY, RNG }

    public static void setPos1(UUID player, BlockPos pos) {
        if (player == null || pos == null) return;
        POS1.put(player, pos);
    }

    public static void setPos2(UUID player, BlockPos pos) {
        if (player == null || pos == null) return;
        POS2.put(player, pos);
    }

    public static Optional<BlockPos> getPos1(UUID player) { return Optional.ofNullable(POS1.get(player)); }
    public static Optional<BlockPos> getPos2(UUID player) { return Optional.ofNullable(POS2.get(player)); }

    public static void saveZone(String name, SearchPriority priority, UUID owner) {
        if (name == null || name.isBlank()) return;
        BlockPos a = POS1.get(owner);
        BlockPos b = POS2.get(owner);
        if (a == null || b == null) return;
        Zone z = new Zone(name, a, b, priority);
        Map<String, Zone> zones = loadZonesMap();
        zones.put(name, z);
        writeZones(zones);
    }

    public static Map<String, Zone> loadZonesMap() {
        try {
            if (Files.exists(STORAGE)) {
                String json = Files.readString(STORAGE, StandardCharsets.UTF_8);
                if (!json.isBlank()) {
                    Map<String, Zone> loaded = GSON.fromJson(json, ZONE_MAP_TYPE);
                    if (loaded != null) return new LinkedHashMap<>(loaded);
                }
            }
        } catch (IOException ignored) {}
        return new LinkedHashMap<>();
    }

    private static void writeZones(Map<String, Zone> z) {
        try {
            Files.createDirectories(STORAGE.getParent());
            Files.writeString(STORAGE, GSON.toJson(z), StandardCharsets.UTF_8);
        } catch (IOException ignored) {}
    }

    public static class Zone {
        public String name;
        public BlockPos a;
        public BlockPos b;
        public SearchPriority priority;

        public Zone() {}

        public Zone(String name, BlockPos a, BlockPos b, SearchPriority priority) {
            this.name = name;
            this.a = a;
            this.b = b;
            this.priority = priority;
        }

        public BlockPos min() {
            return new BlockPos(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()), Math.min(a.getZ(), b.getZ()));
        }

        public BlockPos max() {
            return new BlockPos(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()), Math.max(a.getZ(), b.getZ()));
        }
    }
}
