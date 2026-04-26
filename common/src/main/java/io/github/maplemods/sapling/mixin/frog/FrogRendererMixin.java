package io.github.maplemods.sapling.mixin.frog;

import io.github.maplemods.sapling.functions.EntityRenderFunctions;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.FrogRenderer;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.frog.Frog;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FrogRenderer.class)
public class FrogRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/frog/Frog;Lnet/minecraft/client/renderer/entity/state/FrogRenderState;F)V", at = @At("TAIL"))
	private void sapling$attachChicken(Frog frog, FrogRenderState state, float partialTicks, CallbackInfo ci) {
		((EntityRenderStateExt) state).sapling$setEntity(frog);
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/FrogRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(FrogRenderState frogRenderState, CallbackInfoReturnable<Identifier> cir) {
		if (frogRenderState.isBaby) {
			return;
		}

		Identifier identifier = EntityRenderFunctions.resolveTexture((EntityRenderStateExt) frogRenderState, EntityType.FROG);
		if (identifier != null) {
			cir.setReturnValue(identifier);
		}
	}
}