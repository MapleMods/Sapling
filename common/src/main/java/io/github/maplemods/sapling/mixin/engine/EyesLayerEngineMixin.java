package io.github.maplemods.sapling.mixin.engine;

import io.github.maplemods.sapling.render.TextureEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EyesLayer.class)
public class EyesLayerEngineMixin {

	@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EyesLayer;renderType()Lnet/minecraft/client/renderer/RenderType;"))
	private RenderType sapling$engineEyes(EyesLayer<?, ?> layer) {
		RenderType original = layer.renderType();
		ResourceLocation replacement = TextureEngine.replacementFor(original);
		return replacement == null ? original : RenderType.eyes(replacement);
	}
}
