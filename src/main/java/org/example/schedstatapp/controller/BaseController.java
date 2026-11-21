package org.example.schedstatapp.controller;

/**
 * Базовый класс для всех контроллеров в приложении.
 * Реализует Refreshable, чтобы ViewManager мог обновлять экраны.
 */
public abstract class BaseController implements Refreshable {
	@Override
	public void updateUI() {}

	protected void showNotification(String message) {
		System.out.println("Уведомление: " + message);
		// В будущем здесь можно использовать ControlsFX Notifications
	}
}
