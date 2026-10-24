package io.github.maplemods.sapling.data;

import io.github.maplemods.sapling.functions.RegisterFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class TextureEntry {
    public final EntityType<?> entityType;
    public final String category;
    public final String textureName;
    public final double weight;
    public final boolean enabled;

    public final ResourceLocation identifier;
    public final String modId;

    // Texture engine (Sapling 3.0): vanilla texture -> replacement, shared by every copy of this entry
    public final Map<ResourceLocation, ResourceLocation> replacements;
    public final ResourceLocation[] any;
    public final boolean[] anyForBabies;

    public TextureEntry(EntityType<?> entityType, String category, String textureName, double weight, boolean enabled, String modId) {
        this(entityType, category, textureName, weight, enabled, modId, new HashMap<>(), new ResourceLocation[1], new boolean[1]);
    }

    private TextureEntry(EntityType<?> entityType, String category, String textureName, double weight, boolean enabled, String modId,
                         Map<ResourceLocation, ResourceLocation> replacements, ResourceLocation[] any, boolean[] anyForBabies) {
        this.entityType = entityType;
        this.category = category;
        this.textureName = textureName;
        this.weight = weight;
        this.enabled = enabled;

        this.identifier = new ResourceLocation(modId, "texture/entity/" + RegisterFunctions.getEntityTypeName(entityType) + "/" + category + "/" + textureName + ".png");
        this.modId = modId;

        this.replacements = replacements;
        this.any = any;
        this.anyForBabies = anyForBabies;
    }

    public TextureEntry withWeight(double newWeight) {
        return new TextureEntry(entityType, category, textureName, newWeight, enabled, modId, replacements, any, anyForBabies);
    }

    public TextureEntry withEnabled(boolean newEnabled) {
        return new TextureEntry(entityType, category, textureName, weight, newEnabled, modId, replacements, any, anyForBabies);
    }

    public boolean canSpawn() {
        return this.enabled && this.weight > 0.0D;
    }

    public boolean usesEngine() {
        return !this.replacements.isEmpty() || this.any[0] != null;
    }
}
