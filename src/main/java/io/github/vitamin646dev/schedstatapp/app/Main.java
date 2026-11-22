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
		ViewManager viewManager = new ViewManager();

		Callback<Class<?>, Object> controllerFactory = param -> {

			if (param == NavigationBarController.class) {
				return new NavigationBarController(viewManager);
			}

			try {
				return param.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create controller: " + param.getName(), e);
			}
		};

		viewManager.setControllerFactory(controllerFactory);

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
