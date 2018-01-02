package it.pjsoft.reactive.mailer.api;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;

public class MailPart {
	MimeBodyPart part;

	public MailPart(DataSource source, String fileName) throws MessagingException {
		part = new MimeBodyPart();
		part.setDataHandler(new DataHandler(source));
		if (fileName != null)
			part.setFileName(fileName);
	}

	public MailPart(final InputStream is, final String fileName, final String mime) throws MessagingException, IOException {
		part = new MimeBodyPart();
		part.setDataHandler(new DataHandler(
				new InputStreamDataSource(fileName, mime, is)));
		if (fileName != null)
			part.setFileName(fileName);
	}

	public MailPart(File f) throws MessagingException {
		part = new MimeBodyPart();
		DataSource source = new FileDataSource(f);
		part.setDataHandler(new DataHandler(source));
		part.setFileName(f.getName());
	}

	public MailPart(String txt, String fileName, String mimeType) throws MessagingException {
		part = new MimeBodyPart();
		part.setContent(txt, mimeType);
		if (fileName != null)
			part.setFileName(fileName);
	}

	public MailPart(MimeBodyPart mbp) throws MessagingException {
		part = mbp;
	}

	public static MailPart[] mkInlineImage(InputStream is, String mimeType, String id) throws MessagingException, IOException{
		MailPart[] ret=new MailPart[2];			
		ret[0]=new MailPart("<img src=\"cid:"+id+"\">", null, "text/html");
		ret[1]=new MailPart(is, null, mimeType);
		ret[1].part.setHeader("Content-ID", "<"+id+">");
		return ret;
	}
	
	public BodyPart getPart() {
		return part;
	}
}