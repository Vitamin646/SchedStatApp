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
	private Label exampleLabel;

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
	protected void onHelloButtonClick() {
		welcomeText.setText("Welcome to JavaFX Application!");
	}

	@FXML
	protected void onExampleButtonClick() {
		exampleLabel.setText("Example TEXT EA!!");
	}

	@FXML
	protected void onSettingsButtonClick() {
		ViewManager.getInstance().switchView(ViewType.SETTINGS);
	}
}