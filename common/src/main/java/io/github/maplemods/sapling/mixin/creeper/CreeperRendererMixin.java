package io.github.maplemods.sapling.mixin.creeper;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperRenderer.class)
public class CreeperRendererMixin {

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/monster/Creeper;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(Creeper creeper, CallbackInfoReturnable<ResourceLocation> cir) {
		if (!Textures.texturePairs.containsKey(EntityType.CREEPER)) {
			return;
		}

		ResourceLocation identifier = TextureFunctions.getCachedEntityTexture(creeper.getUUID());
		if (identifier == null) {
			return;
		}

		if (identifier.toString().contains("_default")) {
			return;
		}

		cir.setReturnValue(identifier);
	}
}