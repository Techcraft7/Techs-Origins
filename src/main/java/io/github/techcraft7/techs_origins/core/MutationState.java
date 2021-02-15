package io.github.techcraft7.techs_origins.core;

public enum MutationState {
	NONE,
	PARTIAL_1,
	PARTIAL_2,
	PARTIAL_3,
	PARTIAL_4,
	FULL;

	/**
	 * @return The next mutation state after the current state
	 */
	public MutationState next() {
		return values()[Math.min(this.ordinal() + 1, values().length - 1)];
	}
}
