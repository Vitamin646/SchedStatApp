package io.github.vitamin646dev.schedstatapp.controller;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.model.Interval;
import com.calendarfx.view.CalendarView;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import io.github.vitamin646dev.schedstatapp.repository.WorkoutRepository;
import io.github.vitamin646dev.schedstatapp.service.BackgroundExecutor;
import io.github.vitamin646dev.schedstatapp.service.NotificationService;
import io.github.vitamin646dev.schedstatapp.service.WorkoutFormatter;
import javafx.application.Platform;
import javafx.fxml.FXML;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Calendar of workouts. Every workout is a full-day entry; one read-only calendar per status
 * gives each status its own colour.
 */
public class CalendarController extends BaseController {
	/**
	 * Workouts are loaded for this many days before and after the date shown in the calendar.
	 */
	private static final int LOAD_WINDOW_DAYS = 366;

	private final WorkoutRepository workouts;
	private final BackgroundExecutor background;
	private final NotificationService notifications;
	private final Map<WorkoutStatus, Calendar> calendars = new EnumMap<>(WorkoutStatus.class);

	private int latestRequest;

	@FXML
	private CalendarView calendarView;

	public CalendarController(WorkoutRepository workouts, BackgroundExecutor background,
							  NotificationService notifications) {
		this.workouts = workouts;
		this.background = background;
		this.notifications = notifications;
	}

	@FXML
	public void initialize() {
		calendars.put(WorkoutStatus.DONE, createCalendar("Done", Calendar.Style.STYLE1));
		calendars.put(WorkoutStatus.PLANNED, createCalendar("Planned", Calendar.Style.STYLE2));
		calendars.put(WorkoutStatus.SKIPPED, createCalendar("Skipped", Calendar.Style.STYLE5));

		CalendarSource source = new CalendarSource("Workouts");
		source.getCalendars().addAll(calendars.values());
		calendarView.getCalendarSources().setAll(source);

		calendarView.dateProperty().addListener((observable, oldDate, newDate) -> {
			if (newDate != null) {
				reload();
			}
		});
	}

	@Override
	public void updateUI() {
		reload();
	}

	private static Calendar createCalendar(String name, Calendar.Style style) {
		Calendar calendar = new Calendar(name);
		calendar.setStyle(style);
		calendar.setReadOnly(true);
		return calendar;
	}

	private void reload() {
		LocalDate center = calendarView.getDate();
		LocalDate from = center.minusDays(LOAD_WINDOW_DAYS);
		LocalDate to = center.plusDays(LOAD_WINDOW_DAYS);
		int request = ++latestRequest;

		background.supply(() -> workouts.findByDateRange(from, to)).whenComplete((list, error) -> Platform.runLater(() -> {
			if (request != latestRequest) {
				return; // a newer request has been made since
			}
			if (error != null) {
				notifications.show("Cannot load workouts");
				return;
			}
			showWorkouts(list);
		}));
	}

	private void showWorkouts(List<Workout> list) {
		calendars.values().forEach(Calendar::clear);
		for (Workout workout : list) {
			Entry<Workout> entry = new Entry<>(WorkoutFormatter.title(workout));
			entry.setInterval(new Interval(workout.date().atStartOfDay(), workout.date().atTime(23, 59)));
			entry.setFullDay(true);
			entry.setUserObject(workout);
			calendars.get(workout.status()).addEntry(entry);
		}
	}
}
