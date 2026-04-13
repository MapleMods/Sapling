package io.github.maplemods.sapling;

import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.functions.RegisterFunctions;
import io.github.maplemods.sapling.networking.PacketRegistration;
import net.minecraft.world.entity.EntityType;

public class CommonMod {

    public static void init() {
        new PacketRegistration().init();

        Constants.LOG.info("[" + Constants.MOD_NAME + "] Loaded " + Constants.MOD_NAME + " version " + Constants.MOD_VERSION + ".");
    }
}