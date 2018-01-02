package it.pjsoft.reactive.core.shell.internal;

import java.util.Arrays;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.internal.Activator;
import osgi.enroute.debug.api.Debug;

@Component(service = ProfileCtrl.class, name = "profs.command", property = {
		Debug.COMMAND_SCOPE + "=reactive", Debug.COMMAND_FUNCTION + "=profs" })
public class ProfileCtrl {

	public void profs(String... pars) throws Exception {
		if(pars.length==0) {
			for(String p: Activator.getProfiles())
				System.out.println("- " + p);
		}else if("-R".equalsIgnoreCase(pars[0])){
			if(pars.length<2)
				usage();
			else
				removeProfiles(Arrays.copyOfRange(pars, 1, pars.length));
		}else if("-A".equalsIgnoreCase(pars[0])){
			if(pars.length<2)
				usage();
			else
				addProfiles(Arrays.copyOfRange(pars, 1, pars.length));
		}else if("-S".equalsIgnoreCase(pars[0])){
			if(pars.length != 3)
				usage();
			else
				substProfile(pars[1], pars[2]);
		} else
			usage();
	}

	private void substProfile(String _old, String _new) {
		Activator.getProfiles().remove("#"+_old);
		Activator.getProfiles().add("#"+_new);
		restart();
	}


	private void addProfiles(String[] profs) {
		for(String p: profs)
			Activator.getProfiles().add("#"+p);
		restart();
	}

	private void removeProfiles(String[] profs) {
		for(String p: profs)
			Activator.getProfiles().remove("#"+p);
		restart();
	}

	private void usage() {
		System.out.println("USAGES:");
		System.out.println("\t1) profs\n\t\t(List all active profiles)");
		System.out.println("\t2) profs -A name1 [name2 ...] \n\t\t(Add specified profiles)");
		System.out.println("\t3) profs -D name1 [name2 ...] \n\t\t(Remove specified profiles)");
		System.out.println("\t4) profs -S old new \n\t\t(Substitute old profile with new one)");
	}

	private void restart() {
		try {
			Bundle bundle = Activator.getContext().getBundle();
			bundle.stop();
			bundle.start();
		} catch (BundleException e) {
			e.printStackTrace();
		}
	}
	
}