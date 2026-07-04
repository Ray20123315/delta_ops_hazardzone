package com.deltaops.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.LongValue DAM_REQUIRED_VALUE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> ITEM_VALUE_MAPPING;

    public static final ForgeConfigSpec SPEC;

    static {
        BUILDER.comment("Combat value and trader pricing configuration");

        DAM_REQUIRED_VALUE = BUILDER.comment("Minimum combat value required to enter the dam dimension")
                .defineInRange("dam_required_value", 112500L, 0L, Long.MAX_VALUE);

        ITEM_VALUE_MAPPING = BUILDER.comment("Item value mapping entries in format modid:itemid=value")
                .define("item_value_mapping", List.of(
                        "tacz:m4a1=35000",
                        "tacz:ak47=32000"
                ));

        SPEC = BUILDER.build();
    }

    public static long getItemValue(ResourceLocation registryName) {
        if (registryName == null) {
            return 1500L;
        }

        String target = registryName.toString();
        for (String entry : ITEM_VALUE_MAPPING.get()) {
            if (entry == null || entry.isBlank()) {
                continue;
            }

            String trimmed = entry.trim();
            int splitIndex = trimmed.indexOf('=');
            if (splitIndex < 0) {
                continue;
            }

            String key = trimmed.substring(0, splitIndex).trim();
            String value = trimmed.substring(splitIndex + 1).trim();
            if (key.isBlank() || value.isBlank()) {
                continue;
            }

            if (key.equals(target)) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException ignored) {
                    return 1500L;
                }
            }
        }

        return 1500L;
    }
}
