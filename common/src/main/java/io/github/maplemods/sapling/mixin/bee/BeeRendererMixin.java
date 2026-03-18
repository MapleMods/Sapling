package io.github.maplemods.sapling.mixin.bee;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeRenderer.class)
public class BeeRendererMixin {

    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Bee;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    public void getTextureLocation(Bee bee, CallbackInfoReturnable<ResourceLocation> cir) {
        if (!Textures.texturePairs.containsKey(EntityType.BEE)) {
            return;
        }

        ResourceLocation base = TextureFunctions.getCachedEntityTexture(bee.getUUID());
        if (base == null || base.toString().contains("_default")) {
            return;
        }

        ResourceLocation resolved;
        if (bee.isAngry()) {
            resolved = bee.hasNectar()
                    ? fromBase(base, "_angry_nectar.png")
                    : fromBase(base, "_angry.png");
        } else {
            resolved = bee.hasNectar()
                    ? fromBase(base, "_nectar.png")
                    : base;
        }

        cir.setReturnValue(resolved);
    }

    @Unique
    private static ResourceLocation fromBase(ResourceLocation base, String suffix) {
        return ResourceLocation.fromNamespaceAndPath(base.getNamespace(), base.getPath().replace(".png", suffix));
    }
}