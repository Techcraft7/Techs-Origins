package io.github.techcraft7.techs_origins.network;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.client.MutationDataClient;
import io.github.techcraft7.techs_origins.util.NetworkUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.util.Identifier;

public class NetworkInit {
	public static final Identifier PLAYER_DATA_PID = TechsOrigins.identifier("player_data");

	@Environment(EnvType.CLIENT)
	public static void initPacketsClient() {
		TechsOrigins.LOGGER.info("Loading client packets!");
		ClientPlayNetworking.registerGlobalReceiver(
			PLAYER_DATA_PID,
			(client, handler, buf, responseSender) -> MutationDataClient.updateMultipleStates(NetworkUtil.readPlayerDataFromPacketBuffer(
				buf))
		);
		TechsOrigins.LOGGER.info("Done!");
	}

	@Environment(EnvType.SERVER)
	public static void initPacketsServer() {
		TechsOrigins.LOGGER.info("Loading server packets!");
		TechsOrigins.LOGGER.info("Done!");
	}
}
