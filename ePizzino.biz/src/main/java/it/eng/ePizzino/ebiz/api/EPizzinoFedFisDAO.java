package it.eng.ePizzino.ebiz.api;

import java.sql.SQLException;
import java.util.Map;

public interface EPizzinoFedFisDAO {

	public Map<String, Object> getPagamentoM1ByIDRicevuta(String idRicevuta) throws SQLException;
	
}
