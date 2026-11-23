package io.github.vitamin646dev.schedstatapp.service;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WorkoutFormatterTest {

	private static final LocalDate DAY = LocalDate.of(2026, 11, 23);

	private final Exercise squat = new Exercise(1L, "Squat", "Legs", null);
	private final Exercise bench = new Exercise(2L, "Bench Press", "Chest", null);

	@Test
	void titleListsDistinctExercisesInOrder() {
		Workout workout = new Workout(1L, DAY, WorkoutStatus.DONE, null, Set.of(), List.of(
				WorkoutExercise.of(squat, List.of()), WorkoutExercise.of(bench, List.of()),
				WorkoutExercise.of(squat, List.of())));

		assertEquals("Squat, Bench Press", WorkoutFormatter.title(workout));
	}

	@Test
	void titleOfEmptyWorkoutIsAPlaceholder() {
		assertEquals("Workout (no exercises)", WorkoutFormatter.title(Workout.of(DAY, WorkoutStatus.PLANNED)));
	}

	@Test
	void formatSetOmitsZeroDecimalsAndEmptyRpe() {
		assertEquals("5 × 100 kg", WorkoutFormatter.formatSet(ExerciseSet.of(5, 100)));
		assertEquals("10 × 72.5 kg, RPE 8", WorkoutFormatter.formatSet(new ExerciseSet(null, 10, 72.5, 8.0)));
		assertEquals("12 × 0 kg", WorkoutFormatter.formatSet(ExerciseSet.of(12, 0)));
	}

	@Test
	void detailsDescribeEverything() {
		Workout workout = new Workout(1L, DAY, WorkoutStatus.DONE, "Felt strong", Set.of("legs", "heavy"), List.of(
				WorkoutExercise.of(squat, List.of(new ExerciseSet(null, 5, 100, 8.0), ExerciseSet.of(5, 100))),
				new WorkoutExercise(null, bench, List.of(ExerciseSet.of(8, 60)), "Pause reps")));

		assertEquals(String.join("\n",
				"2026-11-23 · DONE",
				"Tags: heavy, legs",
				"Notes: Felt strong",
				"",
				"Squat",
				"  1. 5 × 100 kg, RPE 8",
				"  2. 5 × 100 kg",
				"",
				"Bench Press (Pause reps)",
				"  1. 8 × 60 kg"), WorkoutFormatter.details(workout));
	}

	@Test
	void detailsOfBareWorkoutHaveOnlyTheHeader() {
		assertEquals("2026-11-23 · SKIPPED", WorkoutFormatter.details(Workout.of(DAY, WorkoutStatus.SKIPPED)));
	}
}
