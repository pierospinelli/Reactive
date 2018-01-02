package it.pjsoft.reactive.mailer.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.mail.MessagingException;

public interface ReactiveMail {

	void setOggetto(String oggetto);

	void setFrom(String oggetto);

	void addTesto(String fragment) throws MessagingException;

	void allega(MailPart mp);

	MailPart allega(InputStream is, String fileName, String mime) throws MessagingException, IOException;

	MailPart allega(String text, String fileName, String mime) throws MessagingException, IOException;

	MailPart allega(File file) throws MessagingException, IOException;

	MailPart[] addInlineImage(InputStream is, String mimeType, String id) throws MessagingException, IOException;

	void addTo(String addr);

	void addCC(String addr);

	void addBCC(String addr);

	void send() throws MessagingException;

}