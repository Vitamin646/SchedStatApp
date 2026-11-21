package io.github.vitamin646dev.schedstatapp.controller;

/**
 * Interface for controllers that must refresh their state (UI)
 * every time their view is shown to the user.
 */
public interface Refreshable {
	void updateUI();
}