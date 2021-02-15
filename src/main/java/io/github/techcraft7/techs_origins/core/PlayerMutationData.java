package io.github.techcraft7.techs_origins.core;

import io.github.techcraft7.techs_origins.TechsOrigins;
import io.github.techcraft7.techs_origins.util.Tuple;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

public class PlayerMutationData extends Tuple<MutationType, MutationState> {

	public PlayerMutationData(@NotNull MutationType mutationType, @NotNull MutationState mutationState) {
		super(mutationType, mutationState);
	}

	public Identifier getTexture(boolean slim) {
		return TechsOrigins.identifier("textures/skins/" + getA().name().toLowerCase() + "/" + getB().name()
			.toLowerCase() + (slim ? "_slim" : "") + ".png");
	}
}
