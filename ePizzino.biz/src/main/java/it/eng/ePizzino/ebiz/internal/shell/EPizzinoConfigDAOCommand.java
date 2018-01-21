package it.eng.ePizzino.ebiz.internal.shell;

import java.io.FileReader;

import org.bson.Document;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.mongodb.client.MongoCursor;

import it.eng.ePizzino.ebiz.api.EPizzinoConfigDAO;
import osgi.enroute.debug.api.Debug;

@Component(service = EPizzinoConfigDAOCommand.class, name = "config", 
	property = { Debug.COMMAND_SCOPE + "=ePizzino",
		Debug.COMMAND_FUNCTION + "=config" })
public class EPizzinoConfigDAOCommand {
	
	@Reference(service=EPizzinoConfigDAO.class)
	private EPizzinoConfigDAO dao= null;
	
	public void config(String col, String... pars) {
		if(col==null) {
			System.out.println("Collection non specificata");
			return;
		}

		Document d=null;
		switch(col) {
		case "enti":
			if(pars.length==0) 
				list(dao.getEnti());
			else if(pars.length==1)
				show(dao.getEnte(pars[0]));
			break;
		case "autenticazioni":
			if(pars.length==0) 
				list(dao.getAutenticazioni());
			else if(pars.length==1)
				show(dao.getAutenticazione(pars[0]));
			break;
		case "canaliPagamento":
			if(pars.length==0) 
				list(dao.getCanaliPagamento());
			else if(pars.length==1)
				show(dao.getCanalePagamento(pars[0]));
			break;
		case "contatti":
			if(pars.length==0) 
				list(dao.getContatti());
			else if(pars.length==1)
				show(dao.getContatto(pars[0]));
			break;
		case "crediti":
			if(pars.length==0) 
				list(dao.getCrediti());
			else if(pars.length==1)
				show(dao.getCredito(pars[0]));
			break;
		case "operatori":
			if(pars.length==0) 
				list(dao.getOperatori());
			else if(pars.length==1)
				show(dao.getOperatore(pars[0]));
			break;
		case "problemi":
			if(pars.length==0) 
				list(dao.getProblemi());
			else if(pars.length==1)
				show(dao.getProblema(pars[0]));
			break;
		case "sistemi":
			if(pars.length==0) 
				list(dao.getSistemi());
			else if(pars.length==1)
				show(dao.getSistema(pars[0]));
			break;
		case "stati":
			if(pars.length==0) 
				list(dao.getStati());
			else if(pars.length==1)
				show(dao.getStato(pars[0]));
			break;
		case "suggerimenti":
			if(pars.length==0) 
				list(dao.getSuggerimenti());
			else if(pars.length==1)
				try {
					dao.loadSuggerimentiFromJS(new FileReader(pars[0]));
					System.out.println(pars[0]+" parsed and loaded");
				} catch (Exception e) {
					System.out.println(e);
				}
			break;
		default:
			System.out.println("Collection sconosciuta");
			break;
		}
		System.out.println("OK");
	}
	
	private void list(MongoCursor<Document> cur) {
		Document d;
		while((d= cur.tryNext())!=null) {
			System.out.println(d.toJson());
		}
	}

	private void show(Document d) {
		if(d!=null)
			System.out.println(d.toJson());
		else
			System.out.println("Elemento non trovato");
	}

//	@Reference(service=EPizzinoConfigDAO.class)
//	public void setDAO(final EPizzinoConfigDAO dao) {
//		this.dao = dao;
//	}
//
//	public void unsetDAO(final EPizzinoConfigDAO dao) {
//		this.dao = null;
//	}
}
