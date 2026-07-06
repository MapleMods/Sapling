package io.github.maplemods.sapling.mixin.pig;

import io.github.maplemods.sapling.functions.EntityRenderFunctions;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.PigRenderer;
import net.minecraft.client.renderer.entity.state.PigRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PigRenderer.class)
public class PigRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/pig/Pig;Lnet/minecraft/client/renderer/entity/state/PigRenderState;F)V", at = @At("TAIL"))
	private void sapling$attachPig(Pig pig, PigRenderState state, float partialTicks, CallbackInfo ci) {
		((EntityRenderStateExt) state).sapling$setEntity(pig);
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/PigRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(PigRenderState pigRenderState, CallbackInfoReturnable<Identifier> cir) {
		if (pigRenderState.isBaby) {
			return;
		}

		Identifier identifier = EntityRenderFunctions.resolveTexture((EntityRenderStateExt) pigRenderState, EntityTypes.PIG);
		if (identifier != null) {
			cir.setReturnValue(identifier);
		}
	}
}