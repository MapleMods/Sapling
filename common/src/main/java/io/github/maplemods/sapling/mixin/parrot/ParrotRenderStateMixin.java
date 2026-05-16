package io.github.maplemods.sapling.mixin.parrot;

import io.github.maplemods.sapling.render.EntityRenderStateExt;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ParrotRenderState.class)
public class ParrotRenderStateMixin implements EntityRenderStateExt {

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