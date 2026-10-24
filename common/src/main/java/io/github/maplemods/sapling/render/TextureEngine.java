package io.github.maplemods.sapling.render;

import io.github.maplemods.sapling.data.TextureEntry;
import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class TextureEngine {
	private static final ArrayDeque<Frame> FRAMES = new ArrayDeque<>();
	private static final Map<Object, Identifier> ORIGINS = Collections.synchronizedMap(new WeakHashMap<>());
	private static Thread renderThread = null;

	private record Frame(TextureEntry entry, boolean baby) {
	}

	public static void begin(LivingEntity entity) {
		renderThread = Thread.currentThread();
		if (FRAMES.size() > 32) {
			FRAMES.clear();
		}
		FRAMES.push(new Frame(entryFor(entity), entity != null && entity.isBaby()));
	}

	public static void end() {
		if (!FRAMES.isEmpty()) {
			FRAMES.pop();
		}
	}

	private static TextureEntry entryFor(LivingEntity entity) {
		if (entity == null || !Textures.texturePairs.containsKey(entity.getType())) {
			return null;
		}

		Identifier identifier = TextureFunctions.getCachedEntityTexture(entity.getUUID());
		if (identifier == null || identifier.toString().contains("_default")) {
			return null;
		}

		TextureEntry entry = Textures.byIdentifier.get(identifier);
		if (entry == null || entry.entityType != entity.getType()) {
			return null;
		}
		return entry;
	}

	private static Frame current() {
		if (Thread.currentThread() != renderThread) {
			return null;
		}
		return FRAMES.peek();
	}

	public static Identifier remapMain(Identifier vanilla) {
		Frame frame = current();
		if (frame == null || frame.entry() == null || vanilla == null) {
			return vanilla;
		}

		TextureEntry entry = frame.entry();
		Identifier exact = entry.replacements.get(vanilla);
		if (exact != null) {
			return exact;
		}

		// Another mod's texture for this mob: left alone unless its config says otherwise
		if (!vanilla.getNamespace().equals("minecraft") && !Textures.replacesModdedTextures(entry.entityType)) {
			return vanilla;
		}

		if (frame.baby() && !entry.anyForBabies[0]) {
			return vanilla;
		}

		if (entry.any[0] != null) {
			return entry.any[0];
		}

		if (!entry.usesEngine() && !frame.baby()) {
			return entry.identifier;
		}
		return vanilla;
	}

	public static Identifier remapLayer(Identifier texture) {
		Frame frame = current();
		if (frame == null || frame.entry() == null || texture == null) {
			return texture;
		}

		Identifier exact = frame.entry().replacements.get(texture);
		return exact != null ? exact : texture;
	}

	public static void recordOrigin(Object renderType, Identifier texture) {
		if (renderType != null && texture != null) {
			ORIGINS.putIfAbsent(renderType, texture);
		}
	}

	public static Identifier replacementFor(Object renderType) {
		Frame frame = current();
		if (frame == null || frame.entry() == null || renderType == null) {
			return null;
		}

		Identifier origin = ORIGINS.get(renderType);
		if (origin == null) {
			return null;
		}
		return frame.entry().replacements.get(origin);
	}
}
