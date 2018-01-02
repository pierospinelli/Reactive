package it.eng.ePizzino.ebiz.impl;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.function.Consumer;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bson.Document;
import org.osgi.service.component.annotations.Component;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.util.JSON;

import it.eng.ePizzino.ebiz.api.EPizzinoConfigDAO;
import it.eng.ePizzino.ebiz.internal.Activator;

@Component(service=EPizzinoConfigDAO.class)
public class EPizzinoConfigDAOImpl implements EPizzinoConfigDAO {


	@Override
	public MongoCursor<Document> getEnti() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("enti");
		return entiCol.find().iterator();
	}

	@Override
	public Document getEnte(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("enti");
		FindIterable<Document> it = entiCol.find(Document.parse("{codice: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getAutenticazioni() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("autenticazioni");
		return entiCol.find().iterator();
	}

	@Override
	public Document getAutenticazione(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("autenticazioni");
		FindIterable<Document> it = entiCol.find(Document.parse("{id: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getCanaliPagamento() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("canaliPagamento");
		return entiCol.find().iterator();
	}

	@Override
	public Document getCanalePagamento(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("canaliPagamento");
		FindIterable<Document> it = entiCol.find(Document.parse("{id: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getContatti() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("contatti");
		return entiCol.find().iterator();
	}

	@Override
	public Document getContatto(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("contatti");
		FindIterable<Document> it = entiCol.find(Document.parse("{value: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getCrediti() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("crediti");
		return entiCol.find().iterator();
	}

	@Override
	public Document getCredito(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("crediti");
		FindIterable<Document> it = entiCol.find(Document.parse("{value: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getOperatori() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("operatori");
		return entiCol.find().iterator();
	}

	@Override
	public Document getOperatore(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("operatori");
		FindIterable<Document> it = entiCol.find(Document.parse("{account: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getProblemi() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("problemi");
		return entiCol.find().iterator();
	}

	@Override
	public Document getProblema(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("problemi");
		FindIterable<Document> it = entiCol.find(Document.parse("{value: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getSistemi() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("sistemi");
		return entiCol.find().iterator();
	}

	@Override
	public Document getSistema(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("sistemi");
		FindIterable<Document> it = entiCol.find(Document.parse("{value: '"+id+"'}"));
		return it.iterator().tryNext();
	}

	@Override
	public MongoCursor<Document> getStati() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("stati");
		return entiCol.find().iterator();
	}

	@Override
	public Document getStato(String id) {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("stati");
		FindIterable<Document> it = entiCol.find(Document.parse("{value: '"+id+"'}"));
		return it.iterator().tryNext();
	}
	

	@Override
	public MongoCursor<Document> getSuggerimenti() {
		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("suggerimenti");
		return entiCol.find().iterator();
	}

	@Override
	public void loadSuggerimentiFromJS(Reader rd) throws ScriptException {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
		engine.eval(rd);
		engine.eval(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("js/parseSuggerimenti.js")));

		String documents = (String) engine.get("documents");
		System.out.println(documents);
		
		BasicDBList ret = (BasicDBList) JSON.parse(documents);

		final MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("suggerimenti");
		ret.forEach(new Consumer<Object>() {

					@Override
					public void accept(Object t) {
						entiCol.insertOne(Document.parse(((BasicDBObject)t).toJson()));
					}
		});
		
	}


	

}
