package io.github.vitamin646dev.schedstatapp.service;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Runs database (and other blocking) work off the JavaFX thread, one task at a time.
 * UI code must hop back with {@code Platform.runLater} before touching controls.
 */
public final class BackgroundExecutor implements AutoCloseable {

	private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable, "background-worker");
		thread.setDaemon(true);
		return thread;
	});

	public <T> CompletableFuture<T> supply(Callable<T> task) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return task.call();
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new CompletionException(e);
			}
		}, executor);
	}

	public CompletableFuture<Void> run(Runnable task) {
		return CompletableFuture.runAsync(task, executor);
	}

	@Override
	public void close() {
		executor.shutdown();
	}
}
