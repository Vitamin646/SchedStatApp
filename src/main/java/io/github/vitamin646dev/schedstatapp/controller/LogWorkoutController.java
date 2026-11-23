package io.github.vitamin646dev.schedstatapp.controller;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.model.Workout;
import io.github.vitamin646dev.schedstatapp.model.WorkoutStatus;
import io.github.vitamin646dev.schedstatapp.repository.ExerciseRepository;
import io.github.vitamin646dev.schedstatapp.repository.WorkoutRepository;
import io.github.vitamin646dev.schedstatapp.service.BackgroundExecutor;
import io.github.vitamin646dev.schedstatapp.service.NotificationService;
import io.github.vitamin646dev.schedstatapp.service.WorkoutDraft;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.util.List;

/**
 * Entry form for a new workout: pick date and status, add sets one by one, save.
 * Saving goes through the background worker; results are applied on the JavaFX thread.
 */
public class LogWorkoutController extends BaseController {
	private static final String ERROR_STYLE = "-fx-text-fill: #b00020;";
	private static final String SUCCESS_STYLE = "-fx-text-fill: #2e7d32;";

	private final ExerciseRepository exercises;
	private final WorkoutRepository workouts;
	private final BackgroundExecutor background;
	private final NotificationService notifications;
	private final WorkoutDraft draft = new WorkoutDraft();

	@FXML
	private DatePicker datePicker;
	@FXML
	private ComboBox<WorkoutStatus> statusBox;
	@FXML
	private TextField tagsField;
	@FXML
	private TextField notesField;
	@FXML
	private ComboBox<Exercise> exerciseBox;
	@FXML
	private TextField repsField;
	@FXML
	private TextField weightField;
	@FXML
	private TextField rpeField;
	@FXML
	private TableView<WorkoutDraft.Entry> setsTable;
	@FXML
	private TableColumn<WorkoutDraft.Entry, String> exerciseColumn;
	@FXML
	private TableColumn<WorkoutDraft.Entry, String> repsColumn;
	@FXML
	private TableColumn<WorkoutDraft.Entry, String> weightColumn;
	@FXML
	private TableColumn<WorkoutDraft.Entry, String> rpeColumn;
	@FXML
	private Label statusLabel;

	public LogWorkoutController(ExerciseRepository exercises, WorkoutRepository workouts,
								BackgroundExecutor background, NotificationService notifications) {
		this.exercises = exercises;
		this.workouts = workouts;
		this.background = background;
		this.notifications = notifications;
	}

	@FXML
	public void initialize() {
		datePicker.setValue(LocalDate.now());
		statusBox.getItems().setAll(WorkoutStatus.values());
		statusBox.setValue(WorkoutStatus.DONE);

		exerciseBox.setConverter(new StringConverter<>() {
			@Override
			public String toString(Exercise exercise) {
				return exercise == null ? "" : exercise.name();
			}

			@Override
			public Exercise fromString(String text) {
				return null;
			}
		});

		exerciseColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().exercise().name()));
		repsColumn.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().set().reps())));
		weightColumn.setCellValueFactory(cell -> new SimpleStringProperty(
				formatNumber(cell.getValue().set().weightKg())));
		rpeColumn.setCellValueFactory(cell -> new SimpleStringProperty(
				cell.getValue().set().rpe() == null ? "" : formatNumber(cell.getValue().set().rpe())));
	}

	@Override
	public void updateUI() {
		Exercise selected = exerciseBox.getValue();
		background.supply(exercises::findAll).whenComplete((list, error) -> Platform.runLater(() -> {
			if (error != null) {
				showError("Cannot load exercises");
				return;
			}
			showExercises(list, selected);
		}));
	}

	@FXML
	protected void onAddSetButtonClick() {
		Exercise exercise = exerciseBox.getValue();
		if (exercise == null) {
			showError("Choose an exercise");
			return;
		}
		try {
			draft.addSet(exercise, WorkoutDraft.parseSet(repsField.getText(), weightField.getText(), rpeField.getText()));
		} catch (IllegalArgumentException e) {
			showError(e.getMessage());
			return;
		}
		statusLabel.setText("");
		refreshTable();
		// Keep exercise, weight and reps: the next set is usually similar.
		repsField.requestFocus();
	}

	@FXML
	protected void onRemoveSetButtonClick() {
		int index = setsTable.getSelectionModel().getSelectedIndex();
		if (index < 0) {
			showError("Select a set to remove");
			return;
		}
		draft.removeEntry(index);
		statusLabel.setText("");
		refreshTable();
	}

	@FXML
	protected void onSaveButtonClick() {
		LocalDate date = datePicker.getValue();
		WorkoutStatus status = statusBox.getValue();
		if (date == null || status == null) {
			showError("Choose a date and a status");
			return;
		}
		if (draft.isEmpty()) {
			showError("Add at least one set");
			return;
		}

		Workout workout = draft.toWorkout(date, status, notesField.getText(), tagsField.getText());
		background.supply(() -> workouts.save(workout)).whenComplete((saved, error) -> Platform.runLater(() -> {
			if (error != null) {
				showError("Cannot save the workout");
				return;
			}
			draft.clear();
			notesField.clear();
			tagsField.clear();
			refreshTable();
			showSuccess("Workout saved: " + saved.date() + ", " + saved.exercises().size() + " exercise(s)");
		}));
	}

	private void showExercises(List<Exercise> list, Exercise previouslySelected) {
		exerciseBox.getItems().setAll(list);
		if (previouslySelected != null) {
			list.stream()
					.filter(e -> e.id().equals(previouslySelected.id()))
					.findFirst()
					.ifPresent(exerciseBox::setValue);
		}
	}

	private void refreshTable() {
		setsTable.getItems().setAll(draft.entries());
	}

	private void showError(String message) {
		statusLabel.setStyle(ERROR_STYLE);
		statusLabel.setText(message);
		notifications.show(message);
	}

	private void showSuccess(String message) {
		statusLabel.setStyle(SUCCESS_STYLE);
		statusLabel.setText(message);
		notifications.show(message);
	}

	private static String formatNumber(double value) {
		return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
	}
}
