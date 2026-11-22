package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.db.DataAccessException;
import io.github.vitamin646dev.schedstatapp.db.Database;
import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.repository.ExerciseRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SqliteExerciseRepository implements ExerciseRepository {

	private static final String COLUMNS = "id, name, muscle_group, notes";

	private final Database database;

	public SqliteExerciseRepository(Database database) {
		this.database = database;
	}

	@Override
	public Exercise save(Exercise exercise) {
		try (Connection connection = database.connection()) {
			if (exercise.id() == null) {
				return insert(connection, exercise);
			}
			return update(connection, exercise);
		} catch (SQLException e) {
			throw new DataAccessException("Cannot save exercise '" + exercise.name() + "'", e);
		}
	}

	private Exercise insert(Connection connection, Exercise exercise) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO exercise (name, muscle_group, notes) VALUES (?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, exercise.name());
			statement.setString(2, exercise.muscleGroup());
			statement.setString(3, exercise.notes());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				keys.next();
				return new Exercise(keys.getLong(1), exercise.name(), exercise.muscleGroup(), exercise.notes());
			}
		}
	}

	private Exercise update(Connection connection, Exercise exercise) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"UPDATE exercise SET name = ?, muscle_group = ?, notes = ? WHERE id = ?")) {
			statement.setString(1, exercise.name());
			statement.setString(2, exercise.muscleGroup());
			statement.setString(3, exercise.notes());
			statement.setLong(4, exercise.id());
			if (statement.executeUpdate() == 0) {
				throw new DataAccessException("Exercise not found: " + exercise.id());
			}
			return exercise;
		}
	}

	@Override
	public Optional<Exercise> findById(long id) {
		return findOne("SELECT " + COLUMNS + " FROM exercise WHERE id = ?", statement -> statement.setLong(1, id));
	}

	@Override
	public Optional<Exercise> findByName(String name) {
		return findOne("SELECT " + COLUMNS + " FROM exercise WHERE name = ?", statement -> statement.setString(1, name.strip()));
	}

	@Override
	public List<Exercise> findAll() {
		try (Connection connection = database.connection();
			 PreparedStatement statement = connection.prepareStatement(
					 "SELECT " + COLUMNS + " FROM exercise ORDER BY name");
			 ResultSet rs = statement.executeQuery()) {
			List<Exercise> result = new ArrayList<>();
			while (rs.next()) {
				result.add(map(rs));
			}
			return result;
		} catch (SQLException e) {
			throw new DataAccessException("Cannot load exercises", e);
		}
	}

	@Override
	public boolean delete(long id) {
		try (Connection connection = database.connection();
			 PreparedStatement statement = connection.prepareStatement("DELETE FROM exercise WHERE id = ?")) {
			statement.setLong(1, id);
			return statement.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new DataAccessException("Cannot delete exercise " + id + " (is it used by a workout?)", e);
		}
	}

	private Optional<Exercise> findOne(String sql, Binder binder) {
		try (Connection connection = database.connection();
			 PreparedStatement statement = connection.prepareStatement(sql)) {
			binder.bind(statement);
			try (ResultSet rs = statement.executeQuery()) {
				return rs.next() ? Optional.of(map(rs)) : Optional.empty();
			}
		} catch (SQLException e) {
			throw new DataAccessException("Cannot load exercise", e);
		}
	}

	private static Exercise map(ResultSet rs) throws SQLException {
		return new Exercise(rs.getLong("id"), rs.getString("name"), rs.getString("muscle_group"), rs.getString("notes"));
	}

	@FunctionalInterface
	private interface Binder {
		void bind(PreparedStatement statement) throws SQLException;
	}
}
