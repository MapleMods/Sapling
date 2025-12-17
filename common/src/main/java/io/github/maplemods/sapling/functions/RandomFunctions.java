package io.github.maplemods.sapling.functions;

import com.mojang.datafixers.util.Pair;
import io.github.maplemods.sapling.data.Constants;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class RandomFunctions {
	public static ResourceLocation getRandomBeeTexture(List<Pair<ResourceLocation, Double>> listInput) {
		double totalWeight = 0.0D;

		for (Pair<ResourceLocation, Double> pair : listInput) {
			totalWeight += pair.getSecond();
		}

		if (totalWeight <= 0.0D) {
			return null;
		}

		double value = Constants.randomSource.nextDouble() * totalWeight;

		for (Pair<ResourceLocation, Double> pair : listInput) {
			value -= pair.getSecond();

			if (value <= 0.0D) {
				return pair.getFirst();
			}
		}

		return listInput.getLast().getFirst();
	}
}
