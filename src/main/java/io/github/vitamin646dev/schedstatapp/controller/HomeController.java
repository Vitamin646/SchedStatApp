package io.github.vitamin646dev.schedstatapp.controller;

import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.repository.WorkoutRepository;
import io.github.vitamin646dev.schedstatapp.service.BackgroundExecutor;
import io.github.vitamin646dev.schedstatapp.service.WeekSummary;
import io.github.vitamin646dev.schedstatapp.service.WorkoutFormatter;
import io.github.vitamin646dev.schedstatapp.util.AppSettings;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class HomeController extends BaseController {
	private final AppSettings settings;
	private final WorkoutRepository workouts;
	private final BackgroundExecutor background;

	private int latestRequest;

	@FXML
	private Label welcomeText;
	@FXML
	private Label todayTitle;
	@FXML
	private Label todayText;
	@FXML
	private Label weekTitle;
	@FXML
	private Label weekText;

	public HomeController(AppSettings settings, WorkoutRepository workouts, BackgroundExecutor background) {
		this.settings = settings;
		this.workouts = workouts;
		this.background = background;
	}

	@FXML
	public void initialize() { }

	@Override
	public void updateUI() {
		boolean showWelcome = settings.isShowWelcomeText();

		welcomeText.setVisible(showWelcome);
		welcomeText.setManaged(showWelcome);

		if (showWelcome) {
			welcomeText.setText("Welcome to JavaFX!");
		}

		loadWeek();
	}

	private void loadWeek() {
		LocalDate today = LocalDate.now();
		LocalDate weekStart = WeekSummary.weekStart(today);
		int request = ++latestRequest;

		todayTitle.setText("Today, " + today);
		weekTitle.setText("This week, " + weekStart + " to " + weekStart.plusDays(6));

		background.supply(() -> workouts.findByDateRange(weekStart, weekStart.plusDays(6)))
				.whenComplete((list, error) -> Platform.runLater(() -> {
					if (request != latestRequest) {
						return;
					}
					if (error != null) {
						todayText.setText("Could not load workouts: " + error.getMessage());
						weekText.setText("");
						return;
					}
					showWeek(today, list);
				}));
	}

	private void showWeek(LocalDate today, List<Workout> week) {
		List<Workout> todays = week.stream().filter(w -> w.date().equals(today)).toList();
		todayText.setText(todays.isEmpty()
				? "No workouts today."
				: todays.stream()
						.map(w -> w.status() + ": " + WorkoutFormatter.title(w))
						.collect(Collectors.joining("\n")));
		weekText.setText(WeekSummary.of(today, week).describe());
	}
}
