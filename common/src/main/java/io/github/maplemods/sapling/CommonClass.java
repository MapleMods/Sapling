package io.github.maplemods.sapling;

import io.github.maplemods.sapling.data.Constants;

public class CommonClass {

    public static void init() {
        Constants.LOG.info("[" + Constants.MOD_NAME + "] Loaded " + Constants.MOD_NAME + " version " + Constants.MOD_VERSION + ".");
    }
}