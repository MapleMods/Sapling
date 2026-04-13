package io.github.maplemods.sapling.mixin.bee;

import io.github.maplemods.sapling.functions.EntityRenderFunctions;
import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.BeeRenderer;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.bee.Bee;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BeeRenderer.class)
public class BeeRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/animal/bee/Bee;Lnet/minecraft/client/renderer/entity/state/BeeRenderState;F)V", at = @At("TAIL"))
    private void sapling$attachBee(Bee bee, BeeRenderState state, float partialTicks, CallbackInfo ci) {
        ((EntityRenderStateExt) state).sapling$setEntity(bee);
    }

    @Inject(method = "getTextureLocation(Lnet/minecraft/client/renderer/entity/state/BeeRenderState;)Lnet/minecraft/resources/Identifier;", at = @At("HEAD"), cancellable = true)
    public void getTextureLocation(BeeRenderState beeRenderState, CallbackInfoReturnable<Identifier> cir) {
        if (beeRenderState.isBaby) {
            return;
        }

        Identifier base = EntityRenderFunctions.resolveTexture((EntityRenderStateExt) beeRenderState, EntityType.BEE);
        if (base == null) return;

        Identifier resolved;
        if (beeRenderState.isAngry) {
            resolved = beeRenderState.hasNectar
                    ? fromBase(base, "_angry_nectar.png")
                    : fromBase(base, "_angry.png");
        } else {
            resolved = beeRenderState.hasNectar
                    ? fromBase(base, "_nectar.png")
                    : base;
        }

        cir.setReturnValue(resolved);
    }

    @Unique
    private static Identifier fromBase(Identifier base, String suffix) {
        return Identifier.fromNamespaceAndPath(base.getNamespace(), base.getPath().replace(".png", suffix));
    }
}