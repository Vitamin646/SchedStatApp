package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.db.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

abstract class RepositoryTestSupport {

	@TempDir
	Path dir;

	Database database;
	SqliteExerciseRepository exercises;
	SqliteWorkoutRepository workouts;

	@BeforeEach
	void openDatabase() {
		database = Database.open(dir.resolve("test.db"));
		exercises = new SqliteExerciseRepository(database);
		workouts = new SqliteWorkoutRepository(database);
	}
}
