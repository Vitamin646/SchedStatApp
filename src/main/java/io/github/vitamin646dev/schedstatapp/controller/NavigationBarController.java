package io.github.vitamin646dev.schedstatapp.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import io.github.vitamin646dev.schedstatapp.app.ViewManager;
import io.github.vitamin646dev.schedstatapp.util.ViewType;

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
	private final ViewManager viewManager;

	// ViewManager внедряется через конструктор (Dependency Injection)
	public NavigationBarController(ViewManager viewManager) {
		this.viewManager = viewManager;
	}

	@FXML
	public void initialize() {
		// Убрали ViewManager.getInstance()! Используем поле this.viewManager

		for (ViewType viewType : ViewType.values()) {
			// Проверяем флаг navigable
			if (!viewType.isNavigable()) {
				continue;
			}

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
