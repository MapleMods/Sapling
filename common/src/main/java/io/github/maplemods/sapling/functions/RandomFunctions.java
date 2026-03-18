package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.data.TextureEntry;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RandomFunctions {

	public static ResourceLocation getRandomTexture(List<CategoryEntry> categories) {
		List<TextureEntry> eligible = categories.stream()
				.filter(CategoryEntry::isDateActive)
				.flatMap(c -> c.textures.stream())
				.filter(TextureEntry::canSpawn)
				.toList();

		double totalWeight = eligible.stream()
				.mapToDouble(e -> e.weight)
				.sum();

		if (totalWeight <= 0.0D) {
			return null;
		}

		double value = Constants.randomSource.nextDouble() * totalWeight;

		for (TextureEntry entry : eligible) {
			value -= entry.weight;
			if (value <= 0.0D) {
				return entry.identifier;
			}
		}

		return eligible.getLast().identifier;
	}
}