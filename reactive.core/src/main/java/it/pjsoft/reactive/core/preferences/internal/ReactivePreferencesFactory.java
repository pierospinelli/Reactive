package it.pjsoft.reactive.core.preferences.internal;

import org.osgi.service.prefs.Preferences;

public class ReactivePreferencesFactory implements PreferencesFactory {
	static ReactivePreferences system = new ReactivePreferences(null, "");
	static ReactivePreferences user = new ReactivePreferences(null, "");

	@Override
	public Preferences systemRoot() {
		return system;
	}

	@Override
	public Preferences userRoot() {
		return user;
	}

}