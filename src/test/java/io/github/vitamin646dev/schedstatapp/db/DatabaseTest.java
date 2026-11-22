package io.github.vitamin646dev.schedstatapp.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseTest {

	@TempDir
	Path dir;

	@Test
	void openCreatesFileAndSchema() throws SQLException {
		Path file = dir.resolve("nested").resolve("test.db");

		Database database = Database.open(file);

		assertTrue(Files.exists(file));
		try (Connection connection = database.connection();
			 Statement statement = connection.createStatement();
			 ResultSet rs = statement.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'")) {
			Set<String> tables = new HashSet<>();
			while (rs.next()) {
				tables.add(rs.getString(1));
			}
			assertTrue(tables.containsAll(Set.of(
					"exercise", "workout", "workout_tag", "workout_exercise", "exercise_set")), tables.toString());
		}
	}

	@Test
	void openingExistingDatabaseKeepsDataAndDoesNotFail() throws SQLException {
		Path file = dir.resolve("test.db");
		Database first = Database.open(file);
		try (Connection connection = first.connection();
			 Statement statement = connection.createStatement()) {
			statement.executeUpdate("INSERT INTO exercise (name) VALUES ('Squat')");
		}

		Database second = Database.open(file);

		try (Connection connection = second.connection();
			 Statement statement = connection.createStatement();
			 ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM exercise")) {
			rs.next();
			assertEquals(1, rs.getInt(1));
		}
	}

	@Test
	void foreignKeysAreEnforced() throws SQLException {
		Database database = Database.open(dir.resolve("test.db"));
		try (Connection connection = database.connection();
			 Statement statement = connection.createStatement();
			 ResultSet rs = statement.executeQuery("PRAGMA foreign_keys")) {
			rs.next();
			assertEquals(1, rs.getInt(1));
		}
	}
}
