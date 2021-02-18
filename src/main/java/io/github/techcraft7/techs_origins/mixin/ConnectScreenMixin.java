package io.github.techcraft7.techs_origins.mixin;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.client.MutationDataClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public class ConnectScreenMixin {

	@Inject(at = @At("HEAD"), method = "connect")
	public void connect(String address, int port, CallbackInfo ci) {
		TechsOrigins.LOGGER.info("Resetting mutation states!");
		MutationDataClient.resetPlayerData();
		TechsOrigins.LOGGER.info("Resetting mutation states!");
	}
}
