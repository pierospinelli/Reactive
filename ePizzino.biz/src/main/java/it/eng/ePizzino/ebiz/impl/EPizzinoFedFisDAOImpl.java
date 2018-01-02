package it.eng.ePizzino.ebiz.impl;

import java.sql.SQLException;
import java.util.Map;

import javax.sql.DataSource;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;

import it.eng.ePizzino.ebiz.api.EPizzinoFedFisDAO;

@Component(service=EPizzinoFedFisDAO.class)
public class EPizzinoFedFisDAOImpl implements EPizzinoFedFisDAO {
	private DataSource dataSource;

	private TransactionControl control;

	@Activate
	void activate(BundleContext context) throws Exception {
	}

	@Reference(target= "(&"
		+ "					(|"
		+ "						(objectClass=javax.sql.DataSource)"
		+ "						(objectClass=javax.sql.XADataSource)"
		+ "					)"
		+ "					(|"
		+ "						(osgi.jndi.service.name=fedfismw)"
		+ "						(datasource=fedfismw)"
		+ "						(name=fedfismw)"
		+ "						(service.id=fedfismw)"
		+ "					)"
		+ "				)")
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Reference
	public void setTransaction(TransactionControl control) {
		this.control = control;
	}

	@Override
	public Map<String, Object> getPagamentoM1ByIDRicevuta(String idRicevuta) throws SQLException {
		return EPizzinoHelper.getPagamentoByIdRicevuta(control, dataSource, idRicevuta);
	}



}
