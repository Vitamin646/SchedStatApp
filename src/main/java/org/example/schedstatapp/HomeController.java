package org.example.schedstatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HomeController {
	@FXML
	private Label welcomeText;

	@FXML
	private Label exampleLabel;

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
		ViewManager.getInstance().switchView("settings-view.fxml");
	}
}