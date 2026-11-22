package io.github.vitamin646dev.schedstatapp.service;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BackgroundExecutorTest {

	@Test
	void tasksRunOffTheCallingThread() throws Exception {
		try (BackgroundExecutor executor = new BackgroundExecutor()) {
			String worker = executor.supply(() -> Thread.currentThread().getName()).get(5, TimeUnit.SECONDS);

			assertEquals("background-worker", worker);
			assertNotEquals(Thread.currentThread().getName(), worker);
		}
	}

	@Test
	void checkedExceptionsArriveAsFailedFutures() {
		try (BackgroundExecutor executor = new BackgroundExecutor()) {
			ExecutionException e = assertThrows(ExecutionException.class,
					() -> executor.supply(() -> {
						throw new IOException("boom");
					}).get(5, TimeUnit.SECONDS));

			assertInstanceOf(IOException.class, e.getCause());
		}
	}
}
