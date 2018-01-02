package it.eng.ePizzino.ebiz.api;

import java.io.Reader;

import javax.script.ScriptException;

import org.bson.Document;

import com.mongodb.client.MongoCursor;

public interface EPizzinoConfigDAO {
	public MongoCursor<Document> getEnti();
	public Document getEnte(String id);

	public MongoCursor<Document> getAutenticazioni();
	public Document getAutenticazione(String id);

	public MongoCursor<Document> getCanaliPagamento();
	public Document getCanalePagamento(String id);

	public MongoCursor<Document> getContatti();
	public Document getContatto(String id);

	public MongoCursor<Document> getCrediti();
	public Document getCredito(String id);

	public MongoCursor<Document> getOperatori();
	public Document getOperatore(String id);

	public MongoCursor<Document> getProblemi();
	public Document getProblema(String id);

	public MongoCursor<Document> getSistemi();
	public Document getSistema(String id);

	public MongoCursor<Document> getStati();
	public Document getStato(String id);

	public MongoCursor<Document> getSuggerimenti();
	void loadSuggerimentiFromJS(Reader rd) throws ScriptException;
	
}
