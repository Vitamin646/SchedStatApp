package io.github.vitamin646dev.schedstatapp.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModelValidationTest {

	@Test
	void exerciseNameIsStrippedAndMustNotBeBlank() {
		assertEquals("Squat", Exercise.of("  Squat ", "Legs").name());
		assertThrows(IllegalArgumentException.class, () -> Exercise.of("   ", null));
	}

	@Test
	void setRejectsInvalidValues() {
		assertThrows(IllegalArgumentException.class, () -> ExerciseSet.of(0, 50));
		assertThrows(IllegalArgumentException.class, () -> ExerciseSet.of(5, -1));
		assertThrows(IllegalArgumentException.class, () -> new ExerciseSet(null, 5, 50, 11.0));
		assertThrows(IllegalArgumentException.class, () -> new ExerciseSet(null, 5, 50, 0.5));
	}

	@Test
	void setAcceptsBodyweightAndOptionalRpe() {
		assertEquals(0.0, ExerciseSet.of(10, 0).weightKg());
		assertEquals(8.5, new ExerciseSet(null, 5, 100, 8.5).rpe());
	}

	@Test
	void workoutNormalizesTagsAndCopiesCollections() {
		Workout workout = new Workout(null, LocalDate.of(2026, 11, 22), WorkoutStatus.PLANNED, null,
				Set.of(" legs ", "", "heavy"), List.of());

		assertEquals(List.of("heavy", "legs"), List.copyOf(workout.tags()));
	}
}
