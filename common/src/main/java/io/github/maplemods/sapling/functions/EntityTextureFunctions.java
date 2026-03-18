package io.github.maplemods.sapling.functions;

import commonnetwork.api.Dispatcher;
import io.github.maplemods.sapling.config.ConfigHandler;
import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.networking.packets.ToClientSendEntityTextureDataPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.Optional;

public class EntityTextureFunctions {

	public static void assignTexture(LivingEntity entity, EntityType<?> entityType) {
		List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
		if (categories == null || categories.isEmpty()) {
			return;
		}

		ConfigHandler config = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());

		if (TagFunctions.hasCustomTexture(entity)) {
			if (config.nameTagPreventsTextureChange && entity.hasCustomName()) {
				return;
			}

			if (!config.replaceSeasonalTextures) {
				migrateTagIfNeeded(entity, categories);
				return;
			}

			// Migrate first so we're working with the most current identifier
			migrateTagIfNeeded(entity, categories);

			// Now check if the (possibly migrated) tag is expired
			boolean expired = isCurrentTextureCategoryExpired(entity, categories);
			if (!expired) {
				return;
			}

			// Category has expired — strip tag and reassign from seasonal only
			String prefix = Constants.MOD_ID + "++";
			entity.getTags().removeIf(tag -> tag.startsWith(prefix));

			List<CategoryEntry> reassignPool = buildSpawnPool(categories, config, true);
			Identifier baseIdentifier = RandomFunctions.getRandomTexture(reassignPool);
			if (baseIdentifier == null) {
				return;
			}

			entity.getTags().add(Constants.MOD_ID + "++" + baseIdentifier.toString().replace(":", "--"));
			syncTextureToTrackingPlayers(entity, baseIdentifier);
			return;
		}

		// No existing tag — assign from pool based on config
		List<CategoryEntry> spawnPool = buildSpawnPool(categories, config, false);
		Identifier baseIdentifier = RandomFunctions.getRandomTexture(spawnPool);
		if (baseIdentifier == null) {
			return;
		}

		entity.getTags().add(Constants.MOD_ID + "++" + baseIdentifier.toString().replace(":", "--"));
		syncTextureToTrackingPlayers(entity, baseIdentifier);
	}

	private static List<CategoryEntry> buildSpawnPool(List<CategoryEntry> categories, ConfigHandler config, boolean expiredReassign) {
		boolean hasActiveHoliday = categories.stream()
				.filter(c -> !c.category.startsWith("_"))
				.filter(c -> !Constants.SEASONAL_CATEGORIES.contains(c.category))
				.anyMatch(CategoryEntry::isDateActive);

		return categories.stream()
				.filter(c -> {
					if (c.category.startsWith("_")) {
						return !expiredReassign;
					}

					boolean isSeasonal = Constants.SEASONAL_CATEGORIES.contains(c.category);
					boolean isHoliday = !isSeasonal;

					if (config.disableSeasonsDuringHoliday && hasActiveHoliday && isSeasonal) {
						return false;
					}

					return !isHoliday || c.isDateActive();
				})
				.toList();
	}

	private static void syncTextureToTrackingPlayers(LivingEntity entity, Identifier identifier) {
		if (!(entity.level() instanceof ServerLevel serverLevel)) {
			return;
		}

		ToClientSendEntityTextureDataPacket packet = new ToClientSendEntityTextureDataPacket(entity.getUUID(), identifier);

		List<ServerPlayer> tracking = serverLevel.getChunkSource().chunkMap.getPlayers(entity.chunkPosition(), false);
		tracking.forEach(player -> Dispatcher.sendToClient(packet, player));
	}

	private static boolean isCurrentTextureCategoryExpired(LivingEntity entity, List<CategoryEntry> categories) {
		Identifier identifier = TagFunctions.getCustomEntityTextureIfExists(entity);
		if (identifier == null) {
			return false;
		}

		String path = identifier.getPath();
		String[] parts = path.split("/");
		if (parts.length < 4) {
			return false;
		}
		String categoryName = parts[parts.length - 2];

		if (categoryName.startsWith("_")) {
			return false;
		}

		return categories.stream()
				.filter(c -> c.category.equals(categoryName))
				.findFirst()
				.map(c -> !c.isDateActive())
				.orElse(false);
	}

	private static void migrateTagIfNeeded(LivingEntity entity, List<CategoryEntry> categories) {
		String prefix = Constants.MOD_ID + "++";

		Optional<String> existingTag = entity.getTags().stream()
				.filter(tag -> tag.startsWith(prefix))
				.findFirst();

		if (existingTag.isEmpty()) {
			return;
		}

		String identifierStr = existingTag.get().substring(prefix.length()).replace("--", ":");
		Identifier identifier = Identifier.parse(identifierStr);

		boolean exactMatch = categories.stream()
				.flatMap(c -> c.textures.stream())
				.anyMatch(e -> e.identifier.equals(identifier));

		if (exactMatch) {
			return;
		}

		String textureName = identifier.getPath()
				.substring(identifier.getPath().lastIndexOf('/') + 1)
				.replace(".png", "");

		categories.stream()
				.flatMap(c -> c.textures.stream())
				.filter(e -> e.textureName.equals(textureName))
				.findFirst()
				.ifPresent(match -> {
					entity.getTags().remove(existingTag.get());
					entity.getTags().add(prefix + match.identifier.toString().replace(":", "--"));
					syncTextureToTrackingPlayers(entity, match.identifier);
				});
	}
}