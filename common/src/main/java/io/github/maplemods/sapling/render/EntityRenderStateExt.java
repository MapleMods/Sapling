package io.github.maplemods.sapling.render;

import net.minecraft.world.entity.LivingEntity;

public interface EntityRenderStateExt {
	void sapling$setEntity(LivingEntity entity);
	LivingEntity sapling$getEntity();
}