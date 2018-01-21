package it.eng.reactive.test.internal;

import java.util.Date;

import it.pjsoft.reactive.core.api.ReactiveBundleActivator;

public class Activator extends ReactiveBundleActivator {
	
    public void start() {
        System.out.println("Starting the bundle "+ getContext().getBundle().getSymbolicName() +" at "+new Date());
    }


	public void stop() {
        System.out.println("Stopping the bundle "+ getContext().getBundle().getSymbolicName() +" at "+new Date());
    }

    
}