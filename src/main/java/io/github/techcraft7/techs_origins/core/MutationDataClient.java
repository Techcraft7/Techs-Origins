package io.github.techcraft7.techs_origins.core;

import io.github.techcraft7.techs_origins.client.MutationTexture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.entity.player.PlayerEntity;

import java.util.*;

@Environment(EnvType.CLIENT)
public class MutationDataClient {

	private static final Map<UUID, PlayerMutationData> PLAYER_DATA = new HashMap<>();
	private static final Map<UUID, AbstractTexture> TEXTURE_CACHE = new HashMap<>();

	public static void resetPlayerData() {
		PLAYER_DATA.clear();
		TEXTURE_CACHE.clear();
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

	public static boolean isTextureCached(UUID uuid) {
		return false;
	}

	public static AbstractTexture getCachedTexture(UUID uuid) {
		return null;
	}

	public static AbstractTexture createAndCacheTexture(UUID uuid) {
		return new MutationTexture(uuid);
	}
}
