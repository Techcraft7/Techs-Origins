package io.github.techcraft7.techs_origins.core;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import io.github.techcraft7.techs_origins.TechsOrigins;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public enum MutationType {
	DRAGONBORNE, ZOMBIE, REDSTONIAN, // Custom
	ARACHNID, AVIAN, BLAZEBORN, ELYTRIAN, ENDERIAN, FELINE, MERLING, PHANTOM, SHULK; // Origins defaults

	private static final BiMap<Integer, MutationType> INT_MAP = ImmutableBiMap.copyOf(Arrays.stream(values())
		.collect(Collectors.toMap(Enum::ordinal, Function.identity())));

	public static int toInt(MutationType type) {
		if (type == null) {
			return 0;
		}
		return type.toInt();
	}

	@Nullable
	public static MutationType fromInt(int i) {
		return INT_MAP.getOrDefault(i, null);
	}

	public int toInt() {
		return INT_MAP.inverse().getOrDefault(this, 0);
	}

	public Identifier getSkin(boolean slim) {
		return TechsOrigins.identifier(String.format("textures/skins/%s/complete%s.png",
			name().toLowerCase(),
			slim ? "_slim" : ""
		));
	}
}
