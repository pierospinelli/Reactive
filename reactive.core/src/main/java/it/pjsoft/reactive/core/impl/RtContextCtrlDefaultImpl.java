package it.pjsoft.reactive.core.impl;

import static it.pjsoft.reactive.core.spi.RtContextCtrl.DEFAULT_QUALIFIER;
import static it.pjsoft.reactive.core.spi.RtContextCtrl.PROP_QUALIFIER;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.core.spi.RtContextCtrl;;


@Component(service = RtContextCtrl.class, 
	property= {PROP_QUALIFIER+"="+DEFAULT_QUALIFIER})
public class RtContextCtrlDefaultImpl implements RtContextCtrl {

	protected Set<String> layers = new HashSet<String>();
	
//	protected static String getConfigProperty(String key) {
//		return ReactiveConfig.getProperty(key);
//	}

	public RtContextCtrlDefaultImpl() {
		layers.add(RtContext.LAYER_SYSTEM);
		layers.add(RtContext.LAYER_INNER_BOUNDARY);
		layers.add(RtContext.LAYER_OUTER_BOUNDARY);
		layers.add(RtContext.LAYER_BUSINESS);
	}
	
	public Set<String> getLayers() {
		return Collections.unmodifiableSet(layers);
	}

	
//	public void checkRegistration(ReactiveRegistry registry, String layer, String componentName, Object container,
//			ReactiveComponent ann, Object registeredObject, Class<IReactiveComponent<?, ?>> registredClass) throws ReactiveException {
//		if(!layers.contains(layer))
//			throw new ReactiveException("Unknown layer. Admitted layers are: " + layers);
//		
//	}
	
	public void checkAttribute(RtContext rtContext, String collection, String operation, String key, Object value) throws ReactiveException {

	}

	public void checkCall(RtContext rtContext, String destLayer, String componentName, Object component, Object... params) throws ReactiveException {
		if(!layers.contains(destLayer))
			throw new ReactiveException("Unknown layer. Admitted layers are: " + layers);
		if(("facades".equals(destLayer) || "commands".equals(destLayer)) && !rtContext.isSysLevel())
			throw new ReactiveException(destLayer + " cannot be called directly in business code");
		 
	}	
	
}
