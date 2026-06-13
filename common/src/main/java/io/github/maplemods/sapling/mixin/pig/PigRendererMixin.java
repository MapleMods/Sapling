package io.github.maplemods.sapling.mixin.pig;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigRenderer.class)
public class PigRendererMixin {

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Pig;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(Pig pig, CallbackInfoReturnable<ResourceLocation> cir) {
		if (!Textures.texturePairs.containsKey(EntityType.PIG)) {
			return;
		}

		ResourceLocation identifier = TextureFunctions.getCachedEntityTexture(pig.getUUID());
		if (identifier == null) {
			return;
		}

		if (identifier.toString().contains("_default")) {
			return;
		}

		cir.setReturnValue(identifier);
	}
}