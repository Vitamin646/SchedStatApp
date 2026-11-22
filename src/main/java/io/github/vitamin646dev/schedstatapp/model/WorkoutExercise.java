package io.github.vitamin646dev.schedstatapp.model;

import java.util.List;
import java.util.Objects;

/**
 * An exercise performed (or planned) within a workout, with its sets in order.
 */
public record WorkoutExercise(Long id, Exercise exercise, List<ExerciseSet> sets, String notes) {

	public WorkoutExercise {
		Objects.requireNonNull(exercise, "exercise");
		sets = List.copyOf(sets);
	}

	public static WorkoutExercise of(Exercise exercise, List<ExerciseSet> sets) {
		return new WorkoutExercise(null, exercise, sets, null);
	}
}
