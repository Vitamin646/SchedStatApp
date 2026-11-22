package io.github.vitamin646dev.schedstatapp.model;

import java.util.Objects;

/**
 * An entry of the exercise catalog. {@code id} is null until the exercise is saved.
 */
public record Exercise(Long id, String name, String muscleGroup, String notes) {

	public Exercise {
		Objects.requireNonNull(name, "name");
		name = name.strip();
		if (name.isEmpty()) {
			throw new IllegalArgumentException("Exercise name must not be blank");
		}
	}

	public static Exercise of(String name, String muscleGroup) {
		return new Exercise(null, name, muscleGroup, null);
	}
}
