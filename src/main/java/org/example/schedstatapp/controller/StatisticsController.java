package org.example.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

public class StatisticsController extends BaseController {
	@FXML
	public Label totalTimeLabel;

	@FXML
	private BarChart<String, Number> workoutChart;

	@FXML
	public void initialize() {
		// Заполняем график тестовыми данными (в будущем здесь будет анализ из базы)
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Тренировки");
		series.getData().add(new XYChart.Data<>("Пн", 45));
		series.getData().add(new XYChart.Data<>("Ср", 60));
		series.getData().add(new XYChart.Data<>("Пт", 30));

		workoutChart.getData().add(series);
	}
}
