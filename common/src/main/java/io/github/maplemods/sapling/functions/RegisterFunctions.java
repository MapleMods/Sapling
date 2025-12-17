package io.github.maplemods.sapling.functions;

import com.mojang.datafixers.util.Pair;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.data.Textures;
import net.minecraft.resources.ResourceLocation;

public class RegisterFunctions {
	public static void registerBeeTextures(ResourceLocation resourceLocation, double weight) {
		Textures.beeTexturePairs.add(Pair.of(resourceLocation, weight));

		Constants.LOG.info("[" + Constants.MOD_ID + "] Registered: {}, with weight: {}", resourceLocation, weight);
	}
}