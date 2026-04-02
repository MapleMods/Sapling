package io.github.maplemods.sapling.services;

import io.github.maplemods.sapling.services.helpers.ModLoaderHelper;

import java.util.ServiceLoader;

public class Services {
    public static final ModLoaderHelper MODLOADER = load(ModLoaderHelper.class);

    public static <T> T load(Class<T> clazz) {
        return ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("[Sapling] Failed to load service for " + clazz.getName() + "."));
    }
}