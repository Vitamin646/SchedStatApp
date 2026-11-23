package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.db.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

abstract class RepositoryTestSupport {

	@TempDir
	Path dir;

	Database database;
	SqliteExerciseRepository exercises;
	SqliteWorkoutRepository workouts;

	/**
	 * Opens a fresh database. The starter exercises seeded by migrations are removed,
	 * so tests start from an empty catalog; use {@link #openDatabaseWithStarterExercises()}
	 * to keep them.
	 */
	@BeforeEach
	void openDatabase() {
		open("test.db");
		clearExercises();
	}

	/**
	 * Replaces the database with a separate, freshly migrated one that still has the starter exercises.
	 */
	void openDatabaseWithStarterExercises() {
		open("starter.db");
	}

	private void open(String fileName) {
		database = Database.open(dir.resolve(fileName));
		exercises = new SqliteExerciseRepository(database);
		workouts = new SqliteWorkoutRepository(database);
	}

	private void clearExercises() {
		try (Connection connection = database.connection();
			 Statement statement = connection.createStatement()) {
			statement.executeUpdate("DELETE FROM exercise");
		} catch (SQLException e) {
			throw new IllegalStateException(e);
		}
	}
}
