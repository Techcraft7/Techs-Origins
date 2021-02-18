package io.github.techcraft7.techs_origins.util;

import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Maps;
import io.github.techcraft7.techs_origins.core.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class NetworkUtil {

	public static ImmutableBiMap<UUID, PlayerMutationData> readPlayerDataFromPacketBuffer(PacketByteBuf buf) {
		Objects.requireNonNull(buf, "readPlayerDataFromPacketBuffer: buf cannot be null!");
		Map<UUID, PlayerMutationData> data = Maps.newHashMap();
		int size = buf.readInt();
		for (int i = 0; i < size; i++) {
			UUID uuid = buf.readUuid();
			MutationType type = MutationType.fromInt(buf.readInt());
			MutationState state = MutationState.fromInt(buf.readInt());
			boolean isSlim = buf.readBoolean();
			Objects.requireNonNull(type, "readPlayerDataFromPacketBuffer: failed to read the MutationType for " + uuid);
			Objects.requireNonNull(
				state,
				"readPlayerDataFromPacketBuffer: failed to read the MutationState for " + uuid
			);
			data.put(uuid, new PlayerMutationData(type, state, isSlim));
		}
		return ImmutableBiMap.copyOf(data);
	}

	public static void writePlayerDataToPacketBuffer(Map<UUID, PlayerMutationData> data, PacketByteBuf buf) {
		Objects.requireNonNull(data, "writePlayerDataToPacketBuffer: data cannot be null!");
		Objects.requireNonNull(buf, "writePlayerDataToPacketBuffer: buf cannot be null!");
		buf.writeInt(data.size());
		data.forEach((uuid, playerData) -> {
			buf.writeUuid(uuid);
			buf.writeInt(playerData.getA().toInt());
			buf.writeInt(playerData.getB().toInt());
			buf.writeBoolean(playerData.isSlim());
		});
	}

	public static void requestPlayerModel(ServerPlayerEntity player) {
	}

}
