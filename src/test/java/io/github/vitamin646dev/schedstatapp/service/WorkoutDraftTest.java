package io.github.vitamin646dev.schedstatapp.service;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WorkoutDraftTest {

	private static final LocalDate DAY = LocalDate.of(2026, 11, 23);

	private final Exercise squat = new Exercise(1L, "Squat", "Legs", null);
	private final Exercise bench = new Exercise(2L, "Bench Press", "Chest", null);

	@Test
	void consecutiveSetsOfTheSameExerciseAreGrouped() {
		WorkoutDraft draft = new WorkoutDraft();
		draft.addSet(squat, ExerciseSet.of(5, 100));
		draft.addSet(squat, ExerciseSet.of(5, 105));
		draft.addSet(bench, ExerciseSet.of(8, 60));
		draft.addSet(squat, ExerciseSet.of(3, 110));

		Workout workout = draft.toWorkout(DAY, WorkoutStatus.DONE, null, "");

		assertEquals(List.of("Squat", "Bench Press", "Squat"),
				workout.exercises().stream().map(e -> e.exercise().name()).toList());
		assertEquals(2, workout.exercises().get(0).sets().size());
		assertEquals(105.0, workout.exercises().get(0).sets().get(1).weightKg());
		assertEquals(1, workout.exercises().get(2).sets().size());
	}

	@Test
	void workoutGetsDateStatusNotesAndTags() {
		WorkoutDraft draft = new WorkoutDraft();
		draft.addSet(squat, ExerciseSet.of(5, 100));

		Workout workout = draft.toWorkout(DAY, WorkoutStatus.PLANNED, "  Felt good ", "legs, heavy");

		assertNull(workout.id());
		assertEquals(DAY, workout.date());
		assertEquals(WorkoutStatus.PLANNED, workout.status());
		assertEquals("Felt good", workout.notes());
		assertEquals(Set.of("legs", "heavy"), workout.tags());
	}

	@Test
	void blankNotesBecomeNull() {
		assertNull(new WorkoutDraft().toWorkout(DAY, WorkoutStatus.DONE, "   ", null).notes());
	}

	@Test
	void emptyDraftBuildsEmptyWorkout() {
		Workout workout = new WorkoutDraft().toWorkout(DAY, WorkoutStatus.DONE, null, null);

		assertTrue(workout.exercises().isEmpty());
	}

	@Test
	void unsavedExerciseIsRejected() {
		WorkoutDraft draft = new WorkoutDraft();

		assertThrows(IllegalArgumentException.class,
				() -> draft.addSet(Exercise.of("Unsaved", null), ExerciseSet.of(5, 100)));
	}

	@Test
	void removeAndClear() {
		WorkoutDraft draft = new WorkoutDraft();
		draft.addSet(squat, ExerciseSet.of(5, 100));
		draft.addSet(bench, ExerciseSet.of(8, 60));

		draft.removeEntry(0);
		assertEquals(1, draft.entries().size());
		assertEquals(bench, draft.entries().get(0).exercise());

		draft.clear();
		assertTrue(draft.isEmpty());
	}

	@Test
	void parseTagsSplitsOnCommasAndDropsBlanksAndDuplicates() {
		assertEquals(Set.of("legs", "heavy"), WorkoutDraft.parseTags(" legs ,, heavy, legs "));
		assertTrue(WorkoutDraft.parseTags(null).isEmpty());
	}

	@Test
	void parseSetAcceptsDecimalCommaAndEmptyRpe() {
		ExerciseSet set = WorkoutDraft.parseSet(" 5 ", "72,5", "");

		assertEquals(5, set.reps());
		assertEquals(72.5, set.weightKg());
		assertNull(set.rpe());
		assertEquals(8.5, WorkoutDraft.parseSet("5", "100", "8.5").rpe());
	}

	@Test
	void parseSetReportsReadableErrors() {
		assertEquals("Reps must be a whole number",
				assertThrows(IllegalArgumentException.class, () -> WorkoutDraft.parseSet("x", "100", "")).getMessage());
		assertEquals("Weight must be a number",
				assertThrows(IllegalArgumentException.class, () -> WorkoutDraft.parseSet("5", "", "")).getMessage());
		assertEquals("RPE must be a number",
				assertThrows(IllegalArgumentException.class, () -> WorkoutDraft.parseSet("5", "100", "hard")).getMessage());
		assertThrows(IllegalArgumentException.class, () -> WorkoutDraft.parseSet("0", "100", ""));
		assertThrows(IllegalArgumentException.class, () -> WorkoutDraft.parseSet("5", "-1", ""));
		assertThrows(IllegalArgumentException.class, () -> WorkoutDraft.parseSet("5", "NaN", ""));
	}
}
