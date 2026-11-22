package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.db.DataAccessException;
import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteExerciseRepositoryTest extends RepositoryTestSupport {

	@Test
	void saveAssignsIdAndCanBeReadBack() {
		Exercise saved = exercises.save(new Exercise(null, "Squat", "Legs", "Low bar"));

		assertNotNull(saved.id());
		assertEquals(Optional.of(saved), exercises.findById(saved.id()));
	}

	@Test
	void findByNameIgnoresCase() {
		Exercise saved = exercises.save(Exercise.of("Bench Press", "Chest"));

		assertEquals(Optional.of(saved), exercises.findByName("bench press"));
		assertEquals(Optional.empty(), exercises.findByName("Deadlift"));
	}

	@Test
	void saveWithIdUpdates() {
		Exercise saved = exercises.save(Exercise.of("Squat", "Legs"));

		exercises.save(new Exercise(saved.id(), "Back Squat", "Quads", null));

		assertEquals("Back Squat", exercises.findById(saved.id()).orElseThrow().name());
		assertEquals(1, exercises.findAll().size());
	}

	@Test
	void updatingUnknownIdFails() {
		assertThrows(DataAccessException.class, () -> exercises.save(new Exercise(999L, "Ghost", null, null)));
	}

	@Test
	void duplicateNamesAreRejectedIgnoringCase() {
		exercises.save(Exercise.of("Squat", "Legs"));

		assertThrows(DataAccessException.class, () -> exercises.save(Exercise.of("SQUAT", "Legs")));
	}

	@Test
	void findAllIsOrderedByName() {
		exercises.save(Exercise.of("Squat", null));
		exercises.save(Exercise.of("Bench Press", null));
		exercises.save(Exercise.of("Deadlift", null));

		assertEquals(List.of("Bench Press", "Deadlift", "Squat"),
				exercises.findAll().stream().map(Exercise::name).toList());
	}

	@Test
	void deleteRemovesUnusedExercise() {
		Exercise saved = exercises.save(Exercise.of("Squat", null));

		assertTrue(exercises.delete(saved.id()));
		assertFalse(exercises.delete(saved.id()));
		assertEquals(Optional.empty(), exercises.findById(saved.id()));
	}

	@Test
	void exerciseUsedByWorkoutCannotBeDeleted() {
		Exercise squat = exercises.save(Exercise.of("Squat", null));
		workouts.save(new Workout(null, LocalDate.of(2026, 11, 22), WorkoutStatus.DONE, null, Set.of(),
				List.of(WorkoutExercise.of(squat, List.of(ExerciseSet.of(5, 100))))));

		assertThrows(DataAccessException.class, () -> exercises.delete(squat.id()));
		assertTrue(exercises.findById(squat.id()).isPresent());
	}
}
