package io.github.vitamin646dev.schedstatapp.repository.sqlite;

import io.github.vitamin646dev.schedstatapp.db.DataAccessException;
import io.github.vitamin646dev.schedstatapp.db.Database;
import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import io.github.vitamin646dev.schedstatapp.repository.WorkoutRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class SqliteWorkoutRepository implements WorkoutRepository {

	private final Database database;

	public SqliteWorkoutRepository(Database database) {
		this.database = database;
	}

	@Override
	public Workout save(Workout workout) {
		try (Connection connection = database.connection()) {
			connection.setAutoCommit(false);
			try {
				long id = workout.id() == null ? insertWorkout(connection, workout) : replaceWorkout(connection, workout);
				insertChildren(connection, id, workout);
				Workout saved = load(connection, id).orElseThrow(() -> new DataAccessException("Workout vanished: " + id));
				connection.commit();
				return saved;
			} catch (SQLException | RuntimeException e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new DataAccessException("Cannot save workout of " + workout.date(), e);
		}
	}

	@Override
	public Optional<Workout> findById(long id) {
		try (Connection connection = database.connection()) {
			return load(connection, id);
		} catch (SQLException e) {
			throw new DataAccessException("Cannot load workout " + id, e);
		}
	}

	@Override
	public List<Workout> findByDateRange(LocalDate from, LocalDate to) {
		try (Connection connection = database.connection()) {
			List<Long> ids = new ArrayList<>();
			try (PreparedStatement statement = connection.prepareStatement(
					"SELECT id FROM workout WHERE date >= ? AND date <= ? ORDER BY date, id")) {
				statement.setString(1, from.toString());
				statement.setString(2, to.toString());
				try (ResultSet rs = statement.executeQuery()) {
					while (rs.next()) {
						ids.add(rs.getLong(1));
					}
				}
			}
			List<Workout> result = new ArrayList<>();
			for (long id : ids) {
				load(connection, id).ifPresent(result::add);
			}
			return result;
		} catch (SQLException e) {
			throw new DataAccessException("Cannot load workouts between " + from + " and " + to, e);
		}
	}

	@Override
	public boolean delete(long id) {
		try (Connection connection = database.connection()) {
			connection.setAutoCommit(false);
			try {
				deleteChildren(connection, id);
				boolean deleted;
				try (PreparedStatement statement = connection.prepareStatement("DELETE FROM workout WHERE id = ?")) {
					statement.setLong(1, id);
					deleted = statement.executeUpdate() > 0;
				}
				connection.commit();
				return deleted;
			} catch (SQLException | RuntimeException e) {
				connection.rollback();
				throw e;
			}
		} catch (SQLException e) {
			throw new DataAccessException("Cannot delete workout " + id, e);
		}
	}

	private long insertWorkout(Connection connection, Workout workout) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO workout (date, status, notes) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
			statement.setString(1, workout.date().toString());
			statement.setString(2, workout.status().name());
			statement.setString(3, workout.notes());
			statement.executeUpdate();
			try (ResultSet keys = statement.getGeneratedKeys()) {
				keys.next();
				return keys.getLong(1);
			}
		}
	}

	private long replaceWorkout(Connection connection, Workout workout) throws SQLException {
		long id = workout.id();
		try (PreparedStatement statement = connection.prepareStatement(
				"UPDATE workout SET date = ?, status = ?, notes = ? WHERE id = ?")) {
			statement.setString(1, workout.date().toString());
			statement.setString(2, workout.status().name());
			statement.setString(3, workout.notes());
			statement.setLong(4, id);
			if (statement.executeUpdate() == 0) {
				throw new DataAccessException("Workout not found: " + id);
			}
		}
		deleteChildren(connection, id);
		return id;
	}

	private void deleteChildren(Connection connection, long workoutId) throws SQLException {
		execute(connection, "DELETE FROM exercise_set WHERE workout_exercise_id IN "
				+ "(SELECT id FROM workout_exercise WHERE workout_id = ?)", workoutId);
		execute(connection, "DELETE FROM workout_exercise WHERE workout_id = ?", workoutId);
		execute(connection, "DELETE FROM workout_tag WHERE workout_id = ?", workoutId);
	}

	private void execute(Connection connection, String sql, long param) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(sql)) {
			statement.setLong(1, param);
			statement.executeUpdate();
		}
	}

	private void insertChildren(Connection connection, long workoutId, Workout workout) throws SQLException {
		try (PreparedStatement statement = connection.prepareStatement(
				"INSERT INTO workout_tag (workout_id, tag) VALUES (?, ?)")) {
			for (String tag : workout.tags()) {
				statement.setLong(1, workoutId);
				statement.setString(2, tag);
				statement.executeUpdate();
			}
		}

		int exercisePosition = 0;
		for (WorkoutExercise workoutExercise : workout.exercises()) {
			Long exerciseId = workoutExercise.exercise().id();
			if (exerciseId == null) {
				throw new IllegalArgumentException("Exercise '" + workoutExercise.exercise().name()
						+ "' must be saved before it is added to a workout");
			}
			long workoutExerciseId;
			try (PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO workout_exercise (workout_id, exercise_id, position, notes) VALUES (?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS)) {
				statement.setLong(1, workoutId);
				statement.setLong(2, exerciseId);
				statement.setInt(3, exercisePosition++);
				statement.setString(4, workoutExercise.notes());
				statement.executeUpdate();
				try (ResultSet keys = statement.getGeneratedKeys()) {
					keys.next();
					workoutExerciseId = keys.getLong(1);
				}
			}

			try (PreparedStatement statement = connection.prepareStatement(
					"INSERT INTO exercise_set (workout_exercise_id, position, reps, weight_kg, rpe) VALUES (?, ?, ?, ?, ?)")) {
				int setPosition = 0;
				for (ExerciseSet set : workoutExercise.sets()) {
					statement.setLong(1, workoutExerciseId);
					statement.setInt(2, setPosition++);
					statement.setInt(3, set.reps());
					statement.setDouble(4, set.weightKg());
					if (set.rpe() == null) {
						statement.setNull(5, Types.REAL);
					} else {
						statement.setDouble(5, set.rpe());
					}
					statement.executeUpdate();
				}
			}
		}
	}

	private Optional<Workout> load(Connection connection, long id) throws SQLException {
		LocalDate date;
		WorkoutStatus status;
		String notes;
		try (PreparedStatement statement = connection.prepareStatement(
				"SELECT date, status, notes FROM workout WHERE id = ?")) {
			statement.setLong(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				if (!rs.next()) {
					return Optional.empty();
				}
				date = LocalDate.parse(rs.getString("date"));
				status = WorkoutStatus.valueOf(rs.getString("status"));
				notes = rs.getString("notes");
			}
		}

		Set<String> tags = new HashSet<>();
		try (PreparedStatement statement = connection.prepareStatement(
				"SELECT tag FROM workout_tag WHERE workout_id = ?")) {
			statement.setLong(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					tags.add(rs.getString(1));
				}
			}
		}

		Map<Long, List<ExerciseSet>> setsByWorkoutExercise = new LinkedHashMap<>();
		try (PreparedStatement statement = connection.prepareStatement(
				"SELECT s.id, s.workout_exercise_id, s.reps, s.weight_kg, s.rpe "
						+ "FROM exercise_set s JOIN workout_exercise we ON we.id = s.workout_exercise_id "
						+ "WHERE we.workout_id = ? ORDER BY s.workout_exercise_id, s.position")) {
			statement.setLong(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					double rpe = rs.getDouble("rpe");
					Double rpeValue = rs.wasNull() ? null : rpe;
					setsByWorkoutExercise
							.computeIfAbsent(rs.getLong("workout_exercise_id"), key -> new ArrayList<>())
							.add(new ExerciseSet(rs.getLong("id"), rs.getInt("reps"), rs.getDouble("weight_kg"), rpeValue));
				}
			}
		}

		List<WorkoutExercise> exercises = new ArrayList<>();
		try (PreparedStatement statement = connection.prepareStatement(
				"SELECT we.id AS we_id, we.notes AS we_notes, e.id AS e_id, e.name, e.muscle_group, e.notes AS e_notes "
						+ "FROM workout_exercise we JOIN exercise e ON e.id = we.exercise_id "
						+ "WHERE we.workout_id = ? ORDER BY we.position")) {
			statement.setLong(1, id);
			try (ResultSet rs = statement.executeQuery()) {
				while (rs.next()) {
					long workoutExerciseId = rs.getLong("we_id");
					Exercise exercise = new Exercise(rs.getLong("e_id"), rs.getString("name"),
							rs.getString("muscle_group"), rs.getString("e_notes"));
					exercises.add(new WorkoutExercise(workoutExerciseId, exercise,
							setsByWorkoutExercise.getOrDefault(workoutExerciseId, List.of()), rs.getString("we_notes")));
				}
			}
		}

		return Optional.of(new Workout(id, date, status, notes, tags, exercises));
	}
}
