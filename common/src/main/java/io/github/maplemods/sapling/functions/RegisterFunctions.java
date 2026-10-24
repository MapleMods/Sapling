package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.time.MonthDay;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisterFunctions {
    public static void registerTextures(String modId, EntityType<?> entityType, String category, String textureName, double weight) {
        if (!Textures.texturePairs.containsKey(entityType)) {
            Textures.texturePairs.put(entityType, new ArrayList<>());
            Textures.defaultTexturePairs.put(entityType, new ArrayList<>());

            TextureEntry defaultEntry = new TextureEntry(entityType, "_default", "normal_" + getEntityTypeName(entityType), 1.0, true, modId);
            Textures.texturePairs.get(entityType).add(new CategoryEntry("_default", CategoryEntry.defaultDateFrom(), CategoryEntry.defaultDateTo(), new ArrayList<>(List.of(defaultEntry))));
            Textures.defaultTexturePairs.get(entityType).add(new CategoryEntry("_default", CategoryEntry.defaultDateFrom(), CategoryEntry.defaultDateTo(), new ArrayList<>(List.of(defaultEntry))));
        }

        TextureEntry entry = new TextureEntry(entityType, category, textureName, weight, true, modId);
        Textures.byIdentifier.putIfAbsent(entry.identifier, entry);

        // Find existing category or create a new one
        addToCategories(Textures.texturePairs.get(entityType), entry, category);

        // Same for defaultTexturePairs
        addToCategories(Textures.defaultTexturePairs.get(entityType), entry, category);

        Constants.LOG.info("[" + Constants.MOD_NAME + "] Registered: {}, in category: {}, with weight: {}", textureName, category, weight);
    }

    private static void addToCategories(List<CategoryEntry> categories, TextureEntry entry, String category) {
        CategoryEntry existing = categories.stream()
                .filter(c -> c.category.equals(category))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            boolean alreadyExists = existing.textures.stream()
                    .anyMatch(e -> e.textureName.equals(entry.textureName));
            if (!alreadyExists) {
                existing.textures.add(entry);
            }
        } else {
            CategoryEntry newCategory = new CategoryEntry(category, CategoryDefaults.getDateFrom(category), CategoryDefaults.getDateTo(category), new ArrayList<>(List.of(entry)));
            categories.add(newCategory);
        }
    }

    // Texture engine (Sapling 3.0). vanillaPath is "entity/cat/cat_calico" (no "textures/", no ".png"),
    // "*" for the design's generic adult file, "*baby" to let babies wear that generic file too.
    public static void registerReplacement(String modId, EntityType<?> entityType, String category, String textureName, String vanillaPath, String file) {
        String folder = "texture/entity/" + getEntityTypeName(entityType) + "/" + category + "/";
        ResourceLocation key = new ResourceLocation(modId, folder + textureName + ".png");
        TextureEntry entry = Textures.byIdentifier.get(key);
        if (entry == null) {
            Constants.LOG.error("[" + Constants.MOD_NAME + "] registerReplacement before registerTextures: {}", key);
            return;
        }

        if (vanillaPath.equals("*baby")) {
            entry.anyForBabies[0] = true;
            return;
        }

        ResourceLocation replacement = new ResourceLocation(modId, folder + file + ".png");
        if (vanillaPath.equals("*")) {
            entry.any[0] = replacement;
            return;
        }

        entry.replacements.put(new ResourceLocation("minecraft", "textures/" + vanillaPath + ".png"), replacement);
    }

    // Collection defaults (Sapling 3.0). A collection that does not blend in with the plain Minecraft look
    // switches the vanilla entry off for its mobs; one that may dress other mods' textures opts in.
    // A player's config file still wins.
    public static void hideVanillaTextureByDefault(EntityType<?> entityType) {
        hideVanillaEntry(Textures.texturePairs.get(entityType));
        hideVanillaEntry(Textures.defaultTexturePairs.get(entityType));
    }

    private static void hideVanillaEntry(List<CategoryEntry> categories) {
        if (categories == null) {
            return;
        }
        for (CategoryEntry category : categories) {
            if (category.category.equals("_default")) {
                category.textures.replaceAll(entry -> new TextureEntry(entry.entityType, entry.category, entry.textureName, entry.weight, false, entry.modId));
            }
        }
    }

    public static void replaceModdedTexturesByDefault(EntityType<?> entityType) {
        Textures.replaceModdedByDefault.add(entityType);
    }

    public static void registerCategory(String modId, String category, String kind) {
        registerCategory(modId, category, kind, 1, 1, 12, 31);
    }

    public static void registerCategory(String modId, String category, String kind, int fromMonth, int fromDay, int toMonth, int toDay) {
        if (!CategoryDefaults.register(modId, category, kind, MonthDay.of(fromMonth, fromDay), MonthDay.of(toMonth, toDay))) {
            return;
        }

        MonthDay from = CategoryDefaults.getDateFrom(category);
        MonthDay to = CategoryDefaults.getDateTo(category);
        for (HashMap<EntityType<?>, List<CategoryEntry>> pairs : List.of(Textures.texturePairs, Textures.defaultTexturePairs)) {
            for (List<CategoryEntry> categories : pairs.values()) {
                categories.replaceAll(c -> c.category.equals(category) ? c.withDateRange(from, to) : c);
            }
        }

        Constants.LOG.info("[" + Constants.MOD_NAME + "] Registered category: {} ({}), by {}", category, kind, modId);
    }

    @Deprecated
    public static void registerBeeTextures(ResourceLocation textureIdentifier, double weight) {
        // Extract texture name from path: texture/entity/bee/{textureName}.png
        String path = textureIdentifier.getPath();
        String textureName = path.substring(path.lastIndexOf('/') + 1).replace(".png", "");
        String modId = textureIdentifier.getNamespace();

        registerTextures(modId, EntityType.BEE, "christmas", textureName, weight);
    }

    public static String getEntityTypeName(EntityType<?> entityType) {
        return BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getPath();
    }
}