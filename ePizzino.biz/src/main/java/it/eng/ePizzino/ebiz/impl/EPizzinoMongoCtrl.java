package it.eng.ePizzino.ebiz.impl;

import org.bson.Document;
import org.osgi.service.prefs.Preferences;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import it.pjsoft.reactive.core.preferences.NodeChangeEvent;
import it.pjsoft.reactive.core.preferences.NodeChangeListener;
import it.pjsoft.reactive.core.preferences.PreferenceChangeEvent;
import it.pjsoft.reactive.core.preferences.PreferenceChangeListener;
import it.pjsoft.reactive.core.preferences.ReactiveConfig;

public class EPizzinoMongoCtrl implements NodeChangeListener, PreferenceChangeListener{	
	private static final String E_PIZZINO_MONGO_DB = "/ePizzino/MongoDB";
	private MongoClientURI uri = null;
	private MongoClient mongoClient = null;
	private MongoDatabase database = null;
	
	public EPizzinoMongoCtrl() {
		refreshURI();
		ReactiveConfig.addNodeChangeListener(E_PIZZINO_MONGO_DB,this);
	}
	
	@Override
	public void childRemoved(NodeChangeEvent evt) {
		refreshURI();
		System.out.println(evt);
	}
	
	@Override
	public void childAdded(NodeChangeEvent evt) {
		refreshURI();
		testMongo();
		System.out.println(evt);
	}

	@Override
	public void preferenceChange(PreferenceChangeEvent evt) {
		refreshURI();
		testMongo();
		System.out.println(evt);
	}
	

	public synchronized MongoDatabase getDatabase() {
		return database;
	}
	
	private synchronized void refreshURI() {
		String prop = null;
		Preferences prefs =ReactiveConfig.getNode(E_PIZZINO_MONGO_DB);
		if(prefs!=null)
			try {
				prop = prefs.get("URI", null);
			} catch (IllegalStateException e1) {
			}
		if(prop!=null) {
			prop=prop.trim().replace("\\n", "").replaceAll("\\s", "");
			uri = new MongoClientURI(prop);
			mongoClient = new MongoClient(uri);
			database = mongoClient.getDatabase("ePizzino");

			ReactiveConfig.addPreferenceChangeListener(prefs,this);
			
		} else {
			if(mongoClient!=null)
				try {
					mongoClient.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				database = null;
				mongoClient = null;
				uri = null;
		}
	}

    private void testMongo() {
    	try {
			MongoCursor<Document> cur = new EPizzinoConfigDAOImpl().getEnti();
			Document d;
			while((d= cur.tryNext())!=null) {
				System.out.println(d.toJson());
			}
			cur.close();
		} catch (Exception e) {
		}

	}

    public void close() {
		ReactiveConfig.removeNodeChangeListener(E_PIZZINO_MONGO_DB,this);
	}
}
