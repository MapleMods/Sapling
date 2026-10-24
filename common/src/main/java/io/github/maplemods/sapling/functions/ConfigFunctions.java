package io.github.maplemods.sapling.functions;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ConfigFunctions {

	private static final int CONFIG_FORMAT = 2;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM");
	private static final DateTimeFormatter DATE_PARSER = DateTimeFormatter.ofPattern("d-M");
	private static final String KNOWN_FILE = "sapling_known_textures.json";

	private static final Set<EntityType<?>> failedToLoad = new HashSet<>();

	public static void initConfigAtLaunch() {
		Constants.LOG.info("[" + Constants.MOD_NAME + "] Writing and/or loading texture config files.");
		loadAndWriteConfig();
	}

	public static void initConfig(MinecraftServer minecraftServer) {
		Constants.LOG.info("[" + Constants.MOD_NAME + "] Loading texture config files.");
		loadAndWriteConfig();
	}

	private static synchronized void loadAndWriteConfig() {
		Path configDirectory = TextureFunctions.getTextureConfigPath();

		try {
			Files.createDirectories(configDirectory);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		Map<String, Set<String>> known = readKnownTextures(configDirectory);
		failedToLoad.clear();
		for (EntityType<?> entityType : new ArrayList<>(Textures.texturePairs.keySet())) {
			loadEntityConfigFile(configDirectory, entityType, known);
		}

		writeTextureConfigFiles(configDirectory);
		writeKnownTextures(configDirectory, known);
	}

	public static synchronized void reloadEntityConfig(MinecraftServer server, EntityType<?> entityType) {
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

		Map<String, Set<String>> known = readKnownTextures(configDirectory);
		failedToLoad.remove(entityType);
		loadEntityConfigFile(configDirectory, entityType, known);
		writeTextureConfigFiles(configDirectory);
		writeKnownTextures(configDirectory, known);
	}

	private static void loadEntityConfigFile(Path configDirectory, EntityType<?> entityType, Map<String, Set<String>> known) {
		String entityTypeName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
		Path filePath = configDirectory.resolve(entityTypeName + ".json");

		// Always ensure a default config exists for this entity type
		Textures.entityConfigs.putIfAbsent(entityType, new ConfigHandler());

		if (!Files.exists(filePath)) {
			return;
		}

		try {
			JsonObject root = Constants.GSON.fromJson(Files.readString(filePath), JsonObject.class);
			if (root == null) {
				throw new IllegalStateException("the file is empty");
			}

			ConfigHandler handler = new ConfigHandler();
			ConfigHandler current = Textures.entityConfigs.get(entityType);
			copyConfig(current, handler);

			if (root.has("_config")) {
				JsonObject config = root.getAsJsonObject("_config");

				if (config.has("replaceSeasonalTextures")) {
					handler.replaceSeasonalTextures = config.get("replaceSeasonalTextures").getAsBoolean();
				}
				if (config.has("disableSeasonsDuringHoliday")) {
					handler.disableSeasonsDuringHoliday = config.get("disableSeasonsDuringHoliday").getAsBoolean();
				}
				if (config.has("nameTagPreventsTextureChange")) {
					handler.nameTagPreventsTextureChange = config.get("nameTagPreventsTextureChange").getAsBoolean();
				}
				if (config.has("replaceModdedTextures")) {
					handler.replaceModdedTextures = config.get("replaceModdedTextures").getAsBoolean();
				}
			}

			List<CategoryEntry> loadedCategories = null;
			if (root.has("spawn_settings")) {
				loadedCategories = readCategories(root.getAsJsonObject("spawn_settings"), entityType, entityTypeName);
			}

			copyConfig(handler, current);
			if (loadedCategories == null) {
				return;
			}

			mergeWithCode(loadedCategories, entityType, known.getOrDefault(entityTypeName, Set.of()));
			Textures.texturePairs.put(entityType, loadedCategories);

		} catch (Exception e) {
			failedToLoad.add(entityType);
			Constants.LOG.warn("[" + Constants.MOD_NAME + "] Could not read config/maplemods/textures/{}.json ({}). "
					+ "The file is left as it is and {} uses its default textures until it is fixed, or deleted to get a fresh one.",
					entityTypeName, e.getMessage(), entityTypeName);
		}
	}

	private static void copyConfig(ConfigHandler from, ConfigHandler to) {
		to.replaceSeasonalTextures = from.replaceSeasonalTextures;
		to.disableSeasonsDuringHoliday = from.disableSeasonsDuringHoliday;
		to.nameTagPreventsTextureChange = from.nameTagPreventsTextureChange;
		to.replaceModdedTextures = from.replaceModdedTextures;
	}

	private static List<CategoryEntry> readCategories(JsonObject spawnSettings, EntityType<?> entityType, String entityTypeName) {
		List<CategoryEntry> codeDefaults = Textures.defaultTexturePairs.getOrDefault(entityType, List.of());
		List<CategoryEntry> loadedCategories = new ArrayList<>();

		for (String category : spawnSettings.keySet()) {
			JsonObject categoryObj = spawnSettings.getAsJsonObject(category);
			CategoryEntry codeCategory = findCategory(codeDefaults, category);

			MonthDay dateFrom = codeCategory != null ? codeCategory.dateFrom : CategoryEntry.defaultDateFrom();
			MonthDay dateTo = codeCategory != null ? codeCategory.dateTo : CategoryEntry.defaultDateTo();
			if (categoryObj.has("date")) {
				JsonObject date = categoryObj.getAsJsonObject("date");
				try {
					dateFrom = MonthDay.parse(date.get("from").getAsString().trim(), DATE_PARSER);
					dateTo = MonthDay.parse(date.get("to").getAsString().trim(), DATE_PARSER);
				} catch (Exception e) {
					Constants.LOG.warn("[" + Constants.MOD_NAME + "] {}.json: the date of '{}' is not a day-month date like 24-12 ({}). "
							+ "Using {} to {} instead; its textures are kept.", entityTypeName, category, e.getMessage(),
							dateFrom.format(DATE_FORMATTER), dateTo.format(DATE_FORMATTER));
				}
			}

			List<TextureEntry> textures = new ArrayList<>();
			JsonArray texturesArray = categoryObj.has("textures") ? categoryObj.getAsJsonArray("textures") : new JsonArray();
			for (JsonElement textureElement : texturesArray) {
				JsonObject inner = textureElement.getAsJsonObject();
				String textureName = inner.get("name").getAsString();
				double weight = inner.has("weight") ? inner.get("weight").getAsDouble() : 1.0;
				boolean enabled = !inner.has("enabled") || inner.get("enabled").getAsBoolean();
				textures.add(entryFor(codeDefaults, entityType, category, textureName, weight, enabled));
			}

			loadedCategories.add(new CategoryEntry(category, dateFrom, dateTo, textures));
		}
		return loadedCategories;
	}

	private static TextureEntry entryFor(List<CategoryEntry> codeDefaults, EntityType<?> entityType, String category, String textureName,
			double weight, boolean enabled) {
		CategoryEntry codeCategory = findCategory(codeDefaults, category);
		if (codeCategory != null) {
			for (TextureEntry code : codeCategory.textures) {
				if (code.textureName.equals(textureName)) {
					return code.withWeight(weight).withEnabled(enabled);
				}
			}
		}

		String modId = codeDefaults.stream()
				.flatMap(c -> c.textures.stream())
				.filter(e -> e.textureName.equals(textureName))
				.map(e -> e.modId)
				.findFirst()
				.orElse(Constants.MOD_ID);
		return new TextureEntry(entityType, category, textureName, weight, enabled, modId);
	}

	private static CategoryEntry findCategory(List<CategoryEntry> categories, String category) {
		for (CategoryEntry entry : categories) {
			if (entry.category.equals(category)) {
				return entry;
			}
		}
		return null;
	}

	private static void mergeWithCode(List<CategoryEntry> loadedCategories, EntityType<?> entityType, Set<String> knownBefore) {
		List<CategoryEntry> codeDefaults = Textures.defaultTexturePairs.get(entityType);
		if (codeDefaults == null) {
			return;
		}

		for (CategoryEntry loadedCategory : loadedCategories) {
			CategoryEntry codeCategory = findCategory(codeDefaults, loadedCategory.category);
			if (codeCategory != null) {
				loadedCategory.textures.removeIf(loaded ->
						codeCategory.textures.stream().noneMatch(code -> code.identifier.equals(loaded.identifier)));
			}
		}
		// Remove categories that no longer exist in code (except _default which is always kept)
		loadedCategories.removeIf(loaded ->
				!loaded.category.equals("_default") &&
				codeDefaults.stream().noneMatch(c -> c.category.equals(loaded.category)));

		for (CategoryEntry codeCategory : codeDefaults) {
			CategoryEntry existing = findCategory(loadedCategories, codeCategory.category);
			if (existing == null) {
				List<TextureEntry> textures = new ArrayList<>();
				for (TextureEntry codeEntry : codeCategory.textures) {
					textures.add(cameBack(codeEntry, knownBefore));
				}
				loadedCategories.add(new CategoryEntry(codeCategory.category, codeCategory.dateFrom, codeCategory.dateTo, textures));
				continue;
			}
			for (TextureEntry codeEntry : codeCategory.textures) {
				boolean alreadyExists = existing.textures.stream()
						.anyMatch(e -> e.identifier.equals(codeEntry.identifier));
				if (!alreadyExists) {
					existing.textures.add(cameBack(codeEntry, knownBefore));
				}
			}
		}
	}

	private static TextureEntry cameBack(TextureEntry codeEntry, Set<String> knownBefore) {
		if (knownBefore.contains(codeEntry.identifier.toString())) {
			return codeEntry.withEnabled(false);
		}
		return codeEntry;
	}

	private static Path knownPath(Path configDirectory) {
		return configDirectory.getParent().resolve(KNOWN_FILE);
	}

	private static Map<String, Set<String>> readKnownTextures(Path configDirectory) {
		Map<String, Set<String>> known = new HashMap<>();
		Path path = knownPath(configDirectory);
		if (!Files.exists(path)) {
			return known;
		}

		try {
			JsonObject root = Constants.GSON.fromJson(Files.readString(path), JsonObject.class);
			if (root == null) {
				return known;
			}
			for (String entityTypeName : root.keySet()) {
				Set<String> identifiers = new HashSet<>();
				for (JsonElement element : root.getAsJsonArray(entityTypeName)) {
					identifiers.add(element.getAsString());
				}
				known.put(entityTypeName, identifiers);
			}
		} catch (Exception e) {
			Constants.LOG.warn("[" + Constants.MOD_NAME + "] Could not read {} ({}); deleted textures may come back enabled once.",
					KNOWN_FILE, e.getMessage());
		}
		return known;
	}

	private static void writeKnownTextures(Path configDirectory, Map<String, Set<String>> known) {
		for (Map.Entry<EntityType<?>, List<CategoryEntry>> entry : Textures.texturePairs.entrySet()) {
			if (failedToLoad.contains(entry.getKey())) {
				continue;
			}
			Set<String> identifiers = new HashSet<>();
			for (CategoryEntry category : entry.getValue()) {
				for (TextureEntry texture : category.textures) {
					identifiers.add(texture.identifier.toString());
				}
			}
			known.put(BuiltInRegistries.ENTITY_TYPE.getKey(entry.getKey()).getPath(), identifiers);
		}

		JsonObject root = new JsonObject();
		known.keySet().stream().sorted().forEach(entityTypeName -> {
			JsonArray identifiers = new JsonArray();
			known.get(entityTypeName).stream().sorted().forEach(identifiers::add);
			root.add(entityTypeName, identifiers);
		});
		writeIfChanged(knownPath(configDirectory), Constants.GSON.toJson(root));
	}

	public static void writeTextureConfigFiles() {
		writeTextureConfigFiles(TextureFunctions.getTextureConfigPath());
	}

	public static synchronized void writeTextureConfigFiles(Path configDirectory) {
		for (Map.Entry<EntityType<?>, List<CategoryEntry>> entry : Textures.texturePairs.entrySet()) {
			EntityType<?> entityType = entry.getKey();
			List<CategoryEntry> categories = entry.getValue();
			if (categories == null || categories.isEmpty()) {
				continue;
			}

			String entityTypeName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
			if (failedToLoad.contains(entityType)) {
				Constants.LOG.warn("[" + Constants.MOD_NAME + "] Not saving config/maplemods/textures/{}.json: it did not load, so it is left as it is.",
						entityTypeName);
				continue;
			}

			categories.sort(Comparator.comparingInt(ConfigFunctions::categorySortOrder).thenComparing(c -> c.category));

			ConfigHandler handler = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
			JsonObject configObject = new JsonObject();
			configObject.addProperty("replaceSeasonalTextures", handler.replaceSeasonalTextures);
			configObject.addProperty("disableSeasonsDuringHoliday", handler.disableSeasonsDuringHoliday);
			configObject.addProperty("nameTagPreventsTextureChange", handler.nameTagPreventsTextureChange);
			configObject.addProperty("replaceModdedTextures", Textures.replacesModdedTextures(entityType));

			Path filePath = configDirectory.resolve(entityTypeName + ".json");
			writeIfChanged(filePath, Constants.GSON.toJson(getJsonElements(categories, configObject)));
		}
	}

	private static void writeIfChanged(Path filePath, String content) {
		try {
			if (Files.exists(filePath) && Files.readString(filePath).equals(content)) {
				return;
			}
			Files.writeString(filePath, content);
		} catch (IOException e) {
			e.printStackTrace();
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
