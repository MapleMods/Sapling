package io.github.maplemods.sapling.mixin.engine;

import io.github.maplemods.sapling.render.TextureEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderType.class)
public class RenderTypeEngineMixin {

	@ModifyVariable(method = {"entityCutout", "entityCutoutNoCull", "entityCutoutNoCullZOffset", "entityDecal", "entityNoOutline", "entitySmoothCutout", "entitySolid", "entityTranslucent", "entityTranslucentCull", "entityTranslucentEmissive", "itemEntityTranslucentCull", "outline"}, at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private static ResourceLocation sapling$engineLayer(ResourceLocation texture) {
		return TextureEngine.remapLayer(texture);
	}

	@Inject(method = {"eyes", "breezeEyes"}, at = @At("RETURN"))
	private static void sapling$engineOrigin(ResourceLocation texture, CallbackInfoReturnable<RenderType> cir) {
		TextureEngine.recordOrigin(cir.getReturnValue(), texture);
	}
}
