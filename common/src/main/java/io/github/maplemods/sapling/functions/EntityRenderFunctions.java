package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.UUID;

public class EntityRenderFunctions {

	public static Identifier resolveTexture(EntityRenderStateExt renderState, EntityType<?> entityType) {
		List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
		if (categories == null || categories.isEmpty()) {
			return null;
		}

		LivingEntity entity = renderState.sapling$getEntity();
		if (entity == null) {
			return null;
		}

		UUID uuid = entity.getUUID();

		Identifier identifier = TextureFunctions.getCachedEntityTexture(uuid);
		if (identifier == null) {
			return null;
		}

		if (identifier.toString().contains("_default")) {
			return null;
		}

		return identifier;
	}
}