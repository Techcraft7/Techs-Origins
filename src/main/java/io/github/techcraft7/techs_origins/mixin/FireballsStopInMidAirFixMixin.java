package io.github.techcraft7.techs_origins.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExplosiveProjectileEntity.class)
public class FireballsStopInMidAirFixMixin {

	@Inject(at = @At("HEAD"), method = "getDrag", cancellable = true)
	private void getDrag(CallbackInfoReturnable<Float> cir) {
		ExplosiveProjectileEntity epe = (ExplosiveProjectileEntity)(Object)this;
		if (epe.getOwner() instanceof PlayerEntity) {
			cir.setReturnValue(1f);
			cir.cancel();
		}
	}


}
