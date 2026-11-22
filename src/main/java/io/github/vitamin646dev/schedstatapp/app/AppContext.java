package io.github.vitamin646dev.schedstatapp.app;

import io.github.vitamin646dev.schedstatapp.controller.HomeController;
import io.github.vitamin646dev.schedstatapp.controller.NavigationBarController;
import io.github.vitamin646dev.schedstatapp.controller.SettingsController;
import io.github.vitamin646dev.schedstatapp.service.ConsoleNotificationService;
import io.github.vitamin646dev.schedstatapp.service.NotificationService;
import io.github.vitamin646dev.schedstatapp.util.AppSettings;
import javafx.util.Callback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Composition root: the single place where application objects are created and wired together.
 */
public class AppContext {

	private final ViewManager viewManager = new ViewManager();
	private final AppSettings settings = new AppSettings();
	private final NotificationService notifications = new ConsoleNotificationService();
	private final Map<Class<?>, Supplier<Object>> controllers = new HashMap<>();

	public AppContext() {
		controllers.put(NavigationBarController.class, () -> new NavigationBarController(viewManager));
		controllers.put(HomeController.class, () -> new HomeController(settings));
		controllers.put(SettingsController.class, () -> new SettingsController(settings, notifications));
		viewManager.setControllerFactory(controllerFactory());
	}

	public ViewManager viewManager() {
		return viewManager;
	}

	/**
	 * Creates controllers for FXMLLoader: registered ones through their suppliers,
	 * any other through its public no-arg constructor.
	 */
	public Callback<Class<?>, Object> controllerFactory() {
		return type -> {
			Supplier<Object> supplier = controllers.get(type);
			if (supplier != null) {
				return supplier.get();
			}
			try {
				return type.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Failed to create controller: " + type.getName(), e);
			}
		};
	}
}
