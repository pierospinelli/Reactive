package it.eng.ePizzino.ebiz.internal;

import java.util.Date;

import it.eng.ePizzino.ebiz.impl.EPizzinoMongoCtrl;
import it.pjsoft.reactive.core.api.ReactiveBundleActivator;

public class Activator extends ReactiveBundleActivator {
	public static EPizzinoMongoCtrl mongoCtrl=null;
	
    public void start() {
        System.out.println("Starting the bundle "+ getContext().getBundle().getSymbolicName() +" at "+new Date());
        mongoCtrl=new EPizzinoMongoCtrl();
    }


	public void stop() {
        System.out.println("Stopping the bundle "+ getContext().getBundle().getSymbolicName() +" at "+new Date());
        mongoCtrl.close();
        mongoCtrl=null;
    }

    
}