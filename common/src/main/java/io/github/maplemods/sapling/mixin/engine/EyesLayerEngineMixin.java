package io.github.maplemods.sapling.mixin.engine;

import io.github.maplemods.sapling.render.TextureEngine;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EyesLayer.class)
public class EyesLayerEngineMixin {

	@Redirect(method = "submit", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/layers/EyesLayer;renderType()Lnet/minecraft/client/renderer/rendertype/RenderType;"))
	private RenderType sapling$engineEyes(EyesLayer<?, ?> layer) {
		RenderType original = layer.renderType();
		Identifier replacement = TextureEngine.replacementFor(original);
		return replacement == null ? original : RenderTypes.eyes(replacement);
	}
}
