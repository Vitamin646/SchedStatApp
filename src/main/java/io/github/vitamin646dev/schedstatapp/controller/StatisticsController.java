package io.github.vitamin646dev.schedstatapp.controller;

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
		// Fill the chart with sample data (analysis of data from the database will go here in the future)
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Workouts");
		series.getData().add(new XYChart.Data<>("Mon", 45));
		series.getData().add(new XYChart.Data<>("Wed", 60));
		series.getData().add(new XYChart.Data<>("Fri", 30));

		workoutChart.getData().add(series);
	}
}
