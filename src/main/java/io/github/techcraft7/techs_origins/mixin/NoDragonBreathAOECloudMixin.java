package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.init.TOPowers;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StatusEffect.class)
public class NoDragonBreathAOECloudMixin {

	@Inject(at = @At("HEAD"), method = "applyInstantEffect", cancellable = true)
	public void applyInstanceEffect(@Nullable Entity source, @Nullable Entity attacker, LivingEntity target, int amplifier, double proximity, CallbackInfo ci) {
		if (!TOPowers.NO_DRAGON_FIREBALL_AOE_CLOUD.isActive(target)) {
			return;
		}
		if (source instanceof AreaEffectCloudEntity) {
			ParticleEffect particle = ((AreaEffectCloudEntity)source).getParticleType();
			if (particle.getType().equals(ParticleTypes.DRAGON_BREATH)) {
				ci.cancel();
			}
		}
	}

}
