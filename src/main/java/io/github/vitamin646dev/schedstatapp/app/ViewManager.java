package io.github.vitamin646dev.schedstatapp.app;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import io.github.vitamin646dev.schedstatapp.controller.Refreshable;
import io.github.vitamin646dev.schedstatapp.util.ViewType;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

public class ViewManager {
	private static final String NAVIGATION_BAR_FXML = "/io/github/vitamin646dev/schedstatapp/fxml/navigation-bar.fxml";

	private static final ViewManager instance = new ViewManager();

	private final Map<ViewType, ViewData> cachedViews = new HashMap<>();
	private final ReadOnlyObjectWrapper<ViewType> currentView = new ReadOnlyObjectWrapper<>();

	private Stage mainStage;
	private BorderPane shell;

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

	/**
	 * Sets the main stage and installs the application shell as the root of its scene:
	 * the shared navigation bar on top and the current view in the center.
	 */
	public void setMainStage(Stage stage) {
		this.mainStage = stage;
		this.shell = createShell();
		stage.getScene().setRoot(shell);
	}

	/** The view that is currently shown, or {@code null} before the first switch. */
	public ReadOnlyObjectProperty<ViewType> currentViewProperty() {
		return currentView.getReadOnlyProperty();
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
							"Error: FXML " + viewType.getFxmlPath() +
							" is bound to " + controller.getClass().getSimpleName() +
							", but " + viewType.getControllerClass().getSimpleName() + " was expected"
					);
				}

				viewData = new ViewData(root, controller);
				cachedViews.put(viewType, viewData);
			}

			shell.setCenter(viewData.root);
			mainStage.setTitle(viewType.getTitle());
			currentView.set(viewType);

			if (viewData.controller instanceof Refreshable) {
				((Refreshable) viewData.controller).updateUI();
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to load view: " + viewType.getFxmlPath());
		}
	}

	private BorderPane createShell() {
		try {
			Parent navigationBar = new FXMLLoader(getClass().getResource(NAVIGATION_BAR_FXML)).load();
			BorderPane pane = new BorderPane();
			pane.setTop(navigationBar);
			return pane;
		} catch (IOException e) {
			throw new UncheckedIOException("Failed to load navigation bar: " + NAVIGATION_BAR_FXML, e);
		}
	}
}