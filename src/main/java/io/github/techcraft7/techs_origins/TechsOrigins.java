package io.github.techcraft7.techs_origins;

import io.github.techcraft7.techs_origins.client.MutationDataClient;
import io.github.techcraft7.techs_origins.core.MutationDataServer;
import io.github.techcraft7.techs_origins.core.PlayerMutationData;
import io.github.techcraft7.techs_origins.init.TOPowers;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TechsOrigins implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "techs_origins";

	public static Identifier identifier(String path) {
		return new Identifier(MOD_ID, path);
	}

	/**
	 * @param player Player to get data of (Works regardless of the side player is on)
	 * @return PlayerMutationData of <code>player</code>
	 */
	public static PlayerMutationData getMutationData(@NotNull PlayerEntity player) {
		Objects.requireNonNull(player);
		if (player.getEntityWorld().isClient()) {
			return MutationDataClient.getPlayerData(player.getUuid());
		} else {
			return MutationDataServer.getPlayerData(player.getUuid());
		}
	}

	@Override
	public void onInitialize() {
		LOGGER.info("Loading Tech'sOrigins!");
		TOPowers.init();
		LOGGER.info("Done!");
	}
}