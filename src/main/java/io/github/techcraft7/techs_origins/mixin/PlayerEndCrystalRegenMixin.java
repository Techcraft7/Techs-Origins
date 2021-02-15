package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.init.TOPowers;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(PlayerEntity.class)
public class PlayerEndCrystalRegenMixin {

	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		if (!TOPowers.CRYSTAL_REGEN.isActive(player)) {
			return;
		}
		if (player.world.isClient) {
			return;
		}
		List<EndCrystalEntity> entityList = player.getEntityWorld().getEntitiesByType(EntityType.END_CRYSTAL,
			player.getBoundingBox().expand(32),
			e -> true
		);
		entityList.sort(Comparator.comparingDouble(e -> e.getPos().distanceTo(player.getPos())));
		entityList.stream().findFirst().ifPresent(crystal -> {
			int numCrystals = entityList.size(); // Number of nearby end crystals
			int amplifier = Math.min(numCrystals, 3) - 1; // Effect amplifier
			boolean hasRegen = player.getStatusEffects()
				.stream()
				.anyMatch(e -> StatusEffects.REGENERATION.equals(e.getEffectType()));
			if (!hasRegen) {
				player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION,
					60,
					amplifier,
					false,
					false,
					false
				));
			}
		});
	}

}
