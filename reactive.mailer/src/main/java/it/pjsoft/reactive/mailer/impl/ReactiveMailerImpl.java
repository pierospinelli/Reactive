package it.pjsoft.reactive.mailer.impl;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.prefs.Preferences;

import it.pjsoft.reactive.mailer.api.ReactiveMail;
import it.pjsoft.reactive.mailer.api.ReactiveMailer;
//import it.pjsoft.reactive.mailer.internal.MailSender;
import it.pjsoft.reactive.mailer.internal.MailSender;

@Component(service=ReactiveMailer.class, immediate=true)
public class ReactiveMailerImpl implements ReactiveMailer {

	@Override
	public ReactiveMail newMail(Preferences mailConf) {
		MailSender sender = new MailSender(mailConf);
		return new ReactiveMailImpl(sender);
	}
	
	@Activate
	void activate(BundleContext context) throws Exception {
		System.out.println(getClass().getName()+ " service activated");
	}

}
