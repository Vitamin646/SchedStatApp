package io.github.vitamin646dev.schedstatapp.repository;

import io.github.vitamin646dev.schedstatapp.model.Workout;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkoutRepository {

	/**
	 * Inserts the workout when its id is null, otherwise replaces it together with its tags,
	 * exercises and sets. Every exercise must already be saved (have an id).
	 *
	 * @return the saved workout with all ids set
	 */
	Workout save(Workout workout);

	Optional<Workout> findById(long id);

	/**
	 * Workouts with {@code from <= date <= to}, ordered by date and then id.
	 */
	List<Workout> findByDateRange(LocalDate from, LocalDate to);

	/**
	 * Deletes the workout with its tags, exercises and sets.
	 *
	 * @return true if the workout existed
	 */
	boolean delete(long id);
}
