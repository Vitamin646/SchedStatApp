package io.github.vitamin646dev.schedstatapp.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * A training session on a date. Tags are kept trimmed, non-blank and sorted.
 */
public record Workout(Long id, LocalDate date, WorkoutStatus status, String notes,
					  Set<String> tags, List<WorkoutExercise> exercises) {

	public Workout {
		Objects.requireNonNull(date, "date");
		Objects.requireNonNull(status, "status");

		TreeSet<String> cleaned = new TreeSet<>();
		for (String tag : tags) {
			String stripped = tag.strip();
			if (!stripped.isEmpty()) {
				cleaned.add(stripped);
			}
		}
		tags = Collections.unmodifiableSet(cleaned);
		exercises = List.copyOf(exercises);
	}

	public static Workout of(LocalDate date, WorkoutStatus status) {
		return new Workout(null, date, status, null, Set.of(), List.of());
	}
}
