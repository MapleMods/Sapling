package io.github.maplemods.sapling.mixin.chicken;

import io.github.maplemods.sapling.functions.EntityRenderFunctions;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.ChickenRenderer;
import net.minecraft.client.renderer.entity.state.ChickenRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.chicken.Chicken;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChickenRenderer.class)
public class ChickenRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/chicken/Chicken;Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;F)V", at = @At("TAIL"))
	private void sapling$attachChicken(Chicken chicken, ChickenRenderState state, float partialTicks, CallbackInfo ci) {
		((EntityRenderStateExt) state).sapling$setEntity(chicken);
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/ChickenRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(ChickenRenderState chickenRenderState, CallbackInfoReturnable<Identifier> cir) {
		if (chickenRenderState.isBaby) {
			return;
		}

		Identifier identifier = EntityRenderFunctions.resolveTexture((EntityRenderStateExt) chickenRenderState, EntityTypes.CHICKEN);
		if (identifier != null) {
			cir.setReturnValue(identifier);
		}
	}
}