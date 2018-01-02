package it.pjsoft.reactive.rs.strategy;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;

import it.pjsoft.reactive.rs.internal.Activator;
import it.pjsoft.reactive.rs.strategy.impl.DefaultDeleteRestUriStrategy;
import it.pjsoft.reactive.rs.strategy.impl.DefaultGetRestUriStrategy;
import it.pjsoft.reactive.rs.strategy.impl.DefaultPostRestUriStrategy;
import it.pjsoft.reactive.rs.strategy.impl.DefaultPutRestUriStrategy;


public abstract class RestUriStrategy{
	public static String QUALIFIER="REST_URI_STRATEGY";
	public static final String REST_URI_METHOD = "reactive.rest.uri.method";

	
	private static final Map<String, RestUriStrategy> defaults = new HashMap<>();
	private static final Map<String, RestUriStrategy> instances = new HashMap<>();
	
	public abstract Map<String, String> parsePath(HttpServletRequest httpRequest);
	
	public static RestUriStrategy get(HttpMethod method) {
		return get(method.value());
	}
	
	public static RestUriStrategy get(String method) {
		return instances.get(method);
	}
	
	static {
		try {
			defaults.put(HttpMethod.GET, new DefaultGetRestUriStrategy());
			defaults.put(HttpMethod.POST, new DefaultPostRestUriStrategy());
			defaults.put(HttpMethod.PUT, new DefaultPutRestUriStrategy());
			defaults.put(HttpMethod.DELETE, new DefaultDeleteRestUriStrategy());
			instances.putAll(defaults);

			Activator.getContext().addServiceListener(new ServiceListener() {
				
				@Override
				public void serviceChanged(ServiceEvent event) {
					ServiceReference<RestUriStrategy> ref = (ServiceReference<RestUriStrategy>) event.getServiceReference();
					String method = (String) ref.getProperty(REST_URI_METHOD);
					switch(event.getType()) {
					case ServiceEvent.REGISTERED:
					case ServiceEvent.MODIFIED:
						instances.put(method, (RestUriStrategy) event.getSource());
						break;
					case ServiceEvent.UNREGISTERING:
					case ServiceEvent.MODIFIED_ENDMATCH:
						instances.put(method, defaults.get(method));
						break;
					}
				}
			}, "(&(component.service="+RestUriStrategy.class.getName()+")(reactive.qualifier="+QUALIFIER+"))");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	

	}
}
