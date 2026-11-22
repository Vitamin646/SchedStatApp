package io.github.vitamin646dev.schedstatapp.util;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppSettingsTest {

	private Preferences node;
	private AppSettings settings;

	@BeforeEach
	void setUp() {
		node = Preferences.userRoot().node("io/github/vitamin646dev/schedstatapp-test");
		settings = new AppSettings(node);
	}

	@AfterEach
	void tearDown() throws BackingStoreException {
		node.removeNode();
	}

	@Test
	void showWelcomeTextDefaultsToTrue() {
		assertTrue(settings.isShowWelcomeText());
	}

	@Test
	void showWelcomeTextIsPersisted() {
		settings.setShowWelcomeText(false);

		assertFalse(settings.isShowWelcomeText());
		assertFalse(new AppSettings(node).isShowWelcomeText());
	}
}
