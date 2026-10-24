package io.github.maplemods.sapling.mixin.engine;

import io.github.maplemods.sapling.render.TextureEngine;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTypes.class)
public class RenderTypesEngineMixin {

	@ModifyVariable(method = {"entityCutout", "entityCutoutCull", "entityCutoutDissolve", "entityCutoutZOffset", "entitySolid", "entitySolidZOffsetForward", "entityTranslucent", "entityTranslucentCull", "entityTranslucentEmissive", "outline"}, at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private static Identifier sapling$engineLayer(Identifier texture) {
		return TextureEngine.remapLayer(texture);
	}

	@Inject(method = {"eyes", "breezeEyes"}, at = @At("RETURN"))
	private static void sapling$engineOrigin(Identifier texture, CallbackInfoReturnable<RenderType> cir) {
		TextureEngine.recordOrigin(cir.getReturnValue(), texture);
	}
}
