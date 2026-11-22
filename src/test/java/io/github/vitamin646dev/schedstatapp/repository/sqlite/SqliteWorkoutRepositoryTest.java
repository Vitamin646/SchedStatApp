package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.db.Database;
import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqliteWorkoutRepositoryTest extends RepositoryTestSupport {

	private static final LocalDate DAY = LocalDate.of(2026, 11, 22);

	private Exercise squat;
	private Exercise bench;

	@BeforeEach
	void createExercises() {
		squat = exercises.save(Exercise.of("Squat", "Legs"));
		bench = exercises.save(Exercise.of("Bench Press", "Chest"));
	}

	private Workout sampleWorkout() {
		return new Workout(null, DAY, WorkoutStatus.DONE, "Felt strong", Set.of("legs", "heavy"), List.of(
				WorkoutExercise.of(squat, List.of(new ExerciseSet(null, 5, 100, 8.0), ExerciseSet.of(5, 100))),
				new WorkoutExercise(null, bench, List.of(ExerciseSet.of(8, 60)), "Pause reps")));
	}

	@Test
	void saveAndLoadRoundTrip() {
		Workout saved = workouts.save(sampleWorkout());

		assertNotNull(saved.id());
		Workout loaded = workouts.findById(saved.id()).orElseThrow();
		assertEquals(saved, loaded);
		assertEquals(DAY, loaded.date());
		assertEquals(WorkoutStatus.DONE, loaded.status());
		assertEquals("Felt strong", loaded.notes());
		assertEquals(Set.of("heavy", "legs"), loaded.tags());
		assertEquals(List.of("Squat", "Bench Press"),
				loaded.exercises().stream().map(e -> e.exercise().name()).toList());
		assertEquals("Pause reps", loaded.exercises().get(1).notes());
		List<ExerciseSet> squatSets = loaded.exercises().get(0).sets();
		assertEquals(2, squatSets.size());
		assertEquals(8.0, squatSets.get(0).rpe());
		assertNull(squatSets.get(1).rpe());
		assertNotNull(squatSets.get(0).id());
	}

	@Test
	void savingWithIdReplacesTagsExercisesAndSets() {
		Workout saved = workouts.save(sampleWorkout());

		Workout changed = new Workout(saved.id(), DAY.plusDays(1), WorkoutStatus.SKIPPED, null, Set.of("rest"),
				List.of(WorkoutExercise.of(bench, List.of(ExerciseSet.of(3, 80)))));
		workouts.save(changed);

		Workout loaded = workouts.findById(saved.id()).orElseThrow();
		assertEquals(DAY.plusDays(1), loaded.date());
		assertEquals(WorkoutStatus.SKIPPED, loaded.status());
		assertEquals(Set.of("rest"), loaded.tags());
		assertEquals(1, loaded.exercises().size());
		assertEquals(1, loaded.exercises().get(0).sets().size());
		assertEquals(1, count("workout"));
		assertEquals(1, count("workout_exercise"));
		assertEquals(1, count("exercise_set"));
	}

	@Test
	void exerciseOrderIsPreserved() {
		Workout saved = workouts.save(new Workout(null, DAY, WorkoutStatus.PLANNED, null, Set.of(), List.of(
				WorkoutExercise.of(bench, List.of()), WorkoutExercise.of(squat, List.of()),
				WorkoutExercise.of(bench, List.of()))));

		assertEquals(List.of("Bench Press", "Squat", "Bench Press"),
				workouts.findById(saved.id()).orElseThrow().exercises().stream().map(e -> e.exercise().name()).toList());
	}

	@Test
	void findByDateRangeIsInclusiveAndOrdered() {
		workouts.save(Workout.of(DAY.plusDays(2), WorkoutStatus.PLANNED));
		workouts.save(Workout.of(DAY, WorkoutStatus.DONE));
		workouts.save(Workout.of(DAY.plusDays(10), WorkoutStatus.PLANNED));
		workouts.save(Workout.of(DAY.minusDays(1), WorkoutStatus.DONE));

		List<LocalDate> dates = workouts.findByDateRange(DAY, DAY.plusDays(2)).stream().map(Workout::date).toList();

		assertEquals(List.of(DAY, DAY.plusDays(2)), dates);
	}

	@Test
	void deleteRemovesWorkoutAndChildren() {
		Workout saved = workouts.save(sampleWorkout());

		assertTrue(workouts.delete(saved.id()));
		assertFalse(workouts.delete(saved.id()));

		assertEquals(Optional.empty(), workouts.findById(saved.id()));
		assertEquals(0, count("workout_tag"));
		assertEquals(0, count("workout_exercise"));
		assertEquals(0, count("exercise_set"));
		assertEquals(2, exercises.findAll().size());
	}

	@Test
	void unsavedExerciseIsRejectedAndNothingIsWritten() {
		Workout workout = new Workout(null, DAY, WorkoutStatus.DONE, null, Set.of(),
				List.of(WorkoutExercise.of(Exercise.of("Unsaved", null), List.of())));

		assertThrows(IllegalArgumentException.class, () -> workouts.save(workout));
		assertEquals(0, count("workout"));
	}

	@Test
	void dataSurvivesReopeningTheDatabase() {
		Workout saved = workouts.save(sampleWorkout());

		Database reopened = Database.open(dir.resolve("test.db"));
		Optional<Workout> loaded = new SqliteWorkoutRepository(reopened).findById(saved.id());

		assertEquals(Optional.of(saved), loaded);
	}

	private int count(String table) {
		try (Connection connection = database.connection();
			 Statement statement = connection.createStatement();
			 ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM " + table)) {
			rs.next();
			return rs.getInt(1);
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
