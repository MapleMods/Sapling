package io.github.maplemods.sapling.event;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.EntityTextureFunctions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class SaplingEvents {

	public static void onEntityJoinLevel(Level level, Entity entity) {
		if (level.isClientSide()) {
			return;
		}

		EntityType<?> entityType = entity.getType();

		if (!Textures.texturePairs.containsKey(entityType)) {
			return;
		}

		if (!(entity instanceof LivingEntity livingEntity)) {
			return;
		}

		EntityTextureFunctions.assignTexture(livingEntity, entityType);
	}
}