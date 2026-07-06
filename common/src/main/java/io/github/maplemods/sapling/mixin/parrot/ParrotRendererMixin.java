package io.github.maplemods.sapling.mixin.parrot;

import io.github.maplemods.sapling.functions.EntityRenderFunctions;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.parrot.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParrotRenderer.class)
public class ParrotRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/parrot/Parrot;Lnet/minecraft/client/renderer/entity/state/ParrotRenderState;F)V", at = @At("TAIL"))
	private void sapling$attachParrot(Parrot parrot, ParrotRenderState state, float partialTicks, CallbackInfo ci) {
		((EntityRenderStateExt) state).sapling$setEntity(parrot);
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/ParrotRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(ParrotRenderState parrotRenderState, CallbackInfoReturnable<Identifier> cir) {
		if (parrotRenderState.isBaby) {
			return;
		}

		Identifier identifier = EntityRenderFunctions.resolveTexture((EntityRenderStateExt) parrotRenderState, EntityTypes.PARROT);
		if (identifier != null) {
			cir.setReturnValue(identifier);
		}
	}
}