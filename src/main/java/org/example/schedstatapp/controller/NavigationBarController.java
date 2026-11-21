package org.example.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import org.example.schedstatapp.app.ViewManager;
import org.example.schedstatapp.util.ViewType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Controller of the shared navigation bar that is shown above every view.
 * Creates one button per {@link ViewType} and disables the button of the view
 * that is currently shown, so adding a new view only requires a new enum constant.
 */
public class NavigationBarController {
	@FXML
	private HBox navigationBar;

	private final Map<ViewType, Button> buttons = new EnumMap<>(ViewType.class);

	@FXML
	public void initialize() {
		ViewManager viewManager = ViewManager.getInstance();

		for (ViewType viewType : ViewType.values()) {
			Button button = new Button(viewType.getTitle());
			button.setOnAction(event -> viewManager.switchView(viewType));
			buttons.put(viewType, button);
			navigationBar.getChildren().add(button);
		}

		viewManager.currentViewProperty().addListener((observable, oldView, newView) -> markActive(newView));
		markActive(viewManager.currentViewProperty().get());
	}

	private void markActive(ViewType activeView) {
		buttons.forEach((viewType, button) -> button.setDisable(viewType == activeView));
	}
}
