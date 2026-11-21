package org.example.schedstatapp.util;

import org.example.schedstatapp.controller.*;

public enum ViewType {
	HOME(PathBuilder.buildPath("home-view"), HomeController.class, "Home"),
	CALENDAR(PathBuilder.buildPath("calendar-view"), CalendarController.class, "Calendar"),
	STATISTICS(PathBuilder.buildPath("statistics-view"), StatisticsController.class, "Statistics"),
	SETTINGS(PathBuilder.buildPath("settings-view"), SettingsController.class, "Settings");

	private final String fxmlPath;
	private final Class<?> controllerClass;
	private final String title;

	ViewType(String fxmlPath, Class<?> controllerClass, String title) {
		this.fxmlPath = fxmlPath;
		this.controllerClass = controllerClass;
		this.title = title;
	}

	public String getFxmlPath() { return fxmlPath; }
	public Class<?> getControllerClass() { return controllerClass; }
	public String getTitle() { return title; }

	private static class PathBuilder {
		private static final String BASE = "/org/example/schedstatapp/fxml/";
		private static final String DOT_FXML = ".fxml";

		private static String buildPath(String filename) {
			return BASE + filename + DOT_FXML;
		}
	}
}
