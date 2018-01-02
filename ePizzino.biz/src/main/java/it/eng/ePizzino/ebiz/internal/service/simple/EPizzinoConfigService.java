package it.eng.ePizzino.ebiz.internal.service.simple;

import java.io.FileReader;
import java.util.function.Consumer;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistries;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.mongodb.client.MongoCursor;

import it.eng.ePizzino.ebiz.api.EPizzinoConfigDAO;
import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;

@Component(service=ReactiveComponent.class, name="ePizzino_config",
property= {"component.qualifier=reactive",
		"component.layer="+RtContext.LAYER_INNER_BOUNDARY})
public class EPizzinoConfigService implements ReactiveComponent<String, String[]>{
	
	private EPizzinoConfigDAO dao= null;
	
	@Override
	public String execute(String[] pars) throws ReactiveException {
		if(pars.length==0) {
			return "{'success': false, 'message': 'Collection non specificata'}";
		}
		String col = pars[0];

		String code = null;
		if(pars.length>1)
			code = pars[1];
		Document d = execute(col, code);
		return d.toJson();
	}
	
	private Document execute(String col, String code) throws ReactiveException {
		Document d=null;
		switch(col) {
		case "enti":
			if(code==null) 
				return list(dao.getEnti());
			else
				return dao.getEnte(code);
		case "autenticazioni":
			if(code==null) 
				return list(dao.getAutenticazioni());
			else
				return dao.getAutenticazione(code);
		case "canaliPagamento":
			if(code==null) 
				return list(dao.getCanaliPagamento());
			else
				return dao.getCanalePagamento(code);
		case "contatti":
			if(code==null) 
				return list(dao.getContatti());
			else
				return dao.getContatto(code);
		case "crediti":
			if(code==null) 
				return list(dao.getCrediti());
			else
				return dao.getCredito(code);
		case "operatori":
			if(code==null) 
				return list(dao.getOperatori());
			else
				return dao.getOperatore(code);
		case "problemi":
			if(code==null) 
				return list(dao.getProblemi());
			else
				return dao.getProblema(code);
		case "sistemi":
			if(code==null) 
				return list(dao.getSistemi());
			else
				return dao.getSistema(code);
		case "stati":
			if(code==null) 
				return list(dao.getStati());
			else
				return dao.getStato(code);
		case "suggerimenti":
			if(code==null) 
				return list(dao.getSuggerimenti());
			else
				try {
					dao.loadSuggerimentiFromJS(new FileReader(code));
					return Document.parse("{'success': false, 'message': '"+code+" parsed and loaded'}");
				} catch (Exception e) {
					e.printStackTrace();
					return Document.parse("{'success': false, 'message': '"+e+"'}");
				}
		default:
			return Document.parse("{'success': false, 'message': 'Collection sconosciuta'}");
		}
	}
	
	private Document list(MongoCursor<Document> cur) {
		final Document ret=Document.parse("{'success': true}");
		final BsonArray col = new BsonArray();
		ret.append("collection", col);
	
		cur.forEachRemaining(new Consumer<Document>() {

			@Override
			public void accept(Document t) {
				col.add(BsonDocument.parse(t.toJson()));
			}
			
		});
		return ret;
	}


	@Reference(service=EPizzinoConfigDAO.class)
	public void setDAO(final EPizzinoConfigDAO dao) {
		this.dao = dao;
	}

	public void unsetDAO(final EPizzinoConfigDAO dao) {
		this.dao = null;
	}


}
