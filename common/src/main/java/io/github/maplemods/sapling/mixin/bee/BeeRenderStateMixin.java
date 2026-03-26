package io.github.maplemods.sapling.mixin.bee;

import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BeeRenderState.class)
public class BeeRenderStateMixin implements EntityRenderStateExt {

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