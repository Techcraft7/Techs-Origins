package io.github.techcraft7.techs_origins.core;

public enum MutationState {
	NONE(0),
	PARTIAL_1(60 * 60),
	PARTIAL_2(60 * 60 * 2),
	PARTIAL_3(60 * 60 * 3),
	PARTIAL_4(60 * 60 * 4),
	FULL(60 * 60 * 5);

	private final int timeSeconds;

	MutationState(int timeSeconds) {
		this.timeSeconds = timeSeconds;
	}

	public static MutationState getStateFromAgeTicks(int age) {
		for (int i = 0; i < values().length; i++) {
			if (age > values()[i].getTimeInTicks()) {
				i--;
				if (i < 0) {
					return NONE;
				}
				return values()[i - 1];
			}
		}
		return NONE;
	}

	public int getTimeInTicks() {
		return timeSeconds * 20;
	}

	/**
	 * @return The next mutation state after the current state
	 */
	public MutationState next() {
		return values()[Math.min(this.ordinal() + 1, values().length - 1)];
	}
}
