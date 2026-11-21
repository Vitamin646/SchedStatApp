package org.example.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.example.schedstatapp.app.ViewManager;
import org.example.schedstatapp.util.AppSettings;
import org.example.schedstatapp.util.ViewType;

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
			welcomeText.setText("Добро пожаловать в JavaFX!");
		}
	}

	@FXML
	protected void onSettingsButtonClick() {
		ViewManager.getInstance().switchView(ViewType.SETTINGS);
	}

	@FXML
	public void onCalendarButtonClick() {
		ViewManager.getInstance().switchView(ViewType.CALENDAR);
	}
}