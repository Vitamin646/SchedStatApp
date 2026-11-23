package io.github.vitamin646dev.schedstatapp.app;

import io.github.vitamin646dev.schedstatapp.controller.CalendarController;
import io.github.vitamin646dev.schedstatapp.controller.ExercisesController;
import io.github.vitamin646dev.schedstatapp.controller.HomeController;
import io.github.vitamin646dev.schedstatapp.controller.LogWorkoutController;
import io.github.vitamin646dev.schedstatapp.controller.NavigationBarController;
import io.github.vitamin646dev.schedstatapp.controller.SettingsController;
import io.github.vitamin646dev.schedstatapp.db.Database;
import io.github.vitamin646dev.schedstatapp.repository.ExerciseRepository;
import io.github.vitamin646dev.schedstatapp.repository.WorkoutRepository;
import io.github.vitamin646dev.schedstatapp.repository.sqlite.SqliteExerciseRepository;
import io.github.vitamin646dev.schedstatapp.repository.sqlite.SqliteWorkoutRepository;
import io.github.vitamin646dev.schedstatapp.service.BackgroundExecutor;
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
public class AppContext implements AutoCloseable {

	private final ViewManager viewManager = new ViewManager();
	private final AppSettings settings = new AppSettings();
	private final NotificationService notifications = new ConsoleNotificationService();
	private final Database database = Database.open(Database.defaultFile());
	private final ExerciseRepository exerciseRepository = new SqliteExerciseRepository(database);
	private final WorkoutRepository workoutRepository = new SqliteWorkoutRepository(database);
	private final BackgroundExecutor backgroundExecutor = new BackgroundExecutor();
	private final Map<Class<?>, Supplier<Object>> controllers = new HashMap<>();

	public AppContext() {
		controllers.put(NavigationBarController.class, () -> new NavigationBarController(viewManager));
		controllers.put(HomeController.class, () -> new HomeController(settings, workoutRepository, backgroundExecutor));
		controllers.put(CalendarController.class,
				() -> new CalendarController(workoutRepository, backgroundExecutor, notifications));
		controllers.put(LogWorkoutController.class, () -> new LogWorkoutController(
				exerciseRepository, workoutRepository, backgroundExecutor, notifications));
		controllers.put(ExercisesController.class,
				() -> new ExercisesController(exerciseRepository, backgroundExecutor, notifications));
		controllers.put(SettingsController.class, () -> new SettingsController(settings, notifications));
		viewManager.setControllerFactory(controllerFactory());
	}

	public ViewManager viewManager() {
		return viewManager;
	}

	public ExerciseRepository exerciseRepository() {
		return exerciseRepository;
	}

	public WorkoutRepository workoutRepository() {
		return workoutRepository;
	}

	public BackgroundExecutor backgroundExecutor() {
		return backgroundExecutor;
	}

	@Override
	public void close() {
		backgroundExecutor.close();
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
