package io.github.vitamin646dev.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import io.github.vitamin646dev.schedstatapp.util.AppSettings;

public class HomeController extends BaseController {
	private final AppSettings settings;

	@FXML
	private Label welcomeText;

	public HomeController(AppSettings settings) {
		this.settings = settings;
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
	}
}