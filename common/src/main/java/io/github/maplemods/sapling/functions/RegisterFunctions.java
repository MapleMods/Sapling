package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
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

    @Deprecated
    public static void registerBeeTextures(Identifier textureIdentifier, double weight) {
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