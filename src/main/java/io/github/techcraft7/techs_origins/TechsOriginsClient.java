package io.github.techcraft7.techs_origins;

import io.github.techcraft7.techs_origins.network.NetworkInit;
import net.fabricmc.api.*;

@Environment(EnvType.CLIENT)
public class TechsOriginsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		TechsOrigins.LOGGER.info("Initializing client side!");
		NetworkInit.initPacketsClient();
		TechsOrigins.LOGGER.info("Done!");
	}
}
