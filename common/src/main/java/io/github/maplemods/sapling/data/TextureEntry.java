package io.github.maplemods.sapling.data;

import io.github.maplemods.sapling.functions.RegisterFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class TextureEntry {
    public final EntityType<?> entityType;
    public final String category;
    public final String textureName;
    public final double weight;
    public final boolean enabled;

    public final ResourceLocation identifier;
    public final String modId;

    public TextureEntry(EntityType<?> entityType, String category, String textureName, double weight, boolean enabled, String modId) {
        this.entityType = entityType;
        this.category = category;
        this.textureName = textureName;
        this.weight = weight;
        this.enabled = enabled;

        this.identifier = ResourceLocation.fromNamespaceAndPath(modId, "texture/entity/" + RegisterFunctions.getEntityTypeName(entityType) + "/" + category + "/" + textureName + ".png");
        this.modId = modId;
    }

    public TextureEntry withWeight(double newWeight) {
        return new TextureEntry(entityType, category, textureName, newWeight, enabled, modId);
    }

    public TextureEntry withEnabled(boolean newEnabled) {
        return new TextureEntry(entityType, category, textureName, weight, newEnabled, modId);
    }

    public boolean canSpawn() {
        return this.enabled && this.weight > 0.0D;
    }
}