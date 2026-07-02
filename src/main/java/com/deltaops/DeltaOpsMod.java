package com.deltaops;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public final class DeltaOpsMod {
    public static final String MOD_ID = "delta_ops_hazardzone";
    public static final Logger LOGGER = LogUtils.getLogger();

    private DeltaOpsMod() {
    }

    public static void logLegacyModLoaded() {
        LOGGER.info("三角洲行動 - 烽火地帶已載入");
    }
}