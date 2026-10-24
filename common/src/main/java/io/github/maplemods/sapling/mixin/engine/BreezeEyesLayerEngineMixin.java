package io.github.maplemods.sapling.mixin.engine;

import io.github.maplemods.sapling.render.TextureEngine;
import net.minecraft.resources.Identifier;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BreezeEyesLayer.class)
public class BreezeEyesLayerEngineMixin {

	@Shadow
	@Final
	private static RenderType BREEZE_EYES;

	// 178 is GETSTATIC (org.objectweb.asm is not on the common module's compile classpath)
	@Redirect(method = "submit", at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/layers/BreezeEyesLayer;BREEZE_EYES:Lnet/minecraft/client/renderer/rendertype/RenderType;", opcode = 178))
	private RenderType sapling$engineBreezeEyes() {
		Identifier replacement = TextureEngine.replacementFor(BREEZE_EYES);
		return replacement == null ? BREEZE_EYES : RenderTypes.breezeEyes(replacement);
	}
}
