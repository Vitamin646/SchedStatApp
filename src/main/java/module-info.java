module org.example.schedstatapp {
	requires javafx.controls;
	requires javafx.fxml;
	requires javafx.web;

	requires org.controlsfx.controls;
	requires com.dlsc.formsfx;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.bootstrapfx.core;
	requires eu.hansolo.tilesfx;
	requires com.calendarfx.view;

	requires java.prefs;


	exports org.example.schedstatapp.app;
	opens org.example.schedstatapp.app to javafx.fxml;

	exports org.example.schedstatapp.controller;
	opens org.example.schedstatapp.controller to javafx.fxml;

	exports org.example.schedstatapp.util;
	opens org.example.schedstatapp.util to javafx.fxml;
}