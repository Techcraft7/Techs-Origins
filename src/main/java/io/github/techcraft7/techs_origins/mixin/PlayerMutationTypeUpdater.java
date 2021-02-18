package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.core.MutationDataServer;
import io.github.techcraft7.techs_origins.init.TOPowers;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public class PlayerMutationTypeUpdater {

	@Inject(at = @At("TAIL"), method = "tick")
	public void tick(CallbackInfo ci) {
		PlayerEntity player = (PlayerEntity)(Object)this;
		if (TOPowers.isMutated(player)) {
			return;
		}
		// Only update mutation state server side, and send this to the player
		if (player.getEntityWorld().isClient) {
			return;
		}
		// Don't check every tick to prevent lag
		if (player.age % 20 != 0) {
			return;
		}
		MutationDataServer.updateData(player);
	}
}
