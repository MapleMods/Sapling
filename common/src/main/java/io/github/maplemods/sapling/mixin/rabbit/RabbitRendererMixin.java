package io.github.maplemods.sapling.mixin.rabbit;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.RabbitRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Rabbit;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RabbitRenderer.class)
public class RabbitRendererMixin {

	@Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Rabbit;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(Rabbit rabbit, CallbackInfoReturnable<ResourceLocation> cir) {
		if (!Textures.texturePairs.containsKey(EntityType.RABBIT)) {
			return;
		}

		ResourceLocation identifier = TextureFunctions.getCachedEntityTexture(rabbit.getUUID());
		if (identifier == null) {
			return;
		}

		if (identifier.toString().contains("_default")) {
			return;
		}

		cir.setReturnValue(identifier);
	}
}