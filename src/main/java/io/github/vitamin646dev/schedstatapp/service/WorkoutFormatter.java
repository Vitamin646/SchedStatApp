package io.github.vitamin646dev.schedstatapp.service;

import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Turns workouts into short texts for lists, calendar entries and detail panels.
 */
public final class WorkoutFormatter {

	private WorkoutFormatter() {
	}

	/**
	 * One-line summary: the distinct exercise names in order, or a placeholder for an empty workout.
	 */
	public static String title(Workout workout) {
		String names = workout.exercises().stream()
				.map(e -> e.exercise().name())
				.distinct()
				.collect(Collectors.joining(", "));
		return names.isEmpty() ? "Workout (no exercises)" : names;
	}

	/**
	 * Multi-line description: date, status, tags, notes and every exercise with its numbered sets.
	 */
	public static String details(Workout workout) {
		List<String> lines = new ArrayList<>();
		lines.add(workout.date() + " · " + workout.status());
		if (!workout.tags().isEmpty()) {
			lines.add("Tags: " + String.join(", ", workout.tags()));
		}
		if (workout.notes() != null) {
			lines.add("Notes: " + workout.notes());
		}

		for (WorkoutExercise workoutExercise : workout.exercises()) {
			lines.add("");
			String heading = workoutExercise.exercise().name();
			if (workoutExercise.notes() != null) {
				heading += " (" + workoutExercise.notes() + ")";
			}
			lines.add(heading);

			int number = 1;
			for (ExerciseSet set : workoutExercise.sets()) {
				lines.add("  " + number++ + ". " + formatSet(set));
			}
		}
		return String.join("\n", lines);
	}

	/**
	 * For example {@code 5 x 100 kg, RPE 8} or {@code 10 x 72.5 kg}.
	 */
	public static String formatSet(ExerciseSet set) {
		String text = set.reps() + " × " + formatNumber(set.weightKg()) + " kg";
		return set.rpe() == null ? text : text + ", RPE " + formatNumber(set.rpe());
	}

	private static String formatNumber(double value) {
		return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
	}
}
