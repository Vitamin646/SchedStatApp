package io.github.vitamin646dev.schedstatapp.repository;

import io.github.vitamin646dev.schedstatapp.model.Exercise;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository {

	/**
	 * Inserts the exercise when its id is null, otherwise updates it.
	 *
	 * @return the saved exercise with its id set
	 * @throws io.github.vitamin646dev.schedstatapp.db.DataAccessException if the name is already taken
	 *                                                                     or the id does not exist
	 */
	Exercise save(Exercise exercise);

	Optional<Exercise> findById(long id);

	/**
	 * Case-insensitive lookup by name.
	 */
	Optional<Exercise> findByName(String name);

	/**
	 * All exercises ordered by name.
	 */
	List<Exercise> findAll();

	/**
	 * @return true if the exercise existed and was deleted
	 * @throws io.github.vitamin646dev.schedstatapp.db.DataAccessException if a workout still uses it
	 */
	boolean delete(long id);
}
