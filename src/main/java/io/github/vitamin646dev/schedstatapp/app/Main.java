package io.github.vitamin646dev.schedstatapp.app;

import io.github.vitamin646dev.schedstatapp.controller.NavigationBarController;
import io.github.vitamin646dev.schedstatapp.util.ViewType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		// 1. Создаем ViewManager
		ViewManager viewManager = new ViewManager();

		// 2. Определяем фабрику контроллеров (внедряем зависимости)
		Callback<Class<?>, Object> controllerFactory = param -> {
			// Если JavaFX просит создать NavigationBarController — передаем ему ViewManager
			if (param == NavigationBarController.class) {
				return new NavigationBarController(viewManager);
			}
			// Если JavaFX просит другие контроллеры (Home, Settings и т.д.)
			// В будущем сюда можно добавить передачу WorkoutService и т.п.
			try {
				return param.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create controller: " + param.getName(), e);
			}
		};

		// 3. Сообщаем ViewManager, как создавать контроллеры
		viewManager.setControllerFactory(controllerFactory);

		// 4. Инициализируем сцену и показываем первый экран
		Scene scene = new Scene(new StackPane(), 800, 600);
		stage.setScene(scene);
		viewManager.setMainStage(stage);
		viewManager.switchView(ViewType.HOME);

		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
