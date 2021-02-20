package io.github.techcraft7.techs_origins.core;

import com.google.common.collect.Maps;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.github.techcraft7.techs_origins.init.TOPowers;
import io.github.techcraft7.techs_origins.network.NetworkInit;
import io.github.techcraft7.techs_origins.util.NetworkUtil;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.Proxy;
import java.util.*;
import java.util.concurrent.atomic.*;

public class MutationDataServer {

	// Atomic Reference to be thread safe :)
	public static final AtomicBoolean NEEDS_SYNC = new AtomicBoolean(false);
	private static final Map<UUID, MutationState> OVERRIDES = Maps.newHashMap(); // For debugging only
	private static final Map<UUID, PlayerMutationData> PLAYER_DATA = Maps.newHashMap();
	private static final Map<UUID, Boolean> SLIM_DATA = Maps.newHashMap();
	private static final MinecraftSessionService SESSION_SERVICE;

	static {
		// Create session service
		SESSION_SERVICE = new YggdrasilAuthenticationService(Proxy.NO_PROXY).createMinecraftSessionService();
	}

	// This is inefficient, the server will only reload all the data on boot
	public static void resetData() {
		SLIM_DATA.clear();
		PLAYER_DATA.clear();
	}

	@Nullable
	public static PlayerMutationData getPlayerData(UUID uuid) {
		return PLAYER_DATA.getOrDefault(uuid, null);
	}

	public static void updateData(@NotNull ServerPlayerEntity player) {
		MutationType type = TOPowers.getMutationType(player);
		if (type == null) { // Not mutated
			return;
		}
		if (!SLIM_DATA.containsKey(player.getUuid())) {
			MinecraftProfileTexture tex = SESSION_SERVICE.getTextures(player.getGameProfile(), true)
				.getOrDefault(MinecraftProfileTexture.Type.SKIN, null);
			SLIM_DATA.put(player.getUuid(),
				tex == null ? (player.getUuid().hashCode() & 1) == 1 : Objects.equals(tex.getMetadata("model"), "slim")
			);
		}
		boolean isSlim = SLIM_DATA.get(player.getUuid());
		int playTime = player.getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_ONE_MINUTE));
		MutationState state = MutationState.getStateFromAgeTicks(playTime);

		PlayerMutationData data = getPlayerData(player.getUuid());
		PlayerMutationData newData = new PlayerMutationData(type, state, isSlim);
		if (data == null) {
			PLAYER_DATA.put(player.getUuid(), newData);
			NEEDS_SYNC.set(true);
		} else if (!data.equals(newData)) {
			PLAYER_DATA.put(player.getUuid(), newData);
			NEEDS_SYNC.set(true);
		}
	}

	public static void sendData(ServerPlayerEntity player) {
		PacketByteBuf buf = PacketByteBufs.create();
		Map<UUID, PlayerMutationData> map = Maps.newHashMap(PLAYER_DATA);
		OVERRIDES.forEach((uuid, state) -> {
			if (map.containsKey(uuid)) {
				PlayerMutationData data = getPlayerData(uuid);
				Objects.requireNonNull(data);
				map.put(uuid, new PlayerMutationData(data.getA(), state, data.isSlim()));
			}
		});
		NetworkUtil.writePlayerDataToPacketBuffer(map, buf);
		ServerPlayNetworking.send(player, NetworkInit.PLAYER_DATA_PID, buf);
	}

	// This is a debug method
	public static void setState(ServerPlayerEntity player, MutationState state) {
		Objects.requireNonNull(player);
		Objects.requireNonNull(state);
		OVERRIDES.put(player.getUuid(), state);
		sendData(player);
	}
}
