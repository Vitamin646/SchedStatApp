package org.example.schedstatapp;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {
	private static final ViewManager instance = new ViewManager();

	// Здесь мы храним загруженные экраны. Ключ - имя файла, Значение - корневой узел (Parent)
	private final Map<String, Parent> cachedViews = new HashMap<>();

	// Сохраняем ссылку на главное окно
	private Stage mainStage;

	private ViewManager() {}

	public static ViewManager getInstance() {
		return instance;
	}

	// Вызывается один раз при старте приложения в Main.java
	public void setMainStage(Stage stage) {
		this.mainStage = stage;
	}

	public void switchView(String fxmlFileName) {
		try {
			Parent view;

			if (cachedViews.containsKey(fxmlFileName)) {
				view = cachedViews.get(fxmlFileName);
			} else {
				FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFileName));
				view = loader.load();
				cachedViews.put(fxmlFileName, view);
			}

			mainStage.getScene().setRoot(view);

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Не удалось загрузить экран: " + fxmlFileName);
		}
	}
}