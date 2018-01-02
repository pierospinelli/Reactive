package it.pjsoft.reactive.mailer.internal;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.osgi.service.prefs.Preferences;

import it.pjsoft.reactive.mailer.api.MailPart;

//import org.apache.log4j.Logger;

public class MailSender {
	private Logger logger = Logger.getLogger("MailSender");

	protected String host;
	protected String port;
	protected String from;
	protected String username;
	protected String password;
	protected boolean ssl = false;;
	Session mailSession;
	Transport transport;

	public MailSender() {

	}

	public MailSender(Properties props) {
		host = props.getProperty("pol.mail.smtp.host");
		port = props.getProperty("pol.mail.smtp.port");
		from = props.getProperty("pol.mail.from");
		username = props.getProperty("pol.mail.username");
		password = props.getProperty("pol.mail.password");
		ssl = "TRUE".equalsIgnoreCase(props.getProperty("pol.mail.ssl"));
	}

	public MailSender(Preferences node) {
		host = node.get("host", null);
		port = node.get("port", "25");
		from = node.get("from", null);
		username = node.get("user", null);
		password = node.get("password", null);
		ssl = node.getBoolean("SSL", false);
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	protected Session getSession() {
		Properties mailProps = new Properties();
		mailProps.put("mail.smtp.host", host);
		mailProps.put("mail.smtp.port", port);
		mailProps.put("mail.smtp.auth", "true");
		mailProps.put("mail.smtp.connectiontimeout", "10000");
		mailProps.put("mail.smtp.timeout", "10000");
		Authenticator auth = new MailSenderAuthenticator(username, password);

		if (ssl) {
//			mailProps.put("mail.transport.protocol", "smtps");
//			mailProps.put("mail.smtp.starttls.enable", "true");
			mailProps.put("mail.smtp.socketFactory.port", port);
			mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

//			mailProps.put("mail.smtp.ssl.checkserveridentity", "false");
//			mailProps.put("mail.smtp.ssl.trust", "*");

			mailProps.put("mail.smtp.debug", "true");
//			mailProps.put("mail.smtp.socketFactory.fallback", "false");

		}

		if (System.getSecurityManager() == null)
			return Session.getInstance(mailProps, auth);
		else
			return Session.getDefaultInstance(mailProps, auth);
	}

	public void sendPlainText(String txt, String subject, String encoding, String from, String[] addrsTO,
			String[] addrsCC, String[] addrsBCC) throws MessagingException {
		MimeMessage msg = prepareSending(subject, from, addrsTO, addrsCC, addrsBCC);

		msg.setText(txt, encoding);

		// Transport.send(msg);
		transport.send(msg);
	}

	public void sendHtml(String txt, String subject, String encoding, String from, String[] addrsTO, String[] addrsCC,
			String[] addrsBCC) throws MessagingException {

		MimeMessage msg = prepareSending(subject, from, addrsTO, addrsCC, addrsBCC);

		msg.setContent(txt, "text/html");

		// Transport.send(msg);
		transport.send(msg);
	}

	public void sendMultiPart(MailPart[] parts, String subject, String from, String[] addrsTO, String[] addrsCC,
			String[] addrsBCC) throws MessagingException {

		MimeMessage msg = prepareSending(subject, from, addrsTO, addrsCC, addrsBCC);

		MimeMultipart multipart = new MimeMultipart("related");

		for (int i = 0; i < parts.length; i++) {
			BodyPart bp = parts[i].getPart();
			multipart.addBodyPart(bp);
		}

		msg.setContent(multipart);

//		 Transport.send(msg);
		transport.send(msg);
	}

	private MimeMessage prepareSending(String subject, String from, String[] addrsTO, String[] addrsCC,
			String[] addrsBCC) throws AddressException, MessagingException {

		// logger.log(Level.FINEST, "Getting Mail Session and Transport...");
		// Session mailSession = getSession();
		// // Transport transport = mailSession.getTransport();
		//// mailSession.setDebug(true);

		logger.log(Level.FINEST, "Getting Mail Session and Transport...");
		mailSession = getSession();
		mailSession.setDebug(true);
		if (ssl) {
			transport = mailSession.getTransport("smtps");
		} else {
			transport = mailSession.getTransport("smtp");
		}

		logger.log(Level.FINEST, "Got Mail Session and Transport: OK");

		if (from == null)
			from = getFrom();

		InternetAddress[] iasTO = toIA(addrsTO);
		InternetAddress[] iasCC = toIA(addrsCC);
		InternetAddress[] iasBCC = toIA(addrsBCC);

		MimeMessage msg = new MimeMessage(mailSession);

		msg.setFrom(new InternetAddress(from));
		if (iasTO != null)
			msg.setRecipients(Message.RecipientType.TO, iasTO);
		if (iasCC != null)
			msg.setRecipients(Message.RecipientType.CC, iasCC);
		if (iasBCC != null)
			msg.setRecipients(Message.RecipientType.BCC, iasBCC);
		msg.setSubject(subject);
		msg.setSentDate(new java.util.Date());
		return msg;
	}

	private InternetAddress[] toIA(String[] ins) throws AddressException {
		if (ins == null || ins.length == 0)
			return null;
		InternetAddress[] ret = new InternetAddress[ins.length];
		for (int i = 0; i < ret.length; i++)
			ret[i] = new InternetAddress(ins[i]);
		return ret;
	}

}
