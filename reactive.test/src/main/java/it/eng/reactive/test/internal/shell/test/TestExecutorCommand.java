package it.eng.reactive.test.internal.shell.test;

import java.util.concurrent.Executor;

import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;

import it.eng.reactive.test.internal.Activator;
import osgi.enroute.debug.api.Debug;

@Component(service = TestExecutorCommand.class, name = "testexecutor", 
		   property = { Debug.COMMAND_SCOPE + "=test", Debug.COMMAND_FUNCTION + "=executor" })
public class TestExecutorCommand {

	@Reference
	private Executor executor;
	private Promise<String>	promise;
	
	@Reference
	private ServiceReference<TestTCCommand> tcc;

	public String executor(final int cycles) {

		Deferred<String> deferred = new Deferred<>();
			
		executor.execute(() -> {
			try {
				Activator.getContext().getService(tcc).tc(cycles);
				deferred.resolve("DONE");
			} catch (Exception e) {
				deferred.fail(e);
			}
		});
		
		System.out.println("Fine parte sincrona");
		
		promise = deferred.getPromise();

		
		promise.onResolve(()->{
			System.out.println("Async task resolved");
		});

		return "END";
	}





}
