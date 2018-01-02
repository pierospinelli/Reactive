package it.pjsoft.reactive.core.preferences;

import java.io.IOException;
import java.io.PrintStream;

import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import it.pjsoft.reactive.core.preferences.internal.AbstractPreferences;
import it.pjsoft.reactive.core.preferences.internal.ListenerController;

public class ReactiveConfig {
	private static Preferences sysRoot = AbstractPreferences.systemRoot();

	public static Preferences getNode(String name){
		return getNode(null, name);
	}
	
	public static Preferences getNode(Preferences parent, String name){
		try {
			if(parent==null)
				parent = sysRoot;
			if(!parent.nodeExists(name))
				return null;
			return parent.node(name);
		} catch (BackingStoreException e) {
			return null;
		}
	}

	public static String getProperty(String node, String key){
		try {
			if(!sysRoot.nodeExists(node))
				return null;
			return sysRoot.node(node).get(key, null);
		} catch (BackingStoreException e) {
			return null;
		}
	}

	public static String[] getPropertyNames(String node){
		try {
			if(!sysRoot.nodeExists(node))
				return null;
			return sysRoot.node(node).keys();
		} catch (BackingStoreException e) {
			return null;
		}
	}

	public static Preferences getChild(String node, String name){
		return getChild(null, node, name);
	}

	public static Preferences getChild(Preferences parent, String node, String name){
		try {
			if(parent==null)
				parent = sysRoot;
			if(!parent.nodeExists(node))
				return null;
			return parent.node(node).node(name);
		} catch (BackingStoreException e) {
			return null;
		}
	}

	public static String[] getChildrenNames(String node){
		return getChildrenNames(null, node);
	}

	public static String[] getChildrenNames(Preferences parent, String node){
		try {
			if(parent==null)
				parent = sysRoot;
			if(!parent.nodeExists(node))
				return null;
			return parent.node(node).childrenNames();
		} catch (BackingStoreException e) {
			return null;
		}
	}
	
	public static Preferences systemRoot() {
		return AbstractPreferences.systemRoot();
	}

	public static Preferences userRoot() {
		return AbstractPreferences.userRoot();
	}

    public static boolean isUserNode(Preferences p) {
    	AbstractPreferences ap = (AbstractPreferences)p;
    	return ap.getRoot()== AbstractPreferences.userRoot();
    }

    public static void addPreferenceChangeListener(Preferences p, PreferenceChangeListener pcl) {
       	AbstractPreferences ap = (AbstractPreferences)p;
       	ap.addPreferenceChangeListener(pcl);
    }

    public static void removePreferenceChangeListener(Preferences p, PreferenceChangeListener pcl) {
       	AbstractPreferences ap = (AbstractPreferences)p;
       	ap.removePreferenceChangeListener(pcl);
    }

    public static void addPreferenceChangeListener(String nodePath, PreferenceChangeListener pcl) {
    	ListenerController.addPreferenceChangeListener(nodePath, pcl);
    }

    public static void removePreferenceChangeListener(String nodePath, PreferenceChangeListener pcl) {
    	ListenerController.removePreferenceChangeListener(nodePath, pcl);
    }

    public static void addNodeChangeListener(Preferences p, NodeChangeListener ncl) {
      	AbstractPreferences ap = (AbstractPreferences)p;
       	ap.addNodeChangeListener(ncl);
    }

    public static void removeNodeChangeListener(Preferences p, NodeChangeListener ncl) {
     	AbstractPreferences ap = (AbstractPreferences)p;
       	ap.removeNodeChangeListener(ncl);
     }

    public static void addNodeChangeListener(String nodePath, NodeChangeListener ncl) {
     	ListenerController.addNodeChangeListener(nodePath, ncl);
    }

    public static void removeNodeChangeListener(String nodePath, NodeChangeListener ncl) {
    	ListenerController.removeNodeChangeListener(nodePath, ncl);
     }

	public static void exportSubtree(Preferences p, PrintStream out) throws IOException, BackingStoreException {
     	AbstractPreferences ap = (AbstractPreferences)p;
		ap.exportSubtree(out);
	}

	public static void exportNode(Preferences p, PrintStream out) throws IOException, BackingStoreException {
     	AbstractPreferences ap = (AbstractPreferences)p;
		ap.exportNode(out);
	}


}
