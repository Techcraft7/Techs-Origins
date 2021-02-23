package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.init.TOPowers;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class PreventLeavingNetherMixin {

	@Inject(at = @At("HEAD"), method = "tickNetherPortal", cancellable = true)
	public void moveToWorld(CallbackInfo ci) {
		Entity e = (Entity)(Object)this;
		if (TOPowers.NO_LEAVE_NETHER.isActive(e)) {
			ci.cancel();
		}
	}
}
