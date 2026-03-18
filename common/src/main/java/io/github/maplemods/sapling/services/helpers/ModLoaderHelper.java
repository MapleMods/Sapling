package io.github.maplemods.sapling.services.helpers;

public interface ModLoaderHelper {
    String getModLoaderName();
    String getGameDirectory();
    boolean isModLoaded(String modId);
    boolean isDevelopmentEnvironment();
    boolean isClientSide();
}