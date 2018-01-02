package it.pjsoft.reactive.core.shell.internal;


import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.api.RtContext;
import osgi.enroute.debug.api.Debug;

@Component(service = ReactiveCommandTest.class,
		name = "exec.command", 
		property = { Debug.COMMAND_SCOPE + "=reactive",
					Debug.COMMAND_FUNCTION + "=exec" 
					})
public class ReactiveCommandTest {

//	private ReactiveComponentExecuter executer = null;

//	public void exec(String cmd, String... pars) throws Exception{
//		String ret = executer.execute(cmd, pars);
//		System.out.println(ret);
//	}

//	@Reference
//	public void setReactiveCommandExecuter(final ReactiveComponentExecuter executer) {
//		this.executer  = executer;
//	}
//
//	public void unsetReactiveCommandExecuter(final ReactiveComponentExecuter executer) {
//		this.executer = null;
//	}
	
	public void exec(String cmd, String... pars) throws Exception{
		
		try(RtContext rtctx = RtContext.open();) {
			String ret = rtctx.executeComponent(RtContext.LAYER_INNER_BOUNDARY, cmd, pars);
			System.out.println(ret);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}

}
