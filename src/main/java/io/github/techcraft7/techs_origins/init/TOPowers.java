package io.github.techcraft7.techs_origins.init;

import com.google.common.collect.Maps;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerTypeReference;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.apace100.origins.util.*;
import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.core.MutationType;
import io.github.techcraft7.techs_origins.power.RedstonianStunPower;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;


public class TOPowers {

	public static final PowerTypeReference<?> CRYSTAL_REGEN = new PowerTypeReference<>(TechsOrigins.identifier(
		"crystal_regen"));
	public static final PowerTypeReference<?> NO_DRAGON_FIREBALL_AOE_CLOUD = new PowerTypeReference<>(TechsOrigins.identifier(
		"no_dragon_fireball_aoe_cloud"));
	public static final PowerTypeReference<?> REDSTONE_AURA = new PowerTypeReference<>(TechsOrigins.identifier(
		"redstone_aura"));
	public static final PowerTypeReference<?> NO_LEAVE_NETHER = new PowerTypeReference<>(TechsOrigins.identifier(
		"no_leave_nether"));
	private static final Map<MutationType, PowerTypeReference<?>> MUTATION_MAP = Maps.newHashMap();
	private static final Map<PowerFactory<?>, Identifier> POWER_FACTORIES = new LinkedHashMap<>();

	static {
		TechsOrigins.LOGGER.info("Loading MutationType -> PowerTypeReference map!");
		for (MutationType value : MutationType.values()) {
			TechsOrigins.LOGGER.debug("Loading: " + value);
			MUTATION_MAP.put(value,
				new PowerTypeReference<>(TechsOrigins.identifier("mutation_" + value.name().toLowerCase()))
			);
		}
		TechsOrigins.LOGGER.info("Done!");
	}


	private static <T extends Power> void create(PowerFactory<T> factory) {
		POWER_FACTORIES.put(factory, factory.getSerializerId());
	}

	public static void init() {
		create(new PowerFactory<>(TechsOrigins.identifier("redstonian_stun"),
			new SerializableData().add("cooldown", SerializableDataType.INT)
				.add("hud_render", SerializableDataType.HUD_RENDER)
				.add("stun_type", SerializableDataType.STRING),
			data -> (type, player) -> new RedstonianStunPower(type,
				player,
				data.getInt("cooldown"),
				(HudRender)data.get("hud_render"),
				data.getString("stun_type")
			)
		));
		TechsOrigins.LOGGER.info("Registering power factories!");
		POWER_FACTORIES.forEach((factory, id) -> {
			TechsOrigins.LOGGER.debug("Registering power factory: " + id);
			Registry.register(ModRegistries.POWER_FACTORY, id, factory);
		});
		TechsOrigins.LOGGER.info("Done!");
	}

	public static boolean isMutated(PlayerEntity player) {
		return Arrays.stream(MUTATION_MAP.values().toArray())
			.map(o -> (PowerTypeReference<?>)o)
			.anyMatch(ptr -> ptr.isActive(player));
	}

	/**
	 * @param player Player to check
	 * @return player's mutation type, if they are not mutated this will return null
	 */
	@Nullable
	public static MutationType getMutationType(PlayerEntity player) {
		if (isMutated(player)) {
			return null;
		}
		return MUTATION_MAP.entrySet()
			.stream()
			.filter(kv -> kv.getValue().isActive(player))
			.map(Map.Entry::getKey)
			.findFirst()
			.orElse(null);
	}
}
