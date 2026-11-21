package org.example.schedstatapp.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import org.example.schedstatapp.controller.Refreshable;
import org.example.schedstatapp.util.ViewType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {
	private static final ViewManager instance = new ViewManager();

	private final Map<ViewType, ViewData> cachedViews = new HashMap<>();

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

	public void switchView(ViewType viewType) {
		try {
			ViewData viewData;

			if (cachedViews.containsKey(viewType)) {
				viewData = cachedViews.get(viewType);
			} else {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(viewType.getFxmlPath()));
				Parent root = loader.load();
				Object controller = loader.getController();

				if (!viewType.getControllerClass().isInstance(controller)) {
					throw new RuntimeException(
							"Ошибка: FXML " + viewType.getFxmlPath() +
							" привязан к " + controller.getClass().getSimpleName() +
							", но ожидался " + viewType.getControllerClass().getSimpleName()
					);
				}

				viewData = new ViewData(root, controller);
				cachedViews.put(viewType, viewData);
			}

			mainStage.getScene().setRoot(viewData.root);
			mainStage.setTitle(viewType.getTitle());

			if (viewData.controller instanceof Refreshable) {
				((Refreshable) viewData.controller).updateUI();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Не удалось загрузить экран: " + viewType.getFxmlPath());
		}
	}
}