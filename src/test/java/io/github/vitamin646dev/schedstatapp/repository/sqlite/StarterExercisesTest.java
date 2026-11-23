package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StarterExercisesTest extends RepositoryTestSupport {

	@Test
	void freshDatabaseContainsStarterExercises() {
		openDatabaseWithStarterExercises();

		List<Exercise> all = exercises.findAll();

		assertTrue(all.size() >= 15, "Expected a starter catalog, got " + all.size());
		assertEquals("Legs", exercises.findByName("squat").orElseThrow().muscleGroup());
		assertTrue(exercises.findByName("Bench Press").isPresent());
		assertTrue(exercises.findByName("Deadlift").isPresent());
		assertTrue(all.stream().allMatch(e -> e.muscleGroup() != null));
	}
}
