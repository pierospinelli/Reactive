package it.eng.ePizzino.ebiz.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.osgi.service.transaction.control.TransactionControl;

import it.pjsoft.reactive.core.preferences.ReactiveConfig;

public class EPizzinoHelper {
	
	private static String pagamentoM1ByIDRicevutaSQL;
	private static String debitiByIdPagamentoSQL;
	private static String notificheByIdDebitoSQL;
	
	static {
		pagamentoM1ByIDRicevutaSQL = ReactiveConfig.getProperty("/ePizzino/sql", "pagamentoM1ByIDRicevuta");
		debitiByIdPagamentoSQL = ReactiveConfig.getProperty("/ePizzino/sql", "debitiByIdPagamento");
		notificheByIdDebitoSQL = ReactiveConfig.getProperty("/ePizzino/sql", "notificheByIdDebito");

	}

	public static Map<String, Object> getPagamentoByIdRicevuta(TransactionControl control, DataSource dataSource, String idRicevuta) {
		return control.supports(new Callable<Map<String, Object>>() {
			
			@Override
			public Map<String, Object> call() throws Exception {
				try (Connection con = dataSource.getConnection();
						PreparedStatement stmt1 = con.prepareStatement(pagamentoM1ByIDRicevutaSQL); ) {
					stmt1.setString(1, idRicevuta);
					try(ResultSet rs1 = stmt1.executeQuery();){
						List<Map<String, Object>> ret1 = rs2listOfMaps(rs1);
						if(ret1!=null && ! ret1.isEmpty()) {
							 Map<String, Object> pagamento = ret1.get(0);
							
							 Long pid = (Long) pagamento.get("ID");
							 if(pid!=null) {
									try (PreparedStatement stmt2 = con.prepareStatement(debitiByIdPagamentoSQL); ) {
										stmt2.setLong(1, pid);
										try(ResultSet rs2 = stmt2.executeQuery();){
											List<Map<String, Object>> debiti = rs2listOfMaps(rs2);
											for(Map<String, Object> debito: debiti) {
												Long did = (Long) debito.get("ID");
												if(did!=null) {
													try (PreparedStatement stmt3 = con.prepareStatement(notificheByIdDebitoSQL); ) {
														stmt3.setLong(1, did);
														try(ResultSet rs3 = stmt3.executeQuery();){
															List<Map<String, Object>> notifiche = rs2listOfMaps(rs3);
															debito.put("notifiche",  notifiche);
														}
													}
												}
											}
											pagamento.put("debiti", debiti);
											return pagamento;
										}
									}
							 }
						}
					}
				}
				return null;
			}

		});
	}
	
	private static List<Map<String, Object>> rs2listOfMaps(ResultSet rs) throws SQLException {
		List<Map<String, Object>> ret = new ArrayList<>();

		ResultSetMetaData rsmd = rs.getMetaData();

		while (rs.next()) {
			Map<String, Object> record = new HashMap<>();
			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				String f = rsmd.getColumnLabel(i);
				Object v = rs.getObject(i);
				record.put(f, v);
			}
			ret.add(record);
		}
		return ret;

		
	}

}
