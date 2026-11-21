package io.github.vitamin646dev.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import io.github.vitamin646dev.schedstatapp.util.AppSettings;

public class HomeController extends BaseController {
	@FXML
	private Label welcomeText;

	@FXML
	public void initialize() {
		updateUI();
	}

	@Override
	public void updateUI() {
		boolean showWelcome = AppSettings.isShowWelcomeText();

		welcomeText.setVisible(showWelcome);
		welcomeText.setManaged(showWelcome);

		if (showWelcome) {
			welcomeText.setText("Welcome to JavaFX!");
		}
	}
}