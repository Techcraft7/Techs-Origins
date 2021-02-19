package io.github.techcraft7.techs_origins.power;

import io.github.apace100.origins.power.ActiveCooldownPower;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.util.HudRender;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class RedstonianStunPower extends ActiveCooldownPower {
	private final StunType stunType;

	public RedstonianStunPower(PowerType<?> type, PlayerEntity player, int cooldownDuration, HudRender hudRender, String stunType) {
		super(type, player, cooldownDuration, hudRender, null);
		Objects.requireNonNull(stunType, "RedstonianStunPower: stunType was null!");
		switch (stunType.toLowerCase()) {
			case "lightning":
			case "slowness":
				this.stunType = StunType.valueOf(stunType.toUpperCase());
				break;
			default:
				throw new IllegalArgumentException("RedstonianStunPower: Invalid stunType!");
		}
	}

	@Override
	public void onUse() {
		if (!canUse()) {
			return;
		}
		super.onUse();
		List<LivingEntity> entities = this.player.getEntityWorld().getEntitiesByClass(
			LivingEntity.class,
			this.player.getBoundingBox().expand(10),
			e -> e instanceof PlayerEntity || e instanceof MobEntity
		);
		for (LivingEntity le : entities) {
			// Don't affect other redstonians
			if (this.type.isActive(player)) {
				continue;
			}
			if (le.isSpectator()) {
				continue;
			}
			switch (stunType) {
				case SLOWNESS:
					le.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 5, 9));
					break;
				case LIGHTNING:
					LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, le.getEntityWorld());
					lightning.setPos(le.getX(), le.getY(), le.getZ());
					le.getEntityWorld().spawnEntity(lightning);
					break;
			}
		}
	}

	private enum StunType {
		SLOWNESS, LIGHTNING
	}
}
