package io.github.vitamin646dev.schedstatapp.controller;

import io.github.vitamin646dev.schedstatapp.service.NotificationService;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import io.github.vitamin646dev.schedstatapp.util.AppSettings;

public class SettingsController extends BaseController {
	private final AppSettings settings;
	private final NotificationService notifications;

	@FXML
	private CheckBox welcomeCheckBox;

	public SettingsController(AppSettings settings, NotificationService notifications) {
		this.settings = settings;
		this.notifications = notifications;
	}

	@FXML
	public void initialize() {
		boolean showWelcome = settings.isShowWelcomeText();
		welcomeCheckBox.setSelected(showWelcome);
	}

	@FXML
	protected void onSaveButtonClick() {
		boolean newValue = welcomeCheckBox.isSelected();
		settings.setShowWelcomeText(newValue);

		notifications.show("Settings saved: " + newValue);
	}
}
