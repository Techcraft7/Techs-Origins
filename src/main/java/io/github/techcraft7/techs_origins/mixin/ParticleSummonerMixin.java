package io.github.techcraft7.techs_origins.mixin;

import com.mojang.authlib.GameProfile;
import io.github.techcraft7.techs_origins.init.TOPowers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(OtherClientPlayerEntity.class)
public abstract class ParticleSummonerMixin extends AbstractClientPlayerEntity {

	public ParticleSummonerMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo ci) {
		if (!TOPowers.REDSTONE_AURA.isActive(this)) {
			return;
		}
		if (this.age % 20 == 0) {
			this.world.addParticle(DustParticleEffect.RED, this.getParticleX(2), this.getRandomBodyY(),
				this.getParticleZ(2), 0, 0, 0);
		}
	}

}
