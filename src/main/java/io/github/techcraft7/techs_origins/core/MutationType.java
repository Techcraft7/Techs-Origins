package io.github.techcraft7.techs_origins.core;

import io.github.techcraft7.techs_origins.TechsOrigins;
import net.minecraft.util.Identifier;

@SuppressWarnings("unused")
public enum MutationType {
	DRAGONBORNE,
	ZOMBIE,
	REDSTONIAN;

	public Identifier getSkin(boolean slim) {
		return TechsOrigins.identifier(String.format("textures/skins/%s/complete%s.png",
			name().toLowerCase(),
			slim ? "_slim" : ""
		));
	}
}
