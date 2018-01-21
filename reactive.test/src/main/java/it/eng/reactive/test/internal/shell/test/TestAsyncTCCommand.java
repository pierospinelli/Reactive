package it.eng.reactive.test.internal.shell.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.sql.DataSource;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.promise.Promise;

import it.pjsoft.reactive.core.api.ReactiveExecutor;
import osgi.enroute.debug.api.Debug;

// https://issues.apache.org/jira/browse/ARIES-1767 (pierospinelli/ts262611)

@Component(service = TestAsyncTCCommand.class, name = "testasynctc", 
		   property = { Debug.COMMAND_SCOPE + "=test", Debug.COMMAND_FUNCTION + "=asynctc" })
public class TestAsyncTCCommand {

	@Reference
	private ReactiveExecutor re;

	@Reference(name="fedfismw")
	private DataSource dataSource;


	private Connection con; 
	
	public String asynctc(final int cycles) {
		try {

			re.execRequiresNew(() -> {
				System.out.println("STEP 1");
				try (Statement st = con.createStatement();
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

			int elaborated = cycles;
			
			re.execRequiresNew(() -> {
				System.out.println("STEP 2");
				try (Statement st = con.createStatement();
					 Statement st2 = con.createStatement();) {

					RunSingleTx rstx = new RunSingleTx();
					
					for (int i = 0; i < cycles; i++) {
						long status;
						try (ResultSet rs = st.executeQuery(
								"select * from LONG_TERM_STATA where TARGET = 'TEST' AND PROCESSO='TEST_GBL'");) {
							rs.next();
							status = Long.parseLong(rs.getString("STATUS"));
						}
						final String step = "STEP 3 - " + i;
						Promise<Object> p = re.asyncRequiresNew(() -> {
							System.out.println(step);
							
							try (PreparedStatement st1 = con.prepareStatement(
										"select * from LONG_TERM_STATA where TARGET = 'TEST' AND PROCESSO='TEST_SNG' FOR UPDATE",
					                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {

								try(ResultSet rs = st1.executeQuery()){
									rs.next();
									int status2 = Integer.parseInt(rs.getString("STATUS"));
									status2++;
									rs.updateString("STATUS", ""+status2);
									rs.updateRow();
									System.out.println(status2);
								}
								
							}
							return null;
						});
						
						p.onResolve(()->{
							System.out.println("Resoved: " +step);
						});
						
						st2.executeUpdate("update LONG_TERM_STATA set STATUS='" + (status + 1) + "' where TARGET = 'TEST' AND PROCESSO='TEST_GBL'");
					}

				}

				return cycles;
			});

			return "Elaborated " + elaborated + " cycles";

		} catch (Exception e) {
			e.printStackTrace();
			return "Error " + e;
		}

	}

	private class RunSingleTx implements Callable<String>{

		@Override
		public String call() throws Exception {
			System.out.println("STEP 3");
		
			try (PreparedStatement st = con.prepareStatement(
						"select * from LONG_TERM_STATA where TARGET = 'TEST' AND PROCESSO='TEST_SNG' FOR UPDATE",
	                    ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);) {

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
		}
	
	}


	
	 @Activate
    void start() {
	
        Map<String, Object> providerProps = new HashMap<>();

        try {
			con = re.getTXConnection(dataSource, providerProps);
		} catch (Throwable e) {
			e.printStackTrace();
		}
    }


}
