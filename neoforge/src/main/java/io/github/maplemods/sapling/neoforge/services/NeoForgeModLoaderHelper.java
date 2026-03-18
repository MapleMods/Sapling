package io.github.maplemods.sapling.neoforge.services;

import io.github.maplemods.sapling.services.helpers.ModLoaderHelper;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;

public class NeoForgeModLoaderHelper implements ModLoaderHelper {
	@Override
	public String getModLoaderName() {
		return "NeoForge";
	}

	@Override
	public String getGameDirectory() {
		return FMLPaths.GAMEDIR.get().toString();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLEnvironment.isProduction();
	}

	@Override
	public boolean isClientSide() {
		return FMLEnvironment.getDist().equals(Dist.CLIENT);
	}
}