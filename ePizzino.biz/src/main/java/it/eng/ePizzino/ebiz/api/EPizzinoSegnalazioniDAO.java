package it.eng.ePizzino.ebiz.api;

import java.util.Date;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

public interface EPizzinoSegnalazioniDAO {
//	public MongoCursor<Document> getSegnalazioni();
	public Document readSegnalazione(String id);
	public Document readSuggerimento(String id);
	public MongoCursor<Document> listSegnalazioni(String ente, String operatore, String richiedente, Date da, Date a, String... stati);
	public boolean insertSegnalazione(Document segnalazione);
	public boolean updateSegnalazione(Document segnalazione);

}
