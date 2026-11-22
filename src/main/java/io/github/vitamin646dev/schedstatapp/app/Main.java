package io.github.vitamin646dev.schedstatapp.app;

import io.github.vitamin646dev.schedstatapp.util.ViewType;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

	private AppContext context;

	@Override
	public void start(Stage stage) {
		context = new AppContext();
		ViewManager viewManager = context.viewManager();

		Scene scene = new Scene(new StackPane(), 800, 600);
		stage.setScene(scene);
		viewManager.setMainStage(stage);
		viewManager.switchView(ViewType.HOME);

		stage.show();
	}

	@Override
	public void stop() {
		if (context != null) {
			context.close();
		}
	}

	public static void main(String[] args) {
		launch();
	}
}
