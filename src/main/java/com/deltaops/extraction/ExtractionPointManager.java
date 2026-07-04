package com.deltaops.extraction;

import com.deltaops.DeltaOpsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ExtractionPointManager {
    private static final Path STORAGE = FMLPaths.CONFIGDIR.get().resolve(DeltaOpsMod.MOD_ID).resolve("extraction_points.json");
    private static final Map<String, BlockPos> EXTRACTION_POINTS = new LinkedHashMap<>();

    static {
        load();
    }

    public static boolean registerExtractionPoint(net.minecraft.server.level.ServerPlayer player, String mapName) {
        if (player == null || mapName == null || mapName.isBlank()) {
            return false;
        }
        if (!com.deltaops.lobby.HazardMapRegistry.getAllMaps().containsKey(mapName)) {
            return false;
        }
        BlockPos pos = player.blockPosition();
        EXTRACTION_POINTS.put(mapName, pos);
        save();
        return true;
    }

    public static Optional<BlockPos> getExtractionPoint(String mapName) {
        if (mapName == null || mapName.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(EXTRACTION_POINTS.get(mapName));
    }

    public static Optional<String> getExtractionMapForPlayer(net.minecraft.world.entity.player.Player player) {
        if (player == null || player.level().isClientSide) {
            return Optional.empty();
        }
        BlockPos position = player.blockPosition();
        for (Map.Entry<String, BlockPos> entry : EXTRACTION_POINTS.entrySet()) {
            if (entry.getValue().distSqr(position) <= 3 * 3) {
                return Optional.of(entry.getKey());
            }
        }
        return Optional.empty();
    }

    public static Map<String, BlockPos> getAllExtractionPoints() {
        return Map.copyOf(EXTRACTION_POINTS);
    }

    private static void save() {
        try {
            Files.createDirectories(STORAGE.getParent());
            CompoundTag root = new CompoundTag();
            for (Map.Entry<String, BlockPos> entry : EXTRACTION_POINTS.entrySet()) {
                CompoundTag posTag = new CompoundTag();
                posTag.putInt("x", entry.getValue().getX());
                posTag.putInt("y", entry.getValue().getY());
                posTag.putInt("z", entry.getValue().getZ());
                root.put(entry.getKey(), posTag);
            }
            Files.writeString(STORAGE, root.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            DeltaOpsMod.LOGGER.error("Failed to save extraction points", e);
        }
    }

    private static void load() {
        if (!Files.exists(STORAGE)) {
            return;
        }
        try {
            String json = Files.readString(STORAGE, StandardCharsets.UTF_8);
            if (json == null || json.isBlank()) {
                return;
            }
            CompoundTag root = net.minecraft.nbt.TagParser.parseTag(json);
            for (String key : root.getAllKeys()) {
                CompoundTag posTag = root.getCompound(key);
                EXTRACTION_POINTS.put(key, new BlockPos(posTag.getInt("x"), posTag.getInt("y"), posTag.getInt("z")));
            }
        } catch (Exception e) {
            DeltaOpsMod.LOGGER.error("Failed to load extraction points", e);
        }
    }
}
