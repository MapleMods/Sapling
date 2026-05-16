package io.github.maplemods.sapling.mixin.parrot;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParrotRenderer.class)
public class ParrotRendererMixin {

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Parrot;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(Parrot parrot, CallbackInfoReturnable<ResourceLocation> cir) {
		if (!Textures.texturePairs.containsKey(EntityType.PARROT)) {
			return;
		}

		ResourceLocation identifier = TextureFunctions.getCachedEntityTexture(parrot.getUUID());
		if (identifier == null) {
			return;
		}

		if (identifier.toString().contains("_default")) {
			return;
		}

		cir.setReturnValue(identifier);
	}
}