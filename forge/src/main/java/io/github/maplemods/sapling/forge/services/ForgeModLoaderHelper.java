package io.github.maplemods.sapling.forge.services;

import io.github.maplemods.sapling.services.helpers.ModLoaderHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

public class ForgeModLoaderHelper implements ModLoaderHelper {
	@Override
	public String getModLoaderName() {
		return "Forge";
	}

	@Override
	public String getGameDirectory() {
		return FMLPaths.GAMEDIR.get().toString();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

	@Override
	public boolean isClientSide() {
		return FMLEnvironment.dist.equals(Dist.CLIENT);
	}
}