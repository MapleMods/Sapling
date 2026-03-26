package io.github.maplemods.sapling.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.maplemods.sapling.config.ConfigHandler;
import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.data.TextureEntry;
import io.github.maplemods.sapling.data.Textures;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ConfigFunctions {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM");

	public static void initConfig(MinecraftServer minecraftServer) {
		Constants.LOG.info("[" + Constants.MOD_NAME + "] Writing and/or loading texture config files.");

		Path configDirectory = TextureFunctions.getTextureConfigPath();

		try {
			Files.createDirectories(configDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		loadTextureConfigFiles(configDirectory);
		writeTextureConfigFiles(configDirectory);
	}

	public static void reloadEntityConfig(MinecraftServer server, EntityType<?> entityType) {
		Path configDirectory = TextureFunctions.getTextureConfigPath();

		try {
			Files.createDirectories(configDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		List<CategoryEntry> defaults = Textures.defaultTexturePairs.get(entityType);
		if (defaults != null) {
			List<CategoryEntry> deepCopy = new ArrayList<>();
			for (CategoryEntry c : defaults) {
				deepCopy.add(new CategoryEntry(c.category, c.dateFrom, c.dateTo, new ArrayList<>(c.textures)));
			}
			Textures.texturePairs.put(entityType, deepCopy);
		}

		// Reset config to defaults before reloading
		Textures.entityConfigs.put(entityType, new ConfigHandler());

		loadEntityConfigFile(configDirectory, entityType);
		writeTextureConfigFiles(configDirectory);
	}

	private static void loadTextureConfigFiles(Path configDirectory) {
		for (EntityType<?> entityType : Textures.texturePairs.keySet()) {
			loadEntityConfigFile(configDirectory, entityType);
		}
	}

	private static void loadEntityConfigFile(Path configDirectory, EntityType<?> entityType) {
		String entityTypeName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
		Path filePath = configDirectory.resolve(entityTypeName + ".json");

		// Always ensure a default config exists for this entity type
		Textures.entityConfigs.putIfAbsent(entityType, new ConfigHandler());

		if (!Files.exists(filePath)) {
			return;
		}

		try {
			JsonObject root = Constants.GSON.fromJson(Files.readString(filePath), JsonObject.class);

			// Read and apply _config block
			if (root.has("_config")) {
				JsonObject config = root.getAsJsonObject("_config");
				ConfigHandler handler = Textures.entityConfigs.get(entityType);

				if (config.has("replaceSeasonalTextures")) {
					handler.replaceSeasonalTextures = config.get("replaceSeasonalTextures").getAsBoolean();
				}
				if (config.has("disableSeasonsDuringHoliday")) {
					handler.disableSeasonsDuringHoliday = config.get("disableSeasonsDuringHoliday").getAsBoolean();
				}
				if (config.has("nameTagPreventsTextureChange")) {
					handler.nameTagPreventsTextureChange = config.get("nameTagPreventsTextureChange").getAsBoolean();
				}
			}

			JsonObject spawnSettings = root.has("spawn_settings")
					? root.getAsJsonObject("spawn_settings")
					: null;

			if (spawnSettings == null) {
				return;
			}

			List<CategoryEntry> loadedCategories = new ArrayList<>();

			for (String category : spawnSettings.keySet()) {
				try {
					JsonObject categoryObj = spawnSettings.getAsJsonObject(category);

					MonthDay dateFrom = CategoryEntry.defaultDateFrom();
					MonthDay dateTo = CategoryEntry.defaultDateTo();
					if (categoryObj.has("date")) {
						JsonObject date = categoryObj.getAsJsonObject("date");
						dateFrom = MonthDay.parse(date.get("from").getAsString(), DATE_FORMATTER);
						dateTo = MonthDay.parse(date.get("to").getAsString(), DATE_FORMATTER);
					}

					JsonArray texturesArray = categoryObj.getAsJsonArray("textures");
					List<TextureEntry> textures = new ArrayList<>();
					for (var textureElement : texturesArray) {
						try {
							JsonObject inner = textureElement.getAsJsonObject();
							String textureName = inner.get("name").getAsString();
							double weight = inner.get("weight").getAsDouble();
							boolean enabled = !inner.has("enabled") || inner.get("enabled").getAsBoolean();

							// Look up modId from already-registered entries, fall back to MOD_ID
							String modId = Constants.MOD_ID;
							List<CategoryEntry> registered = Textures.texturePairs.get(entityType);
							if (registered != null) {
								modId = registered.stream()
										.flatMap(c -> c.textures.stream())
										.filter(e -> e.textureName.equals(textureName))
										.map(e -> e.modId)
										.findFirst()
										.orElse(Constants.MOD_ID);
							}

							textures.add(new TextureEntry(entityType, category, textureName, weight, enabled, modId));
						} catch (Exception e) {
							Constants.LOG.warn("[" + Constants.MOD_NAME + "] Skipping malformed texture entry in {}.json: {}", entityTypeName, e.getMessage());
						}
					}

					loadedCategories.add(new CategoryEntry(category, dateFrom, dateTo, textures));
				} catch (Exception e) {
					Constants.LOG.warn("[" + Constants.MOD_NAME + "] Skipping malformed category entry in {}.json: {}", entityTypeName, e.getMessage());
				}
			}

			// Merge: keep config values for known textures, add new code textures, remove stale entries
			List<CategoryEntry> codeDefaults = Textures.defaultTexturePairs.get(entityType);
			if (codeDefaults != null) {
				// Remove entries from loaded config that are no longer registered in code
				for (CategoryEntry loadedCategory : loadedCategories) {
					CategoryEntry codeCategory = codeDefaults.stream()
							.filter(c -> c.category.equals(loadedCategory.category))
							.findFirst()
							.orElse(null);

					if (codeCategory != null) {
						loadedCategory.textures.removeIf(loaded ->
								codeCategory.textures.stream().noneMatch(code -> code.identifier.equals(loaded.identifier)));
					}
				}
				// Remove categories that no longer exist in code (except _default which is always kept)
				loadedCategories.removeIf(loaded ->
						!loaded.category.equals("_default") &&
						codeDefaults.stream().noneMatch(c -> c.category.equals(loaded.category)));

				// Add new categories/textures from code not present in the config
				for (CategoryEntry codeCategory : codeDefaults) {
					CategoryEntry existing = loadedCategories.stream()
							.filter(c -> c.category.equals(codeCategory.category))
							.findFirst()
							.orElse(null);

					if (existing == null) {
						loadedCategories.add(new CategoryEntry(codeCategory.category, codeCategory.dateFrom, codeCategory.dateTo, new ArrayList<>(codeCategory.textures)));
					} else {
						for (TextureEntry codeEntry : codeCategory.textures) {
							boolean alreadyExists = existing.textures.stream()
									.anyMatch(e -> e.identifier.equals(codeEntry.identifier));
							if (!alreadyExists) {
								existing.textures.add(codeEntry);
							}
						}
					}
				}
			}

			Textures.texturePairs.put(entityType, loadedCategories);

		} catch (Exception e) {
			Constants.LOG.warn("[" + Constants.MOD_NAME + "] Failed to load {}.json, regenerating: {}", entityTypeName, e.getMessage());
		}
	}

	public static void writeTextureConfigFiles() {
		writeTextureConfigFiles(TextureFunctions.getTextureConfigPath());
	}

	public static void writeTextureConfigFiles(Path configDirectory) {
		for (Map.Entry<EntityType<?>, List<CategoryEntry>> entry : Textures.texturePairs.entrySet()) {
			EntityType<?> entityType = entry.getKey();
			List<CategoryEntry> categories = entry.getValue();
			if (categories == null || categories.isEmpty()) {
				continue;
			}

			categories.sort(Comparator.comparingInt(ConfigFunctions::categorySortOrder).thenComparing(c -> c.category));

			ConfigHandler handler = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
			JsonObject configObject = new JsonObject();
			configObject.addProperty("replaceSeasonalTextures", handler.replaceSeasonalTextures);
			configObject.addProperty("disableSeasonsDuringHoliday", handler.disableSeasonsDuringHoliday);
			configObject.addProperty("nameTagPreventsTextureChange", handler.nameTagPreventsTextureChange);

			String entityTypeName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
			Path filePath = configDirectory.resolve(entityTypeName + ".json");
			try {
				Files.writeString(filePath, Constants.GSON.toJson(getJsonElements(categories, configObject)));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static @NotNull JsonObject getJsonElements(List<CategoryEntry> categories, JsonObject configObject) {
		JsonObject root = new JsonObject();

		root.add("_config", configObject);

		JsonObject spawnSettings = new JsonObject();
		for (CategoryEntry categoryEntry : categories) {
			JsonObject categoryData = new JsonObject();

			JsonObject date = new JsonObject();
			date.addProperty("from", categoryEntry.dateFrom.format(DATE_FORMATTER));
			date.addProperty("to", categoryEntry.dateTo.format(DATE_FORMATTER));
			categoryData.add("date", date);

			categoryEntry.textures.sort(Comparator.comparing(e -> e.textureName));
			JsonArray texturesArray = new JsonArray();
			for (TextureEntry textureEntry : categoryEntry.textures) {
				JsonObject jsonEntry = new JsonObject();
				jsonEntry.addProperty("name", textureEntry.textureName);
				jsonEntry.addProperty("weight", textureEntry.weight);
				jsonEntry.addProperty("enabled", textureEntry.enabled);
				texturesArray.add(jsonEntry);
			}

			categoryData.add("textures", texturesArray);
			spawnSettings.add(categoryEntry.category, categoryData);
		}

		root.add("spawn_settings", spawnSettings);
		return root;
	}

	private static int categorySortOrder(CategoryEntry c) {
		return switch (c.category) {
			case "_default" -> 0;
			case "spring"   -> 1;
			case "summer"   -> 2;
			case "autumn"   -> 3;
			case "winter"   -> 4;
			default         -> 5;
		};
	}
}