package it.pjsoft.reactive.ws.internal;

import java.util.Date;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
    public void start(BundleContext context) {
    	Activator.context = context;
        System.out.println("Starting the bundle "+ context.getBundle().getSymbolicName() +" at "+new Date());
    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle "+ context.getBundle().getSymbolicName() +" at "+new Date());
        Activator.context = null;
    }

    public static BundleContext getContext() {
		return context;
	}
}