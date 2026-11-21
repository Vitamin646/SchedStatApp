package org.example.schedstatapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SettingsController {
	@FXML
	private Label label1;

	@FXML
	private Label label2;

	@FXML
	protected void onToggle1ButtonClick() {
		label1.setText("mode12");
	}

	@FXML
	protected void onToggle2ButtonClick() {
		label2.setText("mode22");
	}

	@FXML
	protected void onHomeButtonClick() {
		ViewManager.getInstance().switchView("home-view.fxml");
	}
}
