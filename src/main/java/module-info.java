module io.github.vitamin646dev.schedstatapp {
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
	requires java.sql;
	requires org.flywaydb.core;


	exports io.github.vitamin646dev.schedstatapp.app;
	opens io.github.vitamin646dev.schedstatapp.app to javafx.fxml;

	exports io.github.vitamin646dev.schedstatapp.controller;
	opens io.github.vitamin646dev.schedstatapp.controller to javafx.fxml;

	exports io.github.vitamin646dev.schedstatapp.model;
	opens io.github.vitamin646dev.schedstatapp.model to javafx.fxml;

	exports io.github.vitamin646dev.schedstatapp.db;
	// Flyway finds migrations through the class loader, so the package must be open to everyone
	opens io.github.vitamin646dev.schedstatapp.db.migration;

	exports io.github.vitamin646dev.schedstatapp.service;

	exports io.github.vitamin646dev.schedstatapp.util;
	opens io.github.vitamin646dev.schedstatapp.util to javafx.fxml;
}