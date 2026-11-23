package io.github.vitamin646dev.schedstatapp.service;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.ExerciseSet;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutExercise;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeekSummaryTest {

	// Monday 2026-11-23 .. Sunday 2026-11-29
	private static final LocalDate MONDAY = LocalDate.of(2026, 11, 23);

	private final Exercise squat = new Exercise(1L, "Squat", "Legs", null);

	private Workout workout(LocalDate date, WorkoutStatus status, ExerciseSet... sets) {
		return new Workout(null, date, status, null, Set.of(),
				List.of(WorkoutExercise.of(squat, List.of(sets))));
	}

	@Test
	void weekStartIsTheMondayOfTheWeek() {
		assertEquals(MONDAY, WeekSummary.weekStart(MONDAY));
		assertEquals(MONDAY, WeekSummary.weekStart(LocalDate.of(2026, 11, 29)));
		assertEquals(MONDAY.plusDays(7), WeekSummary.weekStart(LocalDate.of(2026, 11, 30)));
	}

	@Test
	void countsStatusesAndTotalsOnlyDoneWorkouts() {
		List<Workout> workouts = List.of(
				workout(MONDAY, WorkoutStatus.DONE, ExerciseSet.of(5, 100), ExerciseSet.of(5, 102.5)),
				workout(MONDAY.plusDays(2), WorkoutStatus.DONE, ExerciseSet.of(10, 50)),
				workout(MONDAY.plusDays(4), WorkoutStatus.PLANNED, ExerciseSet.of(5, 200)),
				workout(MONDAY.plusDays(5), WorkoutStatus.SKIPPED, ExerciseSet.of(5, 200)));

		WeekSummary summary = WeekSummary.of(MONDAY.plusDays(3), workouts);

		assertEquals(MONDAY, summary.start());
		assertEquals(MONDAY.plusDays(6), summary.end());
		assertEquals(2, summary.done());
		assertEquals(1, summary.planned());
		assertEquals(1, summary.skipped());
		assertEquals(3, summary.sets());
		assertEquals(1512.5, summary.volumeKg());
	}

	@Test
	void ignoresWorkoutsOutsideTheWeek() {
		List<Workout> workouts = List.of(
				workout(MONDAY.minusDays(1), WorkoutStatus.DONE, ExerciseSet.of(5, 100)),
				workout(MONDAY.plusDays(7), WorkoutStatus.DONE, ExerciseSet.of(5, 100)));

		assertEquals(new WeekSummary(MONDAY, MONDAY.plusDays(6), 0, 0, 0, 0, 0),
				WeekSummary.of(MONDAY, workouts));
	}

	@Test
	void describeShowsCountsAndWholeNumberVolume() {
		WeekSummary summary = new WeekSummary(MONDAY, MONDAY.plusDays(6), 2, 1, 0, 18, 7240);

		assertEquals("2 done, 1 planned, 0 skipped\n18 sets, 7240 kg volume", summary.describe());
	}
}
