package io.github.techcraft7.techs_origins.power;

import io.github.apace100.origins.power.Power;
import io.github.apace100.origins.power.PowerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class MutationPower extends Power {

	private final Identifier skin;

	public MutationPower(PowerType<?> type, PlayerEntity player, Identifier skin) {
		super(type, player);
		this.skin = skin;
	}

	public Identifier getSkin() {
		return skin;
	}
}
