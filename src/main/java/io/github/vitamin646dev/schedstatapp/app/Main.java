package io.github.vitamin646dev.schedstatapp.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import io.github.vitamin646dev.schedstatapp.util.ViewType;

public class Main extends Application {

	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new StackPane(), 800, 600);
		stage.setScene(scene);
		ViewManager.getInstance().setMainStage(stage);
		ViewManager.getInstance().switchView(ViewType.HOME);
		stage.show();
	}

	public static void main(String[] args) {
		launch();
	}
}
