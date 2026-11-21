package org.example.schedstatapp.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.schedstatapp.controller.Refreshable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {
	private static final ViewManager instance = new ViewManager();

	private final Map<String, ViewData> cachedViews = new HashMap<>();

	private Stage mainStage;

	private static class ViewData {
		Parent root;
		Object controller;

		ViewData(Parent root, Object controller) {
			this.root = root;
			this.controller = controller;
		}
	}

	private ViewManager() {}

	public static ViewManager getInstance() {
		return instance;
	}

	public void setMainStage(Stage stage) {
		this.mainStage = stage;
	}

	public void switchView(String fxmlFileName) {
		try {
			ViewData viewData;

			if (cachedViews.containsKey(fxmlFileName)) {
				viewData = cachedViews.get(fxmlFileName);
			} else {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
				Parent root = loader.load();
				Object controller = loader.getController();

				viewData = new ViewData(root, controller);
				cachedViews.put(fxmlFileName, viewData);
			}

			mainStage.getScene().setRoot(viewData.root);

			if (viewData.controller instanceof Refreshable) {
				((Refreshable) viewData.controller).updateUI();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Не удалось загрузить экран: " + fxmlFileName);
		}
	}
}