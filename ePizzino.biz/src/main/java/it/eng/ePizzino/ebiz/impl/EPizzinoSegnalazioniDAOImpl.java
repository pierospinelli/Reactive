package it.eng.ePizzino.ebiz.impl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.in;
import static com.mongodb.client.model.Filters.lt;
import static com.mongodb.client.model.Filters.regex;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;

import it.eng.ePizzino.ebiz.api.EPizzinoSegnalazioniDAO;
import it.eng.ePizzino.ebiz.internal.Activator;
import it.pjsoft.reactive.core.preferences.ReactiveConfig;

@Component(service=EPizzinoSegnalazioniDAO.class)
public class EPizzinoSegnalazioniDAOImpl implements EPizzinoSegnalazioniDAO {

	private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSSZ");
	private String QUERY_ELENCO = null;
	private ArrayList<Document> BASE_PIPELINE = null;
	
	@Activate
	void activate(BundleContext context) throws Exception {
		QUERY_ELENCO = ReactiveConfig.getProperty("/ePizzino/MongoDB", "elencoSegnalazioni");
		Document q = Document.parse(QUERY_ELENCO);
		
		BASE_PIPELINE=(ArrayList<Document>)q.get("query");

		System.out.println(getClass().getName()+ " service activated");
	}

//	@Override
//	public MongoCursor<Document> getSegnalazioni() {
//		MongoCollection<Document> entiCol = Activator.mongoCtrl.getDatabase().getCollection("segnalazioni");
//		return entiCol.find().sort(Document.parse("{'dataCreazione' : 1.0}")).sort(Document.parse("{'dataCreazione' : 1.0}")).iterator();
//	}

	
	@Override
	public Document readSegnalazione(String id) {
		MongoCollection<Document> entiCol =  Activator.mongoCtrl.getDatabase().getCollection("segnalazioni");
		FindIterable<Document> fit = entiCol.find(BsonDocument.parse("{'_id': '" + id +"'}"));
		return fit.first();
	}
	
	@Override
	public Document readSuggerimento(String id) {
		MongoCollection<Document> entiCol =  Activator.mongoCtrl.getDatabase().getCollection("suggerimenti");
		FindIterable<Document> fit = entiCol.find(BsonDocument.parse("{'id': '" + id +"'}"));
		return fit.first();
	}
	
	
	
	@Override
	public MongoCursor<Document> listSegnalazioni(String ente, String operatore, String richiedente, Date da, Date a, String... stati) {
		MongoCollection<Document> entiCol =  Activator.mongoCtrl.getDatabase().getCollection("segnalazioni");

//		Document q = Document.parse(QUERY_ELENCO);
//		ArrayList<Document> pipeline=(ArrayList<Document>)q.get("query");
//		Document stage1 = pipeline.get(0);
		
//		Document match = (Document) stage1.get("$match");
//		if(ente!=null)
//			match.append("ente", ente);
//		if(operatore!=null)	
//			match.append("mittente.account", operatore);
//		if(richiedente!=null)
//			match.append("richiedente.denominazione",  Document.parse("{'$regex': \"" + richiedente +"\"}"));
////		if(da!=null)
////			match.put(gte("dataCreazione", df.format(da)));
////		if(a!=null)
////			filters.add(lt("dataCreazione", df.format(a)));
//		if(stati.length>0) {
//			Document in =  Document.parse("{'$in': []}");
//			Document sd = match.append("stato", in);
//			for(String s: stati)
//				((ArrayList<String>)in.get("$in")).add(s);
//		}
//		if(match.keySet().isEmpty())
//			pipeline.remove(0);
//
//		return entiCol.aggregate(pipeline).iterator();

		List<Bson> filters = new ArrayList<>();
		if(ente!=null)
			filters.add(eq("ente", ente));
		if(operatore!=null)
			filters.add(eq("mittente.account", operatore));
		if(richiedente!=null)
			filters.add(regex("richiedente.denominazione", richiedente));
		if(da!=null)
			filters.add(gte("dataCreazione", df.format(da)));
		if(a!=null)
			filters.add(lt("dataCreazione", df.format(a)));
		if(stati.length>0)
			filters.add(in("stato", stati));
//		if(filters.isEmpty())
//			return entiCol.find().sort(Document.parse("{'dataCreazione' : 1.0}")).iterator();			
//		Bson filter = and(filters); 
//		return entiCol.find(filter).sort(Document.parse("{'dataCreazione' : 1.0}")).iterator();

		
		ArrayList<Bson> pipe = new ArrayList<>();
		if(!filters.isEmpty()) {
			Bson filter = and(filters); 
			Bson match = Aggregates.match(filter);
			pipe.add(match);
		}
		pipe.addAll(BASE_PIPELINE);
		return entiCol.aggregate(pipe).iterator();
	}


	@Override
	public boolean insertSegnalazione(Document segnalazione) {
		MongoCollection<Document> entiCol =  Activator.mongoCtrl.getDatabase().getCollection("segnalazioni");
		try {
			entiCol.insertOne(segnalazione);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public boolean updateSegnalazione(Document segnalazione) {
		MongoCollection<Document> entiCol =  Activator.mongoCtrl.getDatabase().getCollection("segnalazioni");
		try {
			BsonDocument filter = BsonDocument.parse("{'_id' : '" + segnalazione.getString("_id") + "'}");
			Document d = entiCol.findOneAndReplace(filter, segnalazione);
			return d!=null;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	


}
