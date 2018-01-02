package it.pjsoft.reactive.mailer.internal;



import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class MailSenderAuthenticator extends Authenticator {
	String username = "";
	String password = "";
	Logger logger = Logger.getLogger("MailSenderAuthenticator");
	
	public MailSenderAuthenticator(String username, String password){
		this.username = username;
		this.password = password;
			
	}
	
	public PasswordAuthentication getPasswordAuthentication(){
		logger.log(Level.FINEST, "method getPasswordAuthentication()");
		logger.log(Level.FINEST, "username  "+this.username+"  password  "+this.password);
		return new PasswordAuthentication(username, password);
	}

}
