package io.github.maplemods.sapling.mixin;

import io.github.maplemods.sapling.data.Constants;
import io.github.maplemods.sapling.data.Textures;
import io.github.maplemods.sapling.functions.RandomFunctions;
import io.github.maplemods.sapling.functions.TagFunctions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Bee.class)
public class BeeMixin {
	@Inject(method = "<init>", at = @At(value = "TAIL"))
	public void Bee(EntityType<?> entityType, Level level, CallbackInfo ci) {
        if (Textures.beeTexturePairs.isEmpty()) {
            return;
        }

		Bee bee = (Bee)(Object)this;

		if (TagFunctions.hasCustomTexture(bee)) {
			return;
		}

        ResourceLocation baseResourceLocation = TagFunctions.getCustomEntityTextureIfExists(bee);
        if (baseResourceLocation == null) {
            baseResourceLocation = RandomFunctions.getRandomBeeTexture(Textures.beeTexturePairs);
            if (baseResourceLocation == null) {
                return;
            }
        }

		bee.getTags().add(Constants.MOD_ID + "++" + baseResourceLocation.toString().replace(":", "--"));
	}
}
