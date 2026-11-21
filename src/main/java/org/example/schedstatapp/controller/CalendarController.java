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
				System.out.println("Выбрана дата в календаре: " + newDate);
				// Здесь в будущем вы будете загружать тренировки на этот день
			}
		});
	}
}
