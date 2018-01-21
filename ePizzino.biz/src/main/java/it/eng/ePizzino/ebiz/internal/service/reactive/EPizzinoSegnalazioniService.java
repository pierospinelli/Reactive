package it.eng.ePizzino.ebiz.internal.service.reactive;

import java.sql.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.mail.MessagingException;

import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.prefs.Preferences;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;

import it.eng.ePizzino.ebiz.api.EPizzinoSegnalazioniDAO;
import it.eng.ePizzino.ebiz.internal.Activator;
import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.core.preferences.ReactiveConfig;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;
import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBody;
import it.pjsoft.reactive.generic.transfer.model.msg.MessageBody;
import it.pjsoft.reactive.generic.transfer.model.msg.RequestHeader;
import it.pjsoft.reactive.generic.transfer.model.msg.ResponseHeader;
import it.pjsoft.reactive.mailer.api.ReactiveMail;
import it.pjsoft.reactive.mailer.api.ReactiveMailer;

@Component(service=ReactiveComponent.class, name="ePizzino_segnalazioni",
property= {"component.qualifier=reactive",
		"component.layer="+RtContext.LAYER_INNER_BOUNDARY})
public class EPizzinoSegnalazioniService implements ReactiveComponent<GenericResponse, GenericRequest>{
	
	@Reference(service=EPizzinoSegnalazioniDAO.class)
	private EPizzinoSegnalazioniDAO dao= null;
	
	@Reference(service=ReactiveMailer.class)
	private ReactiveMailer mailer = null;
	
	@Override
	public GenericResponse execute(GenericRequest req) throws ReactiveException {
		RequestHeader h = req.getHeader();
		String method = h.getMethod();
		MessageBody b = req.getBody();
		GenericResponse res = null;
		switch(method) {
		case "save":
		case "send":
			return upload(h, b, method);
		case "list":
			return list(h, b);
		case "read":
			return read(h, b);
		case "emailSuggerimento":
			return emailSuggerimento(h, b);
		}
		return mkErrorResponse(h, "404", "Comando Sconosciuto");
	}
	

	private GenericResponse upload(RequestHeader h, MessageBody b, String method) {
		Object sdoc = ((JaxbBody)b).getContent();
		Document segnalazione = Document.parse(sdoc.toString());
		String id = segnalazione.getString("_id");
		String stato = segnalazione.getString("stato");
		Document current = dao.readSegnalazione(id);
		boolean success = false;
		switch(method) {
		case "save":
			if(current==null)
				success = dao.insertSegnalazione(segnalazione);
			else
				success = dao.updateSegnalazione(segnalazione);
			break;
		case "send":
			if(current==null)
				success = dao.insertSegnalazione(segnalazione);
			else
				success = dao.updateSegnalazione(segnalazione);
			break;
		}
		if(!success)
			return mkErrorResponse(h, "500", "Errore durante le operazioni con il database");
		
		if("send".equals(method) && !"BOZZA".equals(stato)) {
				Thread th = new Thread() {
					public void run() {
						try {
							List<Document> history = (List<Document>) segnalazione.get("history");
							Document lastSend = history.get(history.size()-1);
							ReactiveMail mail = mailer.newMail(ReactiveConfig.getNode("/ePizzino/mail"));
							String server = ReactiveConfig.getProperty("/ePizzino/global", "appHost");
							mail.setOggetto("ePizzino: aggiornamento segnalazione n. "+segnalazione.getString("_id"));

							MongoCollection<Document> opCol =  Activator.mongoCtrl.getDatabase().getCollection("operatori");
							Map<String, Document> ops = new HashMap<>();
							opCol.find().iterator().forEachRemaining(new Consumer<Document>() {
								@Override
								public void accept(Document d) {
									String opAcc = d.getString("account");
									ops.put(opAcc, d);
								}
							});

							Set<String> addrs = new HashSet<>();
							for(Document h: history) {
								String codMit = h.getString("cod_mittente");
								Document op = ops.get(codMit);
								String addr = op.getString("email"); //TODO leggere addr del mittente
								addrs.add(addr);
							}
							addrs.add("servizi.pagamenti.romacapitale@gmail.com");
							MongoCollection<Document> entiCol =  Activator.mongoCtrl.getDatabase().getCollection("enti");
							Document e=entiCol.find(Document.parse("{'codice': '"+segnalazione.getString("ente")+ "'}")).first();
							addrs.add(e.getString("emailBackOffice"));
							
							for(String m: addrs)
								mail.addTo(m);

							String txt = "ePizzino: Mail automatica - Non rispondere.\n"
									+ "<br/>Ente: " + segnalazione.getString("ente")+"\n"
									+ "<br/>Aggiornamento segnalazione n. <a href=\"" + server + "/ePizzino?segId=" + segnalazione.getString("_id")+ "\">" + segnalazione.getString("_id") + "</a>\n"
									+ "<br/>Effettuato da " + lastSend.getString("nome_mittente")+"\n"
									+ "<br/>Dallo stato: " + lastSend.getString("stato_apertura")+"\n"
									+ "<br/>Allo stato: " + lastSend.getString("stato_chiusura")+"\n"
									+ "<br/>Col commento: " + lastSend.getString("commento")+"\n"
									+ "<br/>Sono state effettuate le seguenti modifiche: " + ((Document)lastSend.get("modifiche")).toJson();
							mail.addTesto(txt);
							mail.send();
						} catch (Exception e) {
							System.out.println("Errore durante le spedizione della mail di aggiornamento: "+e);
						}
					}
				};
				th.start();
		}
		
		return mkSuccessResponse(h, null);
	}




