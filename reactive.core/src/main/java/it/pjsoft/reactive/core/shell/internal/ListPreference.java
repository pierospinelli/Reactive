package it.pjsoft.reactive.core.shell.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import it.pjsoft.reactive.core.preferences.ReactiveConfig;
import osgi.enroute.debug.api.Debug;

@Component(service = ListPreference.class, name = "prefs.command", property = {
		Debug.COMMAND_SCOPE + "=reactive", Debug.COMMAND_FUNCTION + "=prefs" })
public class ListPreference {

	public void prefs(String... pars) throws Exception {
		Map<String, Object> options = new HashMap<>();
		List<String> nodes = parse(pars, options);
		if(nodes.isEmpty()) {
			System.out.println("USAGE: prefs [-l] [s] node1 [node2 ...]");
			System.out.println("\t-l: Long Format for property values");
			System.out.println("\t-s: Show system nodes (having name starting with '.')");
			return;
		}
		boolean longFormat = Boolean.TRUE.equals(options.get("LONG-FORMAT"));
		boolean showSystem = Boolean.TRUE.equals(options.get("SHOW-SYSTEM"));
		for(String node: nodes)
			out(null, node, "", longFormat, showSystem);
	}

	private List<String> parse(String[] pars, Map<String, Object> options) {
		List<String> ret = new ArrayList<>();
		for(String p: pars) {
			if("-l".equalsIgnoreCase(p))
				options.put("LONG-FORMAT", true);
			else if("-s".equalsIgnoreCase(p))
				options.put("SHOW-SYSTEM", true);
			else
				ret.add(p);
		}
		return ret;
	}

	private void out(Preferences parent, String name, String ind, boolean longFormat, boolean showSystem) throws BackingStoreException {
		if((!showSystem) && name.startsWith("."))
			return;
		System.out.println(ind+"  |- "+name+":");
		Preferences node = ReactiveConfig.getNode(parent, name);
		String[] prefs=node.keys();
		if(prefs!=null)
			for(String k: prefs) {
				String pv = node.get(k, null);
				if(!longFormat) {
					if(pv.length()>32)
						pv = pv.substring(0, 32)+"...";
					int nl = pv.indexOf("\n");
					if(nl>0)
						pv = pv.substring(0, nl-1)+"...";
				}
				System.out.println(ind+"  |   |  ("+k+"="+pv+")");
			}

		prefs=ReactiveConfig.getChildrenNames(parent, name);
		if(prefs!=null)
			for(String k: prefs) {
				out(node, k, ind+"  | ", longFormat, showSystem);
			}
	}


}