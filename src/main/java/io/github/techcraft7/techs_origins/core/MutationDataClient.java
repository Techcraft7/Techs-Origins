package io.github.techcraft7.techs_origins.core;

import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

public class MutationDataClient {

	private static final Map<UUID, PlayerMutationData> PLAYER_DATA = new HashMap<>();

	public static void resetPlayerData() {
		PLAYER_DATA.clear();
	}

	public static PlayerMutationData getState(UUID uuid) {
		return PLAYER_DATA.getOrDefault(uuid, null);
	}

	public static PlayerMutationData getState(PlayerEntity player) {
		if (player == null) {
			return null;
		}
		return getState(player.getUuid());
	}

}
