package it.eng.reactive.test.internal.shell.test;

import org.osgi.framework.ServiceReference;
import org.osgi.service.async.Async;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.promise.Promise;

import osgi.enroute.debug.api.Debug;

@Component(service = TestAsyncCommand.class, name = "testasync", 
		   property = { Debug.COMMAND_SCOPE + "=test", Debug.COMMAND_FUNCTION + "=async" })
public class TestAsyncCommand {

	@Reference(service=org.osgi.service.async.Async.class)
	private Async async;
	
	@Reference
	private ServiceReference<TestTCCommand> tcc;

	public String async(final int cycles) {
		
		TestTCCommand mediatedTcc = async.mediate(tcc, TestTCCommand.class);

		Promise<String> promise = async.call(mediatedTcc.tc(cycles));
		
		System.out.println("Fine parte sincrona");
		
		promise.onResolve(()->{
			System.out.println("Async task resolved");
		});

		return "END";
	}





}
