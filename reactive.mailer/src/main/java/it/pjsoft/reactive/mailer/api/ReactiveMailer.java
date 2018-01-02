package it.pjsoft.reactive.mailer.api;

import org.osgi.service.prefs.Preferences;

public interface ReactiveMailer {

	ReactiveMail newMail(Preferences mailConf);
}