	private GenericResponse list(RequestHeader h, MessageBody b) {
		String operatore = h.getUser();
		
		ObjectNode node = (ObjectNode) ((JaxbBody)b).getContent();
		String ente = getText(node.get("ente"));
		String mittente = getText(node.get("operatore"));
		String richiedente = getText(node.get("richiedente"));
		ArrayNode sts = (ArrayNode) node.get("stati");
		String[] stati = new String[sts.size()];
		for(int i=0;i<sts.size();i++)
			stati[i] = sts.get(i).asText();
		Date da =  getDate(node.get("from"));
		Date a =  getDate(node.get("to"));
		MongoCursor<Document> res = dao.listSegnalazioni(ente, mittente, richiedente, da, a, stati);
		
		final Document ret = Document.parse("{}");
		res.forEachRemaining(new Consumer<Document>() {

			@Override
			public void accept(Document t) {
				String stato = t.getString("stato");
				boolean modificato = t.getBoolean("modificato", false);
				Object original = t.get("original");
				Object cod_operatore = t.get("cod_operatore");
				if(!operatore.equals(cod_operatore)) {
					if("BOZZA".equals(stato))
						return;
					if(modificato && original!=null)
						t = (Document) original;
				}
				ret.put(t.getString("_id"), t); //TODO: nel caso di bozze di diverso operatore mettere t.original invece di t
			}
		});
		JaxbBody body = new JaxbBody();
		body.setContent(ret);
		return  mkSuccessResponse(h, body);
	}




	private String getText(JsonNode node) {
		if(node==null)
			return null;
		if(node instanceof NullNode)
			return null;
		return node.asText();
	}


	private Date getDate(JsonNode node) {
		if(node==null)
			return null;
		if(node instanceof NullNode)
			return null;
		String s = node.asText();
		if(s==null || s.trim().length()==0)
			return null;
		return Date.valueOf(s);
	}




	private GenericResponse read(RequestHeader h, MessageBody b) {
		String id = (String) ((JaxbBody)b).getContent();
		Document segnalazione = dao.readSegnalazione(id);
		JaxbBody body = new JaxbBody();
		body.setContent(segnalazione);
		return mkSuccessResponse(h, body);
	}


	private GenericResponse emailSuggerimento(RequestHeader h, MessageBody b) {
		try {
			ObjectNode obj = (ObjectNode) ((JaxbBody)b).getContent();
			String idSegn = obj.get("id_segnalazione").asText();
			String idSugg = obj.get("id_suggerimento").asText();
			
			Document segnalazione = dao.readSegnalazione(idSegn);
			Document suggerimento = dao.readSuggerimento(idSugg);
			String oggetto = ReactiveConfig.getProperty("/ePizzino/suggerimenti/mail", "oggetto");
			String testo = ReactiveConfig.getProperty("/ePizzino/suggerimenti/mail", "testo");
			String addr = ((Document)segnalazione.get("richiedente")).getString("email");
			String ente = segnalazione.getString("ente"); //TODO: sostituire il codice con la descrizione da "enti"
			String ticket = segnalazione.getString("_id");
			String data = segnalazione.getString("dataCreazione"); //TODO: formattare
			Preferences mailConf = ReactiveConfig.getNode("/ePizzino/mail");

			oggetto = oggetto.replaceAll("\\$\\$ente\\$\\$", ente)
					.replaceAll("\\$\\$ticket\\$\\$", ticket);

			testo = testo.replaceAll("\\$\\$ente\\$\\$", ente)
					.replaceAll("\\$\\$ticket\\$\\$", ticket)
					.replaceAll("\\$\\$data\\$\\$", data)
					.replaceAll("\\$\\$suggerimento\\$\\$", suggerimento.getString("testo"));
			
			//TODO: calcolare e sostituire $$schema$$ come table con i tag json rettificati ed i valori presenti precompilati...
			
			ReactiveMail mail = mailer.newMail(mailConf);
			mail.setOggetto(oggetto);
			mail.addTo(addr);
			mail.addTesto(testo);
			mail.send();
			
			JaxbBody body = new JaxbBody();
			body.setContent("ok");
			return mkSuccessResponse(h, body);
		} catch (Exception e) {
			return mkErrorResponse(h, "600", "Spedizione eMail fallita: "+e);
		}
	}


	private GenericResponse mkSuccessResponse(RequestHeader h, MessageBody body) {
		ResponseHeader resH = new ResponseHeader(h, true, "200", null);
		GenericResponse res = new GenericResponse(resH, body);
		return res;
	}

	private GenericResponse mkErrorResponse(RequestHeader h, String code, String message) {
		ResponseHeader resH = new ResponseHeader(h, false, code, message);
		GenericResponse res = new GenericResponse(resH, null);
		return res;
	}


//	@Reference(service=EPizzinoSegnalazioniDAO.class)
//	public void setDAO(final EPizzinoSegnalazioniDAO dao) {
//		this.dao = dao;
//	}
//
//	public void unsetDAO(final EPizzinoSegnalazioniDAO dao) {
//		this.dao = null;
//	}
//
//	@Reference(service=ReactiveMailer.class)
//	public void setMailer(final ReactiveMailer mailer) {
//		this.mailer = mailer;
//	}
//
//	public void unsetMailer(final ReactiveMailer mailer) {
//		this.mailer = null;
//	}


}
