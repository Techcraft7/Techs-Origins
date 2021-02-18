package io.github.techcraft7.techs_origins.core;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.client.MutationDataClient;
import io.github.techcraft7.techs_origins.util.Tuple;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.*;

import java.util.*;

public class PlayerMutationData extends Tuple<MutationType, MutationState> {

	public static final String PARTIAL_MUTATION_PREFIX = "mutation";
	private final boolean slim;

	public PlayerMutationData(@NotNull MutationType mutationType, @NotNull MutationState mutationState, boolean slim) {
		super(mutationType, mutationState);
		this.slim = slim;
	}

	@Environment(EnvType.CLIENT)
	public static Identifier createPartialID(UUID uuid) {
		PlayerMutationData data = MutationDataClient.getPlayerData(uuid);
		switch (data.getB()) {
			case NONE:
				return null;
			case PARTIAL_1:
			case PARTIAL_2:
			case PARTIAL_3:
			case PARTIAL_4:
				return TechsOrigins.identifier(String.format("%s/%s/%s.png",
					PARTIAL_MUTATION_PREFIX,
					uuid,
					data.getB().name().toLowerCase()
				));
			case FULL:
				return data.getA().getSkin(data.slim);
		}
		return null; // Should not happen!
	}

	@Contract("null -> null")
	@Nullable
	public static UUID getUUIDFromPartialMutation(Identifier id) {
		if (id == null) {
			return null;
		}
		if (id.getNamespace().contentEquals(TechsOrigins.MOD_ID)) {
			String path = id.getPath();
			// check for mutation/ at start of path
			if (!path.startsWith(PlayerMutationData.PARTIAL_MUTATION_PREFIX + "/")) {
				return null;
			}
			// extract uuid
			String[] split = path.split("/");
			// if it is in the correct format, split will always be three long
			// new String[] { "mutation", "<uuid>", "partial_<num>[.png]" }
			if (split.length != 3) {
				return null;
			}
			try {
				return UUID.fromString(split[1]);
			} catch (IllegalArgumentException e) {
				TechsOrigins.LOGGER.error("Failed to bind custom mutation texture! UUID: " + split[1]);
				return null;
			}
		}
		return null;
	}

	public static Identifier createPartial(UUID uuid, MutationState state) {
		return TechsOrigins.identifier(PARTIAL_MUTATION_PREFIX + "/" + uuid + "/" + state.name()
			.toLowerCase() + ".png");
	}

	/**
	 * @param player Player to get texture of
	 * @return The specified texture for the mutation state
	 *
	 * <h1>Partial Textures</h1>
	 * <p>
	 * Partial textures are special, the Identifier used uses the following format:
	 * </p>
	 * <br>
	 * <code>
	 * techs_origins:mutation/&lt;UUID&gt;/partial_&lt;partial number&gt;.png
	 * </code>
	 */
	@Nullable
	public Identifier getTexture(AbstractClientPlayerEntity player) {
		return createPartialID(player.getUuid());
	}

	public boolean isSlim() {
		return slim;
	}

	@Override
	public boolean equals(Object o) {
		if (super.equals(o)) {
			return ((PlayerMutationData)o).isSlim() && this.isSlim();
		}
		return false;
	}
}
