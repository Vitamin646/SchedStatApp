package org.example.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import org.example.schedstatapp.util.AppSettings;

public class SettingsController extends BaseController {
	@FXML
	private CheckBox welcomeCheckBox;

	@FXML
	public void initialize() {
		boolean showWelcome = AppSettings.isShowWelcomeText();
		welcomeCheckBox.setSelected(showWelcome);
	}

	@FXML
	protected void onSaveButtonClick() {
		boolean newValue = welcomeCheckBox.isSelected();
		AppSettings.setShowWelcomeText(newValue);

		System.out.println("Settings saved: " + newValue);
	}
}
