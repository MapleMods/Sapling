package io.github.maplemods.sapling.data;

import io.github.maplemods.sapling.config.ConfigHandler;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.List;

public class Textures {
	public static HashMap<EntityType<?>, List<CategoryEntry>> texturePairs = new HashMap<>();
	public static HashMap<EntityType<?>, List<CategoryEntry>> defaultTexturePairs = new HashMap<>();
	public static HashMap<EntityType<?>, ConfigHandler> entityConfigs = new HashMap<>();
}