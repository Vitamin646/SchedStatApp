package io.github.vitamin646dev.schedstatapp.model;

/**
 * One set: repetitions at a weight, with an optional RPE (rate of perceived exertion, 1-10).
 */
public record ExerciseSet(Long id, int reps, double weightKg, Double rpe) {

	public ExerciseSet {
		if (reps <= 0) {
			throw new IllegalArgumentException("Reps must be positive: " + reps);
		}
		if (weightKg < 0 || Double.isNaN(weightKg)) {
			throw new IllegalArgumentException("Weight must not be negative: " + weightKg);
		}
		if (rpe != null && (rpe < 1 || rpe > 10)) {
			throw new IllegalArgumentException("RPE must be between 1 and 10: " + rpe);
		}
	}

	public static ExerciseSet of(int reps, double weightKg) {
		return new ExerciseSet(null, reps, weightKg, null);
	}
}
