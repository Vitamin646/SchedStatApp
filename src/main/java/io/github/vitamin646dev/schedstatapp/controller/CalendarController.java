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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Calendar of workouts. Every workout is a full-day entry; one read-only calendar per status
 * gives each status its own colour. Below the calendar, the workouts of the shown day are listed
 * with their details and can be deleted.
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

	private List<Workout> loaded = List.of();
	private int latestRequest;

	@FXML
	private CalendarView calendarView;
	@FXML
	private Label dayLabel;
	@FXML
	private ListView<Workout> dayWorkoutList;
	@FXML
	private TextArea detailsArea;
	@FXML
	private Button deleteButton;
	@FXML
	private Label statusLabel;

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

		dayWorkoutList.setCellFactory(list -> new ListCell<>() {
			@Override
			protected void updateItem(Workout workout, boolean empty) {
				super.updateItem(workout, empty);
				setText(empty || workout == null ? null : workout.status() + ": " + WorkoutFormatter.title(workout));
			}
		});
		dayWorkoutList.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldWorkout, newWorkout) -> showDetails(newWorkout));

		calendarView.dateProperty().addListener((observable, oldDate, newDate) -> {
			if (newDate != null) {
				showDay();
				reload();
			}
		});
		showDay();
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

	@FXML
	protected void onDeleteButtonClick() {
		Workout selected = dayWorkoutList.getSelectionModel().getSelectedItem();
		if (selected == null) {
			statusLabel.setText("Select a workout to delete");
			return;
		}

		Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION,
				"Delete the workout of " + selected.date() + " (" + WorkoutFormatter.title(selected) + ")?",
				ButtonType.OK, ButtonType.CANCEL);
		confirmation.setHeaderText(null);
		Optional<ButtonType> answer = confirmation.showAndWait();
		if (answer.isEmpty() || answer.get() != ButtonType.OK) {
			return;
		}

		background.supply(() -> workouts.delete(selected.id())).whenComplete((deleted, error) -> Platform.runLater(() -> {
			if (error != null) {
				statusLabel.setText("Cannot delete the workout");
				notifications.show("Cannot delete the workout");
			} else {
				statusLabel.setText("");
			}
			reload();
		}));
	}

	private void showWorkouts(List<Workout> list) {
		loaded = list;
		calendars.values().forEach(Calendar::clear);
		for (Workout workout : list) {
			Entry<Workout> entry = new Entry<>(WorkoutFormatter.title(workout));
			entry.setInterval(new Interval(workout.date().atStartOfDay(), workout.date().atTime(23, 59)));
			entry.setFullDay(true);
			entry.setUserObject(workout);
			calendars.get(workout.status()).addEntry(entry);
		}
		showDay();
	}

	/**
	 * Lists the loaded workouts of the date shown in the calendar and keeps the selection if possible.
	 */
	private void showDay() {
		LocalDate date = calendarView.getDate();
		dayLabel.setText("Workouts on " + date);

		Workout previous = dayWorkoutList.getSelectionModel().getSelectedItem();
		List<Workout> ofDay = loaded.stream().filter(w -> w.date().equals(date)).toList();
		dayWorkoutList.getItems().setAll(ofDay);

		Optional<Workout> same = ofDay.stream()
				.filter(w -> previous != null && w.id().equals(previous.id()))
				.findFirst();
		if (same.isPresent()) {
			dayWorkoutList.getSelectionModel().select(same.get());
		} else if (!ofDay.isEmpty()) {
			dayWorkoutList.getSelectionModel().selectFirst();
		} else {
			showDetails(null);
		}
	}

	private void showDetails(Workout workout) {
		detailsArea.setText(workout == null ? "" : WorkoutFormatter.details(workout));
		deleteButton.setDisable(workout == null);
	}
}
