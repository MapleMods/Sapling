package io.github.maplemods.sapling.fabric.services;

import io.github.maplemods.sapling.services.helpers.ModLoaderHelper;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public class FabricModLoaderHelper implements ModLoaderHelper {
    @Override
    public String getModLoaderName() {
        return "Fabric";
    }

    @Override
    public String getGameDirectory() {
        return FabricLoader.getInstance().getGameDir().toString();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public boolean isClientSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }
}
