package io.github.vitamin646dev.schedstatapp.util;

import java.util.prefs.Preferences;

public class AppSettings {
	private static final Preferences prefs = Preferences.userRoot().node("io/github/vitamin646dev/schedstatapp");

	private static final String SHOW_WELCOME_KEY = "showWelcomeText";

	public static boolean isShowWelcomeText() {
		return prefs.getBoolean(SHOW_WELCOME_KEY, true);
	}

	public static void setShowWelcomeText(boolean value) {
		prefs.putBoolean(SHOW_WELCOME_KEY, value);
	}
}