package io.github.techcraft7.techs_origins.core;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.github.techcraft7.techs_origins.init.TOPowers;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.*;

public class MutationDataServer {

	private static final Map<UUID, PlayerMutationData> PLAYER_DATA = Maps.newHashMap();
	private static final Map<UUID, Boolean> SLIM_DATA = Maps.newHashMap();
	private static final MinecraftSessionService SESSION_SERVICE;

	static {
		SESSION_SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY).createMinecraftSessionService();
	}

	public static void resetData() {
		SLIM_DATA.clear();
		PLAYER_DATA.clear();
	}

	@Nullable
	public static PlayerMutationData getData(UUID uuid) {
		return PLAYER_DATA.getOrDefault(uuid, null);
	}

	public static void updateData(@NotNull PlayerEntity player) {
		MutationType type = TOPowers.getMutationType(player);
		if (type == null) { // Not mutated
			return;
		}
		if (!SLIM_DATA.containsKey(player.getUuid())) {
			MinecraftProfileTexture tex = SESSION_SERVICE.getTextures(player.getGameProfile(), true).getOrDefault(MinecraftProfileTexture.Type.SKIN,
				null
			);
			SLIM_DATA.put(player.getUuid(),
				tex == null ? (player.getUuid().hashCode() & 1) == 1 : Objects.equals(tex.getMetadata("model"), "slim")
			);
		}
		boolean isSlim = SLIM_DATA.get(player.getUuid());
		MutationState state = MutationState.getStateFromAgeTicks(player.age);

		PlayerMutationData data = getData(player.getUuid());
		PlayerMutationData newData = new PlayerMutationData(type, state, isSlim);
		if (data == null) {
			PLAYER_DATA.put(player.getUuid(), newData);
		} else if (!data.equals(newData)) {
			PLAYER_DATA.put(player.getUuid(), newData);
		}
	}
}
