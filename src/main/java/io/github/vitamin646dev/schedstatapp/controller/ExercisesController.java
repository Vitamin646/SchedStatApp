package io.github.vitamin646dev.schedstatapp.controller;

import io.github.vitamin646dev.schedstatapp.model.Exercise;
import io.github.vitamin646dev.schedstatapp.repository.ExerciseRepository;
import io.github.vitamin646dev.schedstatapp.service.BackgroundExecutor;
import io.github.vitamin646dev.schedstatapp.service.NotificationService;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

/**
 * Exercise catalog: lists exercises and lets the user add or delete them.
 * Database calls run on the background worker; results are applied on the JavaFX thread.
 */
public class ExercisesController extends BaseController {
	private final ExerciseRepository exercises;
	private final BackgroundExecutor background;
	private final NotificationService notifications;

	@FXML
	private TableView<Exercise> exerciseTable;
	@FXML
	private TableColumn<Exercise, String> nameColumn;
	@FXML
	private TableColumn<Exercise, String> muscleGroupColumn;
	@FXML
	private TextField nameField;
	@FXML
	private TextField muscleGroupField;
	@FXML
	private Label statusLabel;

	public ExercisesController(ExerciseRepository exercises, BackgroundExecutor background,
							   NotificationService notifications) {
		this.exercises = exercises;
		this.background = background;
		this.notifications = notifications;
	}

	@FXML
	public void initialize() {
		nameColumn.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().name()));
		muscleGroupColumn.setCellValueFactory(cell -> new SimpleStringProperty(
				cell.getValue().muscleGroup() == null ? "" : cell.getValue().muscleGroup()));
	}

	@Override
	public void updateUI() {
		reload();
	}

	@FXML
	protected void onAddButtonClick() {
		String name = nameField.getText() == null ? "" : nameField.getText().strip();
		if (name.isEmpty()) {
			report("Enter an exercise name");
			return;
		}
		String muscleGroup = muscleGroupField.getText() == null ? "" : muscleGroupField.getText().strip();

		Exercise exercise = Exercise.of(name, muscleGroup.isEmpty() ? null : muscleGroup);
		background.run(() -> exercises.save(exercise)).whenComplete((ignored, error) -> Platform.runLater(() -> {
			if (error != null) {
				report("Cannot add '" + name + "' (does it already exist?)");
				return;
			}
			statusLabel.setText("");
			nameField.clear();
			muscleGroupField.clear();
			reload();
		}));
	}

	@FXML
	protected void onDeleteButtonClick() {
		Exercise selected = exerciseTable.getSelectionModel().getSelectedItem();
		if (selected == null) {
			report("Select an exercise to delete");
			return;
		}

		background.run(() -> exercises.delete(selected.id())).whenComplete((ignored, error) -> Platform.runLater(() -> {
			if (error != null) {
				report("Cannot delete '" + selected.name() + "' (is it used in a workout?)");
			} else {
				statusLabel.setText("");
			}
			reload();
		}));
	}

	private void reload() {
		background.supply(exercises::findAll).whenComplete((list, error) -> Platform.runLater(() -> {
			if (error != null) {
				report("Cannot load exercises");
				return;
			}
			showExercises(list);
		}));
	}

	/**
	 * Shows a message under the table and sends it to the notification service.
	 */
	private void report(String message) {
		statusLabel.setText(message);
		notifications.show(message);
	}

	private void showExercises(List<Exercise> list) {
		exerciseTable.getItems().setAll(list);
	}
}
