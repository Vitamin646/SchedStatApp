package org.example.schedstatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class HelloController {
	@FXML
	private Label welcomeText;

	@FXML
	private Label exampleButton;

	@FXML
	protected void onHelloButtonClick() {
		welcomeText.setText("Welcome to JavaFX Application!");
	}

	@FXML
	protected void onExampleButtonClick() {
		exampleButton.setText("Example TEXT EA!!");
	}
}