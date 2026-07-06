package io.github.maplemods.sapling.mixin.creeper;

import io.github.maplemods.sapling.functions.EntityRenderFunctions;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.CreeperRenderer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.Creeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreeperRenderer.class)
public class CreeperRendererMixin {

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/monster/Creeper;Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;F)V", at = @At("TAIL"))
	private void sapling$attachCreeper(Creeper creeper, CreeperRenderState state, float partialTicks, CallbackInfo ci) {
		((EntityRenderStateExt) state).sapling$setEntity(creeper);
	}

	@Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/CreeperRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
	public void getTextureLocation(CreeperRenderState creeperRenderState, CallbackInfoReturnable<Identifier> cir) {
		if (creeperRenderState.isBaby) {
			return;
		}

		Identifier identifier = EntityRenderFunctions.resolveTexture((EntityRenderStateExt) creeperRenderState, EntityTypes.CREEPER);
		if (identifier != null) {
			cir.setReturnValue(identifier);
		}
	}
}