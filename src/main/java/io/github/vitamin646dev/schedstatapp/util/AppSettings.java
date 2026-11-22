package io.github.vitamin646dev.schedstatapp.util;

import java.util.prefs.Preferences;

public class AppSettings {
	private static final String SHOW_WELCOME_KEY = "showWelcomeText";

	private final Preferences prefs;

	public AppSettings() {
		this(Preferences.userRoot().node("io/github/vitamin646dev/schedstatapp"));
	}

	public AppSettings(Preferences prefs) {
		this.prefs = prefs;
	}

	public boolean isShowWelcomeText() {
		return prefs.getBoolean(SHOW_WELCOME_KEY, true);
	}

	public void setShowWelcomeText(boolean value) {
		prefs.putBoolean(SHOW_WELCOME_KEY, value);
	}
}
