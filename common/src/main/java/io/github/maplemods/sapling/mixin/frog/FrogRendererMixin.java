package io.github.maplemods.sapling.mixin.frog;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrogRenderer.class)
public class FrogRendererMixin {

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/frog/Frog;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(Frog frog, CallbackInfoReturnable<ResourceLocation> cir) {
		if (!Textures.texturePairs.containsKey(EntityType.FROG)) {
			return;
		}

		ResourceLocation identifier = TextureFunctions.getCachedEntityTexture(frog.getUUID());
		if (identifier == null) {
			return;
		}

		if (identifier.toString().contains("_default")) {
			return;
		}

		cir.setReturnValue(identifier);
	}
}