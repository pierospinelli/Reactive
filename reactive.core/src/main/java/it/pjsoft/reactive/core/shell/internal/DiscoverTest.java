package it.pjsoft.reactive.core.shell.internal;


import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveMethod;
import it.pjsoft.reactive.core.internal.Activator;
import osgi.enroute.debug.api.Debug;

@Component(service = DiscoverTest.class,
		name = "discover.command", 
		property = { Debug.COMMAND_SCOPE + "=reactive",
					Debug.COMMAND_FUNCTION + "=discover" 
					})
public class DiscoverTest {

	public void discover(String... pars) throws Exception{

		if(pars.length==0) {
			listCommands();
			listMethods();
		}else if("-C".equalsIgnoreCase(pars[0])){
			listCommands(pars);
		}else if("-M".equalsIgnoreCase(pars[0]) | "-E".equalsIgnoreCase(pars[0])){
			listMethods(pars);
		} else
			usage();

	}
	
	private void listCommands(String... pars) throws InvalidSyntaxException {
		System.out.println("COMMANDS:");
		
		for(ServiceReference<?> sr: Activator.getContext().getAllServiceReferences(ReactiveComponent.class.getName(), null)) {
			ReactiveComponent<?,?> s = (ReactiveComponent<?,?>) Activator.getContext().getService(sr);
			if(s==null)
				continue;
			String cmd = (String)sr.getProperty("component.name");

			if(pars.length>=2) {
				String pattern = ".*" + pars[1] + ".*";
				if(!Pattern.matches(pattern, cmd)) 
					continue;
			}
			System.out.println("\t"+cmd+" - implementation: "+s.getClass().getCanonicalName());

			Type[] gis = s.getClass().getGenericInterfaces();
			Type[] pts = ((ParameterizedType)gis[0]).getActualTypeArguments();
			System.out.println("\t\tinput:" + pts[1].getTypeName() );
			System.out.println("\t\toutput:" + pts[0].getTypeName() );
		}
	}

	private void listMethods(String... pars) throws InvalidSyntaxException {
		System.out.println("METHODS");
		
		for(ServiceReference<?> sr: Activator.getContext().getAllServiceReferences(ReactiveMethod.class.getName(), "("+ReactiveMethod.CALL_BACK_TYPE + "=METHOD)")) {
			ReactiveMethod<?,?> s = (ReactiveMethod<?,?>) Activator.getContext().getService(sr);
			if(s==null)
				continue;

			String mth = (String)sr.getProperty(ReactiveMethod.METHOD_NAME);
			String clz = (String)sr.getProperty(ReactiveMethod.TARGET_CLASS);

			if(pars.length>1) {
				String pattern = ".*" + pars[1] + ".*";
				if("-M".equalsIgnoreCase(pars[0]) && pars.length>=2 && !Pattern.matches(pattern, mth))
					continue;
				else if("-E".equalsIgnoreCase(pars[0]) && pars.length>=2 && !Pattern.matches(pattern, clz))
					continue;
			}
				
			System.out.print("\t"+clz+"::");
			System.out.println(mth+" - implementation: "+s.getClass().getCanonicalName());
			Type[] gis = s.getClass().getGenericInterfaces();
			Type[] pts = ((ParameterizedType)gis[0]).getActualTypeArguments();
			System.out.println("\t\tinput:" + pts[1].getTypeName() );
			System.out.println("\t\toutput:" + pts[0].getTypeName() );
			for(ServiceReference<?> src: Activator.getContext().getAllServiceReferences(ReactiveMethod.class.getName(), 
					"(& (!("+ReactiveMethod.CALL_BACK_TYPE+"=METHOD)) ("+ReactiveMethod.METHOD_NAME+"="+mth+"))")) {
				ReactiveMethod<?,?> sc = (ReactiveMethod<?,?>) Activator.getContext().getService(src);
				if(sc==null)
					continue;
				String eid = (String)src.getProperty(ReactiveMethod.TARGET_ENTITY_ID);
				String cbt = (String)src.getProperty(ReactiveMethod.CALL_BACK_TYPE);
				System.out.print("\t\t" + cbt + (eid!=null ? " on entity "+eid : " on class"));
				System.out.println(" - implementation: "+sc.getClass().getCanonicalName());
			}
		}
	}

	private void usage() {
		System.out.println("USAGES:");
		System.out.println("\t1) discover\n\t\t(List all registered reactive commands and methods)");
		System.out.println("\t2) discover -C [name-pattern]\n\t\t(List all registered reactive commands or those matching the given pattern)");
		System.out.println("\t3) discover -M [name-pattern]\n\t\t(List all registered reactive methods or those matching the given pattern)");
		System.out.println("\t4) discover -E name-pattern\n\t\t(List all registered reactive methods for entities matching the given pattern)");
	}
}
