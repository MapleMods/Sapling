package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.CategoryEntry;
import io.github.maplemods.sapling.data.TextureEntry;
import io.github.maplemods.sapling.data.Constants;
import net.minecraft.resources.Identifier;

import java.util.List;

public class RandomFunctions {

	public static Identifier getRandomTexture(List<CategoryEntry> categories) {
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