package it.pjsoft.reactive.mailer.internal;

import java.util.Date;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
    public void start(BundleContext context) {
    	Activator.context = context;
          System.out.println("Starting the bundle "+ context.getBundle().getSymbolicName() +" at "+new Date());
		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap(); 
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html"); 
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml"); 
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain"); 
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed"); 
		mc.addMailcap("message/rfc822;; x-java-content- handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc); 
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle "+ context.getBundle().getSymbolicName() +" at "+new Date());
        Activator.context = null;
    }

    public static BundleContext getContext() {
		return context;
	}
}