package io.github.vitamin646dev.schedstatapp.service;

import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

/**
 * Counts and totals for one calendar week (Monday to Sunday). Sets and volume only come from
 * workouts that are DONE; planned and skipped workouts are only counted.
 */
public record WeekSummary(LocalDate start, LocalDate end, int done, int planned, int skipped,
						  int sets, double volumeKg) {

	public static LocalDate weekStart(LocalDate day) {
		return day.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}

	/**
	 * Summary of the week containing {@code day}. Workouts outside that week are ignored.
	 */
	public static WeekSummary of(LocalDate day, List<Workout> workouts) {
		LocalDate start = weekStart(day);
		LocalDate end = start.plusDays(6);
		int done = 0;
		int planned = 0;
		int skipped = 0;
		int sets = 0;
		double volume = 0;
		for (Workout workout : workouts) {
			if (workout.date().isBefore(start) || workout.date().isAfter(end)) {
				continue;
			}
			switch (workout.status()) {
				case PLANNED -> planned++;
				case SKIPPED -> skipped++;
				case DONE -> {
					done++;
					for (WorkoutExercise exercise : workout.exercises()) {
						for (ExerciseSet set : exercise.sets()) {
							sets++;
							volume += set.reps() * set.weightKg();
						}
					}
				}
			}
		}
		return new WeekSummary(start, end, done, planned, skipped, sets, volume);
	}

	/**
	 * For example {@code 2 done, 1 planned, 0 skipped} and {@code 18 sets, 7240 kg volume}.
	 */
	public String describe() {
		return done + " done, " + planned + " planned, " + skipped + " skipped\n"
				+ sets + " sets, " + WorkoutFormatter.formatNumber(volumeKg) + " kg volume";
	}
}
