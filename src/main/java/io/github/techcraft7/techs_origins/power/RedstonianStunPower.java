package io.github.techcraft7.techs_origins.power;

import io.github.apace100.origins.power.ActiveCooldownPower;
import io.github.apace100.origins.power.PowerType;
import io.github.apace100.origins.util.HudRender;
import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.core.MutationState;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class RedstonianStunPower extends ActiveCooldownPower {
	private final StunType stunType;

	public RedstonianStunPower(PowerType<?> type, PlayerEntity player, int cooldownDuration, HudRender hudRender, String stunType) {
		super(type, player, cooldownDuration, hudRender, (entity) -> {
		});
		this.type = type;
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
			if (le.equals(player)) {
				continue;
			}
			if (getType().isActive(le)) {
				continue;
			}
			if (le.isSpectator()) {
				continue;
			}
			switch (stunType.accountForMutationState(player)) {
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
		SLOWNESS(null), // Can't self reference, this is a cheeky hack to avoid this
		LIGHTNING(SLOWNESS);

		private final StunType fallback;

		StunType(@Nullable StunType fallback) {
			this.fallback = fallback;
		}

		/**
		 * @param player The player that is using the stun power
		 * @return <code>this</code> if <code>player</code> is fully mutated, otherwise {@link #getFallback()}
		 */
		public StunType accountForMutationState(PlayerEntity player) {
			PlayerMutationData data = TechsOrigins.getMutationData(player);
			// This will never cause a NPE because if data == null is true the other check is skipped
			if (data == null || data.getB() != MutationState.FULL) {
				return getFallback() == null ? this : getFallback();
			}
			return this;
		}

		/**
		 * @return The fallback stun type if the player tries to use the stun power of this type, but is not fully
		 * mutated.
		 * <br>
		 * If this returns null, then the stun type can be used even if the player is not fully mutated
		 */
		@Nullable
		public StunType getFallback() {
			return fallback;
		}
	}
}
