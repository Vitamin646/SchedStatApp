package io.github.vitamin646dev.schedstatapp.controller;

/**
 * Base class for all controllers in the application.
 * Implements Refreshable so that ViewManager can refresh views.
 */
public abstract class BaseController implements Refreshable {
	@Override
	public void updateUI() {}
}
