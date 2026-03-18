package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.Constants;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public class TagFunctions {
	public static boolean hasCustomTexture(Entity entity) {
		Set<String> tags = entity.getTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.MOD_ID + "++")) {
				return true;
			}
		}
		return false;
	}

	public static ResourceLocation getCustomEntityTextureIfExists(Entity entity) {
		Set<String> tags = entity.getTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.MOD_ID + "++")) {
				String rawIdentifier = tag.split("\\+\\+")[1];

				ResourceLocation identifier;
				try {
					identifier = new ResourceLocation(rawIdentifier.replace("--", ":"));
				}
				catch (ResourceLocationException ex) {
					return null;
				}

				return identifier;
			}
		}

		return null;
	}
}
