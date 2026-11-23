package io.github.vitamin646dev.schedstatapp.service;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A workout being entered: sets are added one by one and turned into a {@link Workout} on save.
 * Consecutive sets of the same exercise are grouped into one {@link WorkoutExercise}.
 */
public class WorkoutDraft {

	/**
	 * One entered set together with its exercise.
	 */
	public record Entry(Exercise exercise, ExerciseSet set) {
	}

	private final List<Entry> entries = new ArrayList<>();

	/**
	 * @param exercise a saved exercise (it must have an id)
	 */
	public void addSet(Exercise exercise, ExerciseSet set) {
		Objects.requireNonNull(exercise, "exercise");
		Objects.requireNonNull(set, "set");
		if (exercise.id() == null) {
			throw new IllegalArgumentException("Exercise must be saved first: " + exercise.name());
		}
		entries.add(new Entry(exercise, set));
	}

	public void removeEntry(int index) {
		entries.remove(index);
	}

	public void clear() {
		entries.clear();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	public List<Entry> entries() {
		return Collections.unmodifiableList(entries);
	}

	/**
	 * Builds an unsaved workout. Blank notes become null; tags are comma separated.
	 */
	public Workout toWorkout(LocalDate date, WorkoutStatus status, String notes, String tagsText) {
		List<WorkoutExercise> exercises = new ArrayList<>();
		Exercise current = null;
		List<ExerciseSet> currentSets = new ArrayList<>();
		for (Entry entry : entries) {
			if (current != null && !current.id().equals(entry.exercise().id())) {
				exercises.add(WorkoutExercise.of(current, currentSets));
				currentSets = new ArrayList<>();
			}
			current = entry.exercise();
			currentSets.add(entry.set());
		}
		if (current != null) {
			exercises.add(WorkoutExercise.of(current, currentSets));
		}

		String cleanedNotes = notes == null || notes.isBlank() ? null : notes.strip();
		return new Workout(null, date, status, cleanedNotes, parseTags(tagsText), exercises);
	}

	/**
	 * Splits comma separated tags, dropping blanks and duplicates.
	 */
	public static Set<String> parseTags(String text) {
		Set<String> tags = new LinkedHashSet<>();
		if (text == null) {
			return tags;
		}
		for (String part : text.split(",")) {
			String tag = part.strip();
			if (!tag.isEmpty()) {
				tags.add(tag);
			}
		}
		return tags;
	}

	/**
	 * Parses user input for a set. Decimal comma is accepted; RPE may be left empty.
	 *
	 * @throws IllegalArgumentException with a message suitable for showing to the user
	 */
	public static ExerciseSet parseSet(String repsText, String weightText, String rpeText) {
		int reps;
		try {
			reps = Integer.parseInt(clean(repsText));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Reps must be a whole number");
		}
		double weight = parseNumber(weightText, "Weight must be a number");
		Double rpe = clean(rpeText).isEmpty() ? null : parseNumber(rpeText, "RPE must be a number");
		return new ExerciseSet(null, reps, weight, rpe);
	}

	private static double parseNumber(String text, String message) {
		try {
			double value = Double.parseDouble(clean(text).replace(',', '.'));
			if (Double.isNaN(value) || Double.isInfinite(value)) {
				throw new IllegalArgumentException(message);
			}
			return value;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(message);
		}
	}

	private static String clean(String text) {
		return text == null ? "" : text.strip();
	}
}
