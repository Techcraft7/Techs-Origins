package io.github.techcraft7.techs_origins.init;

import com.google.common.collect.Maps;
import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerTypeReference;
import io.github.apace100.origins.power.factory.PowerFactory;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.core.MutationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.*;


@SuppressWarnings("unused")
public class TOPowers {

	public static final PowerTypeReference<?> CRYSTAL_REGEN = new PowerTypeReference<>(TechsOrigins.identifier(
		"crystal_regen"));
	public static final PowerTypeReference<?> NO_DRAGON_FIREBALL_AOE_CLOUD = new PowerTypeReference<>(TechsOrigins.identifier(
		"no_dragon_fireball_aoe_cloud"));
	public static final PowerTypeReference<?> MUTATION_DRAGON = new PowerTypeReference<>(TechsOrigins.identifier(
		"mutation_dragon"));
	public static final PowerTypeReference<?> MUTATION_ZOMBIE = new PowerTypeReference<>(TechsOrigins.identifier(
		"mutation_zombie"));
	public static final PowerTypeReference<?> MUTATION_REDSTONIAN = new PowerTypeReference<>(TechsOrigins.identifier(
		"mutation_redstonian"));
	private static final Map<MutationType, PowerTypeReference<?>> MUTATION_MAP = Maps.newHashMap();
	private static final Map<PowerFactory<?>, Identifier> POWER_FACTORIES = new LinkedHashMap<>();

	static {
		MUTATION_MAP.put(MutationType.DRAGONBORNE, MUTATION_DRAGON);
		MUTATION_MAP.put(MutationType.ZOMBIE, MUTATION_ZOMBIE);
		MUTATION_MAP.put(MutationType.REDSTONIAN, MUTATION_REDSTONIAN);
	}


	private static <T extends Power> PowerFactory<T> create(PowerFactory<T> factory) {
		POWER_FACTORIES.put(factory, factory.getSerializerId());
		return factory;
	}

	public static void init() {
		POWER_FACTORIES.keySet().forEach(powerType -> {
			TechsOrigins.LOGGER.info("Registering power type: " + powerType);
			Registry.register(ModRegistries.POWER_FACTORY, POWER_FACTORIES.get(powerType), powerType);
		});
	}

	public static boolean isMutated(PlayerEntity player) {
		return Arrays.stream(MUTATION_MAP.values().toArray())
			.map(o -> (PowerTypeReference<?>)o)
			.noneMatch(ptr -> ptr.isActive(player));
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
