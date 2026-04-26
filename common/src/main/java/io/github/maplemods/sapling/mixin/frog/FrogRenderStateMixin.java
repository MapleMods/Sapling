package io.github.maplemods.sapling.mixin.frog;

import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.state.FrogRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FrogRenderState.class)
public class FrogRenderStateMixin implements EntityRenderStateExt {

	@Unique
	private LivingEntity sapling$entity;

	@Override
	public void sapling$setEntity(LivingEntity entity) {
		this.sapling$entity = entity;
	}

	@Override
	public LivingEntity sapling$getEntity() {
		return this.sapling$entity;
	}
}