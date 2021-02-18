package io.github.techcraft7.techs_origins;

import io.github.techcraft7.techs_origins.network.NetworkInit;
import net.fabricmc.api.DedicatedServerModInitializer;

public class TechsOriginsServer implements DedicatedServerModInitializer {
	@Override
	public void onInitializeServer() {
		TechsOrigins.LOGGER.info("Initializing server side!");
		NetworkInit.initPacketsServer();
		TechsOrigins.LOGGER.info("Done!");
	}
}
