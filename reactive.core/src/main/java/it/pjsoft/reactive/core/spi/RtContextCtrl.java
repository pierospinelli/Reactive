package it.pjsoft.reactive.core.spi;

import java.util.Set;

import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;

public interface RtContextCtrl {
	public static final String PROP_QUALIFIER = "reactive.rt.context.qualifier";
	public static final String DEFAULT_QUALIFIER = "DEFAULT";
	
	public static final String SYS_COLLECTION="sys";
	public static final String RT_COLLECTION="rt";
	public static final String READ_OP="r";
	public static final String WRITE_OP="w";
	public static final String DEL_OP="d";
	
	public Set<String> getLayers();
		
//	public void checkRegistration(ReactiveRegistry registry, String layer, String componentName, 
//			Object container, ReactiveComponent ann, Object registeredObject, Class<IReactiveComponent<?, ?>> registredClass) throws ReactiveException;

	public void checkAttribute(RtContext rtContext, String collection, String operation, String key, Object value) throws ReactiveException;

	public void checkCall(RtContext rtContext, String destLayer, String componentName, Object component, Object... params) throws ReactiveException;

}
