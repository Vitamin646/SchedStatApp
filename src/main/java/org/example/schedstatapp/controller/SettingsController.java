package org.example.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import org.example.schedstatapp.app.ViewManager;
import org.example.schedstatapp.util.AppSettings;
import org.example.schedstatapp.util.ViewType;

public class SettingsController extends BaseController {
	@FXML
	private Label label1;

	@FXML
	private Label label2;

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
		ViewManager.getInstance().switchView(ViewType.HOME);
	}
}
