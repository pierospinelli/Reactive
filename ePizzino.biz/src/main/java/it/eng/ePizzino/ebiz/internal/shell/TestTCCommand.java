package it.eng.ePizzino.ebiz.internal.shell;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.transaction.control.TransactionControl;

import osgi.enroute.debug.api.Debug;

// https://issues.apache.org/jira/browse/ARIES-1767 (pierospinelli/ts262611)

@Component(service = TestTCCommand.class, name = "testtc", 
property = { Debug.COMMAND_SCOPE + "=test",
		Debug.COMMAND_FUNCTION + "=tc" })
public class TestTCCommand {

	private DataSource dataSource;

	private TransactionControl control = null;
	
	private Object sem=new Object();

	public String tc(final int cycles) {
		try {

			control.requiresNew(() -> {

				try (Connection con = dataSource.getConnection(); 
					Statement st = con.createStatement();
					 Statement st2 = con.createStatement();) {
					boolean exists = false;
					try (ResultSet rs = st.executeQuery("select * from LONG_TERM_STATA where TARGET = 'TEST'");) {
						while (rs.next()) {
							exists = true;
							long lid = rs.getLong("ID");
							st2.executeUpdate("update LONG_TERM_STATA set STATUS='0' where ID="+lid);
						}
					}
					if (!exists) {
						st.executeUpdate(
								"insert into LONG_TERM_STATA (STATUS, PROCESSO, TARGET, NOTE) values ('0', 'TEST_GBL', 'TEST', 'TEST')");
						st.executeUpdate(
								"insert into LONG_TERM_STATA (STATUS, PROCESSO, TARGET, NOTE) values ('0', 'TEST_SNG', 'TEST', 'TEST')");
					}
				}

				return 0L;
			});

			int elaborated = control.requiresNew(() -> {

				try (Connection con = dataSource.getConnection(); 
					 Statement st = con.createStatement();
					 Statement st2 = con.createStatement();) {

					for (int i = 0; i < cycles; i++) {
						long status;
						try (ResultSet rs = st.executeQuery(
								"select * from LONG_TERM_STATA where TARGET = 'TEST' AND PROCESSO='TEST_GBL'");) {
							rs.next();
							status = Long.parseLong(rs.getString("STATUS"));
						}
						runThread(i);
						st2.executeUpdate("update LONG_TERM_STATA set STATUS='" + (status + 1) + "' where TARGET = 'TEST' AND PROCESSO='TEST_GBL'");
					}

				}

				return cycles;
			});

			System.out.println("VIA");
			control.requiresNew(() -> {
				try (Connection con = dataSource.getConnection(); 
						PreparedStatement st = con.prepareStatement(
								"select * from LONG_TERM_STATA where TARGET = 'TEST' AND PROCESSO='TEST_SNG' FOR UPDATE",
			                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
						ResultSet rs = st.executeQuery()) {
							System.out.println("Start transaction MAIN" );
							System.out.println("Active scope/active: "+control.activeScope()+"/"+control.activeTransaction());
							System.out.println("Transaction isolation level: "+con.getTransactionIsolation());
							System.out.println("Transaction Key: "+control.getCurrentContext().getTransactionKey());
							System.out.println("Transaction Status: "+control.getCurrentContext().getTransactionStatus());
							System.out.println("Transaction Context: "+control.getCurrentContext().hashCode());
					synchronized(sem) {
						sem.notifyAll();
					}

					Thread.sleep(10000);

				}
				return 0;
			});
			return "Elaborated " + elaborated + " cycles";

		} catch (Exception e) {
			e.printStackTrace();
			return "Error " + e;
		}

	}

	private void runThread(final int id) {
		
		Thread th = new Thread(() ->{
				synchronized(sem) {	
					try {
						sem.wait();
					} catch (InterruptedException e) {}
				}
				
				final String name = "Thread " + id;
				synchronized (sem) {
					System.out.println("Start thread " + name);
					System.out.println("Active scope/active: "+control.activeScope()+"/"+control.activeTransaction());
				}
				
				control.build().requiresNew(() ->{
					try (Connection con = dataSource.getConnection(); 
							PreparedStatement st = con.prepareStatement(
									"select * from LONG_TERM_STATA where TARGET = 'TEST' AND PROCESSO='TEST_SNG' FOR UPDATE",
				                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {
							synchronized (sem) {
								System.out.println("Start transaction " + name);
								System.out.println("Active scope/active: "+control.activeScope()+"/"+control.activeTransaction());
								System.out.println("Transaction isolation level: "+con.getTransactionIsolation());
								System.out.println("Transaction Key: "+control.getCurrentContext().getTransactionKey());
								System.out.println("Transaction Status: "+control.getCurrentContext().getTransactionStatus());
								System.out.println("Transaction Context: "+control.getCurrentContext().hashCode());
							}

//							st.executeUpdate("UPDATE long_term_stata SET STATUS = convert(STATUS, unsigned)+1 where TARGET = 'TEST' AND PROCESSO='TEST_SNG'");
							try(ResultSet rs = st.executeQuery()){
								rs.next();
								int status = Integer.parseInt(rs.getString("STATUS"));
								status++;
								rs.updateString("STATUS", ""+status);
								rs.updateRow();
								System.out.println(status);
							}
						}
						return null;
					});
				
			});

		th.start();
	}

	@Reference
	public void setTransaction(TransactionControl control) {
		this.control = control;
	}

	@Reference(target = "(&" 
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

}