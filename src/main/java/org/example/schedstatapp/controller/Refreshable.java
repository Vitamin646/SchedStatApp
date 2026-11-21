package org.example.schedstatapp.controller;

/**
 * Интерфейс для контроллеров, которые должны обновлять
 * свое состояние (UI) каждый раз, когда экран показывается пользователю.
 */
public interface Refreshable {
	void updateUI();
}