package io.github.maplemods.sapling.functions;

import io.github.maplemods.sapling.data.Constants;
import net.minecraft.IdentifierException;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;

import java.util.Set;

public class TagFunctions {
	public static boolean hasCustomTexture(Entity entity) {
		Set<String> tags = entity.entityTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.MOD_ID + "++")) {
				return true;
			}
		}
		return false;
	}

	public static Identifier getCustomEntityTextureIfExists(Entity entity) {
		Set<String> tags = entity.entityTags();
		for (String tag : tags) {
			if (tag.startsWith(Constants.MOD_ID + "++")) {
				String rawIdentifier = tag.split("\\+\\+")[1];

				Identifier identifier;
				try {
					identifier = Identifier.parse(rawIdentifier.replace("--", ":"));
				}
				catch (IdentifierException ex) {
					return null;
				}

				return identifier;
			}
		}

		return null;
	}
}
