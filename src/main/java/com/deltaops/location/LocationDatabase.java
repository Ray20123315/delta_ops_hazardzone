/*
 * Copyright (c) 2026 ray20123315. All Rights Reserved.
 * This file is part of "Delta Ops: Hazard Zone".
 * Proprietary and confidential.
 */
package com.deltaops.location;

import com.deltaops.DeltaOpsMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LocationDatabase {
    private static final Path STORAGE = Paths.get("config", DeltaOpsMod.MOD_ID, "locations.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Type DATA_TYPE = new TypeToken<DataStore>() {}.getType();

    private static DataStore store = loadInternal();

    public static class GlobalLobby {
        public String dimension; // ResourceLocation string
        public int x, y, z;
        public float yaw, pitch;

        public GlobalLobby() {}

        public GlobalLobby(String dimension, int x, int y, int z, float yaw, float pitch) {
            this.dimension = dimension;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    public static class SpawnPoint {
        public double x, y, z;
        public float yaw, pitch;

        public SpawnPoint() {}

        public SpawnPoint(double x, double y, double z, float yaw, float pitch) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
        }
    }

    private static class DataStore {
        public GlobalLobby globalLobby;
        public Map<String, List<SpawnPoint>> mapSpawns = new LinkedHashMap<>();
    }

    private static DataStore loadInternal() {
        LOCK.writeLock().lock();
        try {
            try {
                if (Files.exists(STORAGE)) {
                    String json = Files.readString(STORAGE, StandardCharsets.UTF_8);
                    if (json != null && !json.isBlank()) {
                        DataStore ds = GSON.fromJson(json, DATA_TYPE);
                        if (ds != null) return ds;
                    }
                }
            } catch (IOException ignored) {}
            return new DataStore();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    private static void persist() {
        LOCK.writeLock().lock();
        try {
            try {
                Files.createDirectories(STORAGE.getParent());
                Files.writeString(STORAGE, GSON.toJson(store, DATA_TYPE), StandardCharsets.UTF_8);
            } catch (IOException ignored) {}
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public static void setLobby(ServerLevel level, BlockPos pos, float yaw, float pitch) {
        if (level == null || pos == null) return;
        LOCK.writeLock().lock();
        try {
            String dim = level.dimension().location().toString();
            store.globalLobby = new GlobalLobby(dim, pos.getX(), pos.getY(), pos.getZ(), yaw, pitch);
            persist();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public static void addSpawnPoint(String mapName, double x, double y, double z, float yaw, float pitch) {
        if (mapName == null || mapName.isBlank()) return;
        LOCK.writeLock().lock();
        try {
            store.mapSpawns.computeIfAbsent(mapName, k -> new ArrayList<>()).add(new SpawnPoint(x, y, z, yaw, pitch));
            persist();
        } finally {
            LOCK.writeLock().unlock();
        }
    }

    public static Optional<SpawnPoint> getRandomSpawnPoint(String mapName) {
        if (mapName == null || mapName.isBlank()) return Optional.empty();
        LOCK.readLock().lock();
        try {
            List<SpawnPoint> list = store.mapSpawns.get(mapName);
            if (list == null || list.isEmpty()) return Optional.empty();
            int idx = ThreadLocalRandom.current().nextInt(list.size());
            return Optional.of(list.get(idx));
        } finally {
            LOCK.readLock().unlock();
        }
    }

    public static void teleportToLobby(ServerPlayer player) {
        if (player == null || player.server == null) return;
        LOCK.readLock().lock();
        try {
            if (store.globalLobby == null) return;
            String dim = store.globalLobby.dimension;
            // simple support for overworld; for other dimensions extend here
            ServerLevel target = player.server.overworld();
            if (target == null) return;
            try {
                player.teleportTo(target, store.globalLobby.x + 0.5, store.globalLobby.y + 0.1, store.globalLobby.z + 0.5, store.globalLobby.yaw, store.globalLobby.pitch);
            } catch (Exception ignored) {}
        } finally {
            LOCK.readLock().unlock();
        }
    }

    // Utility: list spawn points for a map (thread-safe snapshot)
    public static List<SpawnPoint> listSpawnPoints(String mapName) {
        LOCK.readLock().lock();
        try {
            List<SpawnPoint> list = store.mapSpawns.get(mapName);
            if (list == null) return Collections.emptyList();
            return new ArrayList<>(list);
        } finally {
            LOCK.readLock().unlock();
        }
    }

    // Utility: remove spawn point by index
    public static boolean removeSpawnPoint(String mapName, int index) {
        LOCK.writeLock().lock();
        try {
            List<SpawnPoint> list = store.mapSpawns.get(mapName);
            if (list == null || index < 0 || index >= list.size()) return false;
            list.remove(index);
            persist();
            return true;
        } finally {
            LOCK.writeLock().unlock();
        }
    }
}
