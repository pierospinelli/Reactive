package it.pjsoft.reactive.mailer.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import it.pjsoft.reactive.mailer.api.MailPart;
import it.pjsoft.reactive.mailer.api.ReactiveMail;
import it.pjsoft.reactive.mailer.internal.MailSender;

public class ReactiveMailImpl implements ReactiveMail {
	private MailSender sender;
	private String from;
	private String oggetto;
	private StringBuffer testo=new StringBuffer();
	private List<String> to = new ArrayList<String>();
	private List<String> cc = new ArrayList<String>();
	private List<String> bcc = new ArrayList<String>();
	private List<MailPart> parts = new ArrayList<MailPart>();

	ReactiveMailImpl(MailSender sender){
		this.sender =sender;
	}
	
	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#setOggetto(java.lang.String)
	 */
	@Override
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#setFrom(java.lang.String)
	 */
	@Override
	public void setFrom(String from) {
		this.from = from;
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#addTesto(java.lang.String)
	 */
	@Override
	public void addTesto(String fragment) throws MessagingException {
		testo.append(fragment);
	}

	
	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#allega(it.pjsoft.reactive.mailer.internal.MailSender.MailPart)
	 */
	@Override
	public void allega(MailPart mp) {
		parts.add(mp);
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#allega(java.io.InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public MailPart allega(InputStream is, String fileName, String mime) throws MessagingException, IOException {
		MailPart mp = new MailPart(is, fileName, mime);
		parts.add(mp);
		return mp;
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#allega(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public MailPart allega(String text, String fileName, String mime) throws MessagingException, IOException {
		MailPart mp = new MailPart(text, fileName, mime);
		parts.add(mp);
		return mp;
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#allega(java.io.File)
	 */
	@Override
	public MailPart allega(File file) throws MessagingException, IOException {
		MailPart mp = new MailPart(file);
		parts.add(mp);
		return mp;
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#addInlineImage(java.io.InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public MailPart[] addInlineImage(InputStream is, String mimeType, String id) throws MessagingException, IOException {
		MailPart[] mp = MailPart.mkInlineImage(is, mimeType, id);
		parts.add(mp[0]);
		parts.add(mp[1]);
		return mp;
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#addTo(java.lang.String)
	 */
	@Override
	public void addTo(String addr) {
		to.add(addr);
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#addCC(java.lang.String)
	 */
	@Override
	public void addCC(String addr) {
		cc.add(addr);
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#addBCC(java.lang.String)
	 */
	@Override
	public void addBCC(String addr) {
		bcc.add(addr);
	}

	/* (non-Javadoc)
	 * @see it.pjsoft.reactive.mailer.impl.ReactiveMailer#sendMail()
	 */
	@Override
	public void send() throws MessagingException {
		ClassLoader cth = Thread.currentThread().getContextClassLoader();
		try {
			Thread.currentThread().setContextClassLoader(javax.mail.Message.class.getClassLoader() );
	
			MailPart txt = new MailPart(testo.toString(), null, "text/html");
			txt.getPart().setHeader("Content-Type", "text/html; charset=\"UTF-8\"");
			txt.getPart().setHeader("Content-Transfer-Encoding", "8bit");
			parts.add(0, txt);
			sender.sendMultiPart(parts.toArray(new MailPart[0]), oggetto, 
					from!=null ? from : sender.getFrom(), to.toArray(new String[0]),
					cc.toArray(new String[0]), bcc.toArray(new String[0]));
		}finally {
			Thread.currentThread().setContextClassLoader(cth);
		}
	}
	
}
