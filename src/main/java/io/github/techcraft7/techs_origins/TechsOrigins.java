package io.github.techcraft7.techs_origins;

import io.github.techcraft7.techs_origins.init.TOPowers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TechsOrigins implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "techs_origins";

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Tech'sOrigins!");
		TOPowers.init();
		LOGGER.info("Done!");
	}
}