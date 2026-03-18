package io.github.maplemods.sapling.cmd;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import commonnetwork.api.Dispatcher;
import io.github.maplemods.sapling.config.ConfigHandler;
import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.data.TextureEntry;
import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.ConfigFunctions;
import io.github.maplemods.sapling.functions.EntityTextureFunctions;
import io.github.maplemods.sapling.functions.TagFunctions;
import io.github.maplemods.sapling.networking.packets.ToClientSyncAllEntityTexturesPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.time.MonthDay;
import java.time.format.DateTimeParseException;
import java.util.*;

public class MapleModsCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		var entityTypeArg = Commands.argument("entityType", StringArgumentType.string()).suggests(CommandSuggestions.ENTITY_TYPE_SUGGESTIONS)
				.then(Commands.literal("updateTextureSettings")
						.then(Commands.argument("texture", StringArgumentType.string()).suggests(CommandSuggestions.TEXTURE_SUGGESTIONS)
								.then(Commands.literal("enable")
										.executes(MapleModsCommand::executeEnable))
								.then(Commands.literal("disable")
										.executes(MapleModsCommand::executeDisable))
								.then(Commands.literal("setWeight")
										.then(Commands.argument("weight", DoubleArgumentType.doubleArg(0.0))
												.executes(MapleModsCommand::executeSetWeight)))))
				.then(Commands.literal("updateCategorySettings")
						.then(Commands.argument("category", StringArgumentType.string()).suggests(CommandSuggestions.CATEGORY_SUGGESTIONS)
								.then(Commands.literal("enable")
										.executes(MapleModsCommand::executeEnableCategory))
								.then(Commands.literal("disable")
										.executes(MapleModsCommand::executeDisableCategory))
								.then(Commands.literal("setDate")
										.then(Commands.argument("from", StringArgumentType.string()).suggests(CommandSuggestions.DATE_FROM_SUGGESTIONS)
												.then(Commands.argument("to", StringArgumentType.string()).suggests(CommandSuggestions.DATE_TO_SUGGESTIONS)
														.executes(MapleModsCommand::executeSetDate))))))
				.then(Commands.literal("setConfigValues")
						.then(Commands.literal("replaceSeasonalTextures")
								.then(Commands.argument("value", BoolArgumentType.bool())
										.executes(MapleModsCommand::executeSetReplaceSeasonalTextures)))
						.then(Commands.literal("disableSeasonsDuringHoliday")
								.then(Commands.argument("value", BoolArgumentType.bool())
										.executes(MapleModsCommand::executeSetDisableSeasonsDuringHoliday)))
						.then(Commands.literal("nameTagPreventsTextureChange")
								.then(Commands.argument("value", BoolArgumentType.bool())
										.executes(MapleModsCommand::executeSetNameTagPreventsTextureChange)))
						.then(Commands.literal("_showCurrentValues")
										.executes(MapleModsCommand::executeGetConfig)))
				.then(Commands.literal("adminTools")
						.then(Commands.literal("reloadConfigFile")
								.executes(MapleModsCommand::executeReload))
						.then(Commands.literal("flipSeasonalDatesToOtherHemisphere")
								.executes(MapleModsCommand::flipSeasonalDatesToOtherHemisphere))
						.then(Commands.literal("resetAllLoadedEntityTextures")
								.executes(MapleModsCommand::executeResetAllLoadedEntityTextures)));

		dispatcher.register(Commands.literal("maplemods")
				.requires(source -> source.permissions().hasPermission(Permissions.COMMANDS_ADMIN))
				.then(entityTypeArg)
		);
	}

	// --- Texture commands ---

	private static int executeEnable(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		String textureName = StringArgumentType.getString(command, "texture");
		CategoryEntry category = resolveCategoryForTexture(entityType, textureName);
		TextureEntry entry = resolveTextureEntry(entityType, textureName);
		if (category == null || entry == null) {
			source.sendFailure(Component.literal("Unknown texture: " + textureName));
			return 0;
		}

		double defaultWeight = getDefaultWeight(entityType, entry.identifier);
		if (defaultWeight < 0) {
			source.sendFailure(Component.literal("No default weight found for: " + textureName));
			return 0;
		}

		replaceEntry(category, entry, entry.withEnabled(true).withWeight(defaultWeight));
		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Enabled '" + textureName + "' with default weight " + defaultWeight + ".").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeDisable(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		String textureName = StringArgumentType.getString(command, "texture");
		CategoryEntry category = resolveCategoryForTexture(entityType, textureName);
		TextureEntry entry = resolveTextureEntry(entityType, textureName);
		if (category == null || entry == null) {
			source.sendFailure(Component.literal("Unknown texture: " + textureName));
			return 0;
		}

		replaceEntry(category, entry, entry.withEnabled(false));
		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Disabled '" + textureName + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeSetWeight(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		String textureName = StringArgumentType.getString(command, "texture");
		CategoryEntry category = resolveCategoryForTexture(entityType, textureName);
		TextureEntry entry = resolveTextureEntry(entityType, textureName);
		if (category == null || entry == null) {
			source.sendFailure(Component.literal("Unknown texture: " + textureName));
			return 0;
		}

		double weight = DoubleArgumentType.getDouble(command, "weight");
		replaceEntry(category, entry, entry.withWeight(weight));
		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Set weight of '" + textureName + "' to " + weight + ".").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	// --- Category commands ---

	private static int executeEnableCategory(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		String categoryName = StringArgumentType.getString(command, "category");
		int count = setCategoryEnabled(entityType, categoryName, true);
		if (count == 0) {
			source.sendFailure(Component.literal("No textures found in category: " + categoryName));
			return 0;
		}

		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Enabled " + count + " texture(s) in category '" + categoryName + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeDisableCategory(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		String categoryName = StringArgumentType.getString(command, "category");
		int count = setCategoryEnabled(entityType, categoryName, false);
		if (count == 0) {
			source.sendFailure(Component.literal("No textures found in category: " + categoryName));
			return 0;
		}

		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Disabled " + count + " texture(s) in category '" + categoryName + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeSetDate(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		String categoryName = StringArgumentType.getString(command, "category");
		String fromStr = StringArgumentType.getString(command, "from");
		String toStr = StringArgumentType.getString(command, "to");

		MonthDay dateFrom;
		MonthDay dateTo;
		try {
			dateFrom = MonthDay.parse(fromStr, Constants.DATE_FORMATTER);
			dateTo = MonthDay.parse(toStr, Constants.DATE_FORMATTER);
		} catch (DateTimeParseException e) {
			source.sendFailure(Component.literal("Invalid date format. Use dd-MM, e.g. 01-12 for December 1st."));
			return 0;
		}

		List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
		if (categories == null) {
			source.sendFailure(Component.literal("No categories found for entity type."));
			return 0;
		}

		CategoryEntry existing = categories.stream()
				.filter(c -> c.category.equals(categoryName))
				.findFirst()
				.orElse(null);
		if (existing == null) {
			source.sendFailure(Component.literal("Unknown category: " + categoryName));
			return 0;
		}

		int index = categories.indexOf(existing);
		categories.set(index, existing.withDateRange(dateFrom, dateTo));

		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Set date range of '" + categoryName + "' to " + fromStr + " - " + toStr + ".").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	// --- Config commands ---

	private static int executeSetReplaceSeasonalTextures(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		boolean value = BoolArgumentType.getBool(command, "value");
		ConfigHandler handler = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
		handler.replaceSeasonalTextures = value;
		Textures.entityConfigs.put(entityType, handler);

		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Set replaceSeasonalTextures to " + value + " for '" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeSetDisableSeasonsDuringHoliday(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		boolean value = BoolArgumentType.getBool(command, "value");
		ConfigHandler handler = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
		handler.disableSeasonsDuringHoliday = value;
		Textures.entityConfigs.put(entityType, handler);

		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Set disableSeasonsDuringHoliday to " + value + " for '" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeSetNameTagPreventsTextureChange(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		boolean value = BoolArgumentType.getBool(command, "value");
		ConfigHandler handler = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
		handler.nameTagPreventsTextureChange = value;
		Textures.entityConfigs.put(entityType, handler);

		ConfigFunctions.writeTextureConfigFiles();
		source.sendSuccess(() -> Component.literal("Set nameTagPreventsTextureChange to " + value + " for '" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

	private static int executeGetConfig(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		ConfigHandler handler = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
		String entityTypeName = BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();

		source.sendSuccess(() -> Component.literal("Config for '" + entityTypeName + "':").withStyle(ChatFormatting.GOLD), false);
		source.sendSuccess(() -> Component.literal("  replaceSeasonalTextures: " + handler.replaceSeasonalTextures).withStyle(ChatFormatting.YELLOW), false);
		source.sendSuccess(() -> Component.literal("  disableSeasonsDuringHoliday: " + handler.disableSeasonsDuringHoliday).withStyle(ChatFormatting.YELLOW), false);
		source.sendSuccess(() -> Component.literal("  nameTagPreventsTextureChange: " + handler.nameTagPreventsTextureChange).withStyle(ChatFormatting.YELLOW), false);
		return 1;
	}

	// --- Other commands ---

	private static int executeReload(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
		if (entityType == null) {
			source.sendFailure(Component.literal("Unknown entity type."));
			return 0;
		}

		ConfigFunctions.reloadEntityConfig(source.getServer(), entityType);
		source.sendSuccess(() -> Component.literal("Reloaded config for '" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

    private static int flipSeasonalDatesToOtherHemisphere(CommandContext<CommandSourceStack> command) {
        CommandSourceStack source = command.getSource();
        EntityType<?> entityType = resolveEntityType(StringArgumentType.getString(command, "entityType"));
        if (entityType == null) {
            source.sendFailure(Component.literal("Unknown entity type."));
            return 0;
        }

        List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
        if (categories == null) {
            source.sendFailure(Component.literal("No categories found for entity type."));
            return 0;
        }

        CategoryEntry spring = categories.stream().filter(c -> c.category.equals("spring")).findFirst().orElse(null);
        CategoryEntry summer = categories.stream().filter(c -> c.category.equals("summer")).findFirst().orElse(null);
        CategoryEntry autumn = categories.stream().filter(c -> c.category.equals("autumn")).findFirst().orElse(null);
        CategoryEntry winter = categories.stream().filter(c -> c.category.equals("winter")).findFirst().orElse(null);

        if (spring == null || summer == null || autumn == null || winter == null) {
            source.sendFailure(Component.literal("Entity type is missing one or more seasonal categories (spring/summer/autumn/winter)."));
            return 0;
        }

        // Capture current dates before swapping
        MonthDay springFrom = spring.dateFrom;
        MonthDay springTo   = spring.dateTo;
        MonthDay summerFrom = summer.dateFrom;
        MonthDay summerTo   = summer.dateTo;
        MonthDay autumnFrom = autumn.dateFrom;
        MonthDay autumnTo   = autumn.dateTo;
        MonthDay winterFrom = winter.dateFrom;
        MonthDay winterTo   = winter.dateTo;

        // spring <-> autumn, summer <-> winter
        replaceCategory(categories, spring, spring.withDateRange(autumnFrom, autumnTo));
        replaceCategory(categories, summer, summer.withDateRange(winterFrom, winterTo));
        replaceCategory(categories, autumn, autumn.withDateRange(springFrom, springTo));
        replaceCategory(categories, winter, winter.withDateRange(summerFrom, summerTo));

        ConfigFunctions.writeTextureConfigFiles();
        source.sendSuccess(() -> Component.literal("Flipped seasonal dates to other hemisphere for '" + BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath() + "'.").withStyle(ChatFormatting.DARK_GREEN), true);
        return 1;
    }

	private static int executeResetAllLoadedEntityTextures(CommandContext<CommandSourceStack> command) {
		CommandSourceStack source = command.getSource();
		String prefix = Constants.MOD_ID + "++";
		int[] count = {0};
		Map<UUID, Identifier> syncMap = new HashMap<>();

		source.getServer().getAllLevels().forEach(level -> {
			level.getAllEntities().forEach(entity -> {
				if (!(entity instanceof LivingEntity livingEntity)) {
					return;
				}

				EntityType<?> entityType = livingEntity.getType();
				if (!Textures.texturePairs.containsKey(entityType)) {
					return;
				}

				ConfigHandler config = Textures.entityConfigs.getOrDefault(entityType, new ConfigHandler());
				if (config.nameTagPreventsTextureChange && livingEntity.hasCustomName()) {
					return;
				}

				// Remove existing texture tag
				livingEntity.getTags().removeIf(tag -> tag.startsWith(prefix));

				// Reassign immediately
				EntityTextureFunctions.assignTexture(livingEntity, entityType);

				// Add to sync map if a texture was assigned
				Identifier identifier = TagFunctions.getCustomEntityTextureIfExists(livingEntity);
				if (identifier != null) {
					syncMap.put(livingEntity.getUUID(), identifier);
				}

				count[0]++;
			});
		});

		Dispatcher.sendToAllClients(new ToClientSyncAllEntityTexturesPacket(syncMap), source.getServer());

		source.sendSuccess(() -> Component.literal("Reset and reassigned textures for " + count[0] + " entity/entities.").withStyle(ChatFormatting.DARK_GREEN), true);
		return 1;
	}

    private static void replaceCategory(List<CategoryEntry> categories, CategoryEntry oldEntry, CategoryEntry newEntry) {
        int index = categories.indexOf(oldEntry);
        if (index >= 0) {
            categories.set(index, newEntry);
        }
    }

	// --- Helpers ---

	private static int setCategoryEnabled(EntityType<?> entityType, String categoryName, boolean enabled) {
		List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
		if (categories == null) return 0;

		CategoryEntry category = categories.stream()
				.filter(c -> c.category.equals(categoryName))
				.findFirst()
				.orElse(null);
		if (category == null) return 0;

		int count = 0;
		for (int i = 0; i < category.textures.size(); i++) {
			TextureEntry entry = category.textures.get(i);
			double defaultWeight = getDefaultWeight(entityType, entry.identifier);
			double weight = enabled ? (defaultWeight < 0 ? entry.weight : defaultWeight) : entry.weight;
			category.textures.set(i, entry.withEnabled(enabled).withWeight(weight));
			count++;
		}
		return count;
	}

	public static EntityType<?> resolveEntityType(String entityTypeId) {
		Optional<EntityType<?>> entityType = BuiltInRegistries.ENTITY_TYPE.getOptional(Identifier.parse(entityTypeId));
		if (entityType.isPresent()) {
			return entityType.get();
		}

		return Textures.texturePairs.keySet().stream()
				.filter(e -> BuiltInRegistries.ENTITY_TYPE.getKey(e).getPath().equalsIgnoreCase(entityTypeId))
				.findFirst()
				.orElse(null);
	}

	private static CategoryEntry resolveCategoryForTexture(EntityType<?> entityType, String textureName) {
		List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
		if (categories == null) return null;

		return categories.stream()
				.filter(c -> c.textures.stream().anyMatch(e -> e.textureName.equals(textureName)))
				.findFirst()
				.orElse(null);
	}

	private static TextureEntry resolveTextureEntry(EntityType<?> entityType, String textureName) {
		List<CategoryEntry> categories = Textures.texturePairs.get(entityType);
		if (categories == null) return null;

		return categories.stream()
				.flatMap(c -> c.textures.stream())
				.filter(e -> e.textureName.equals(textureName))
				.findFirst()
				.orElse(null);
	}

	private static void replaceEntry(CategoryEntry category, TextureEntry oldEntry, TextureEntry newEntry) {
		int index = category.textures.indexOf(oldEntry);
		if (index >= 0) {
			category.textures.set(index, newEntry);
		}
	}

	private static double getDefaultWeight(EntityType<?> entityType, Identifier texture) {
		List<CategoryEntry> defaults = Textures.defaultTexturePairs.get(entityType);
		if (defaults == null) return -1;

		return defaults.stream()
				.flatMap(c -> c.textures.stream())
				.filter(e -> e.identifier.equals(texture))
				.mapToDouble(e -> e.weight)
				.findFirst()
				.orElse(-1);
	}
}