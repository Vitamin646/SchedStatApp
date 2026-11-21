package org.example.schedstatapp.controller;

import javafx.fxml.FXML;
import com.calendarfx.view.CalendarView;

public class CalendarController extends BaseController {

	@FXML
	private CalendarView calendarView;

	@FXML
	public void initialize() {
		calendarView.dateProperty().addListener((observable, oldDate, newDate) -> {
			if (newDate != null) {
				System.out.println("Date selected in calendar: " + newDate);
				// Workouts for this date will be loaded here in the future
			}
		});
	}
}
