package io.github.maplemods.sapling.mixin;

import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.RandomFunctions;
import io.github.maplemods.sapling.functions.TextureFunctions;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(BeeRenderer.class)
public class BeeRendererMixin {
    
    @Inject(method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Bee;)Lnet/minecraft/resources/ResourceLocation;", at = @At("HEAD"), cancellable = true)
    public void getTextureLocation(Bee bee, CallbackInfoReturnable<ResourceLocation> cir) {
        if (Textures.beeTexturePairs.isEmpty()) {
            return;
        }

        UUID beeUUID = bee.getUUID();

        ResourceLocation baseResourceLocation = TextureFunctions.getCachedEntityTexture(beeUUID);
        if (baseResourceLocation == null) {
            baseResourceLocation = RandomFunctions.getRandomBeeTexture(Textures.beeTexturePairs);
            if (baseResourceLocation == null) {
                return;
            }
        }

        ResourceLocation beeResourceLocation;
        if (bee.isAngry()) {
            beeResourceLocation = bee.hasNectar()
                    ? angryNectarResourceLocation(baseResourceLocation)
                    : angryResourceLocation(baseResourceLocation);
        }
        else {
            beeResourceLocation = bee.hasNectar()
                    ? nectarResourceLocation(baseResourceLocation)
                    : baseResourceLocation;
        }

        cir.setReturnValue(beeResourceLocation);
    }

    @Unique
    private ResourceLocation nectarResourceLocation(ResourceLocation baseResourceLocation) {
        return ResourceLocation.fromNamespaceAndPath(baseResourceLocation.getNamespace(), baseResourceLocation.getPath().replace(".png", "_nectar.png"));
    }

    @Unique
    private ResourceLocation angryResourceLocation(ResourceLocation baseResourceLocation) {
        return ResourceLocation.fromNamespaceAndPath(baseResourceLocation.getNamespace(), baseResourceLocation.getPath().replace(".png", "_angry.png"));
    }

    @Unique
    private ResourceLocation angryNectarResourceLocation(ResourceLocation baseResourceLocation) {
        return ResourceLocation.fromNamespaceAndPath(baseResourceLocation.getNamespace(), baseResourceLocation.getPath().replace(".png", "_angry_nectar.png"));
    }
}