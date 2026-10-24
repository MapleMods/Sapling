package io.github.maplemods.sapling.data;

import io.github.maplemods.sapling.config.ConfigHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Textures {
	public static HashMap<EntityType<?>, List<CategoryEntry>> texturePairs = new HashMap<>();
	public static HashMap<EntityType<?>, List<CategoryEntry>> defaultTexturePairs = new HashMap<>();
	public static HashMap<EntityType<?>, ConfigHandler> entityConfigs = new HashMap<>();
	public static HashMap<ResourceLocation, TextureEntry> byIdentifier = new HashMap<>();

	// Mobs whose collections let Sapling dress other mods' textures too (Sapling 3.0)
	public static Set<EntityType<?>> replaceModdedByDefault = new HashSet<>();

	// A player's config wins, else any installed collection that opts in
	public static boolean replacesModdedTextures(EntityType<?> entityType) {
		ConfigHandler handler = entityConfigs.get(entityType);
		if (handler != null && handler.replaceModdedTextures != null) {
			return handler.replaceModdedTextures;
		}
		return replaceModdedByDefault.contains(entityType);
	}
}