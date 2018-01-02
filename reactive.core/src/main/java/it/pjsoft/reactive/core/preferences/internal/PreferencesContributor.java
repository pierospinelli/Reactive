package it.pjsoft.reactive.core.preferences.internal;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import it.pjsoft.reactive.core.api.ReactiveBundleActivator;

public class PreferencesContributor {
	private Set<URL> resources = new HashSet<>();
	
	static final String preferencesSchema = "schema/preferences.xsd";
	static final String JAXP_SCHEMA_SOURCE ="http://java.sun.com/xml/jaxp/properties/schemaSource";
	static final String[] schemas = {
			preferencesSchema
	};
	
	public Set<URL> getResources() {
		return resources;
	}
	
	public PreferencesContributor(ReactiveBundleActivator activator) throws Exception {
		Bundle bundle = activator.getContext().getBundle();
		BundleWiring bw = bundle.adapt(BundleWiring.class);
		ClassLoader bcl = bw.getClassLoader();
		
		Collection<String> res = bw.listResources("conf", "*preferences.xml", BundleWiring.LISTRESOURCES_RECURSE);

		for (String cn : res) {
			URL url = bcl.getResource(cn);
			resources.add(url);
		}				
	}
	
	public boolean hasPreferences() {
		return !this.resources.isEmpty();
	}

//
}
