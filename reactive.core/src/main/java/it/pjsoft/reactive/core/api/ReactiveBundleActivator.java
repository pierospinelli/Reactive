package it.pjsoft.reactive.core.api;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;

import it.pjsoft.reactive.core.internal.Activator;
import it.pjsoft.reactive.core.preferences.internal.AbstractPreferences;
import it.pjsoft.reactive.core.preferences.internal.ListenerController;
import it.pjsoft.reactive.core.preferences.internal.PreferencesContributor;



public abstract class ReactiveBundleActivator implements BundleActivator {
	protected static BundleContext m_context;
	private ServiceRegistration<PreferencesContributor> pcsr;

	public static BundleContext getContext() {
		return m_context;
	}

	public PreferencesService getPreferenceService(){
		return Activator.SERVICE_INSTANCE;
	}
	
	protected void start() throws Exception {
	}

	protected void stop() throws Exception {
	}

	
	public final void start(BundleContext context) throws Exception {
		m_context = context;
		
		Preferences pr = AbstractPreferences.systemRoot();
		pr.sync();
	
		PreferencesContributor pc = new PreferencesContributor(this);
		if(pc.hasPreferences()){
			Dictionary<String, String> props = new Hashtable<>();
			props.put("bundle", context.getBundle().getSymbolicName());
			pcsr = context.registerService(PreferencesContributor.class, pc, props);
		}
		
		start();
		
		System.out.println(this+" started");
	}
	

	public final void stop(BundleContext context) throws Exception {
		stop();
		
		ListenerController.removeAllPerBundle(context.getBundle());
		if(pcsr!=null)
			pcsr.unregister();
	}
	

}
