package it.eng.ePizzino.ebiz.internal.shell;

import java.sql.SQLException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import it.eng.ePizzino.ebiz.api.EPizzinoFedFisDAO;
import osgi.enroute.debug.api.Debug;

@Component(service = EPizzinoFedFisDAOCommand.class, name = "ricevuta", 
	property = { Debug.COMMAND_SCOPE + "=ePizzino",
		Debug.COMMAND_FUNCTION + "=ricevuta" })
public class EPizzinoFedFisDAOCommand {
	
	private EPizzinoFedFisDAO dao= null;
	
	public void ricevuta(String idRicevuta) {
		try {
			System.out.println(dao.getPagamentoM1ByIDRicevuta(idRicevuta));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Reference(service=EPizzinoFedFisDAO.class)
	public void setDAO(final EPizzinoFedFisDAO dao) {
		this.dao = dao;
	}

	public void unsetDAO(final EPizzinoFedFisDAO dao) {
		this.dao = null;
	}
}
