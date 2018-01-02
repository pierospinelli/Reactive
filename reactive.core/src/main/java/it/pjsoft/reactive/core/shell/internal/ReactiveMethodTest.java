package it.pjsoft.reactive.core.shell.internal;


import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.core.shell.TestEntity;
import it.pjsoft.reactive.core.shell.TestRepository;
import osgi.enroute.debug.api.Debug;

@Component(service = ReactiveMethodTest.class,
		name = "method.command", 
		property = { Debug.COMMAND_SCOPE + "=reactive",
					Debug.COMMAND_FUNCTION + "=method" 
					})
public class ReactiveMethodTest {

	public void method(String method, String... pars) throws Exception{
		TestEntity e = TestRepository.getCurrentEntity();
		if(e==null) {
			System.out.println("Set reactive:entity before method execution");
			return;
		}
		
		try(RtContext rtctx = RtContext.open();) {
			System.out.println("Executing "+method+" on entity "+e.getId()+" - "+e.getName());
			String ret = rtctx.executeMehod(e, method, pars);
			System.out.println(ret);
		}catch(Exception ex) {
			ex.printStackTrace();
		}

	}

}
