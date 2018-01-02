package it.pjsoft.reactive.core.preferences.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;

import org.osgi.framework.Bundle;
import org.osgi.service.prefs.BackingStoreException;

import it.pjsoft.reactive.core.preferences.ReactiveConfig;

public class ReactivePreferences extends AbstractPreferences {
	private static ThreadLocal<Bundle> cb = new ThreadLocal<>();
	private static Map<Bundle, Set<String>> prefsXbundle = new HashMap<>();
	
	private Set<Bundle> owners = new HashSet<>();
	
	public static <T> T doWithBundle(Bundle b, Callable<T> clb) throws Exception {
		synchronized(cb) {
			try {
				cb.set(b);
				return clb.call();
			}finally {
				cb.remove();
			}
		}
	}

	protected ReactivePreferences(ReactivePreferences parent, String name) {
		super(parent, name);
	}
	
	public static void unmountBundle(Bundle b) throws BackingStoreException {
		Set<String> prefs = prefsXbundle.remove(b);
		if(prefs==null) 
			return;
		ReactivePreferences root = (ReactivePreferences) AbstractPreferences.systemRoot();
		for(String s: prefs) {
			String[] ret = s.split("#");
			if(root.nodeExists(ret[0])) {
				ReactivePreferences n = (ReactivePreferences) root.node(ret[0]);
				if(ret.length>1)
					n.remove(ret[1]);
				n.removeNode(); //Removed just in case no other bundle uses the node
				
			}
		}
	}
	
	private void addOwner(Bundle b) {
		owners.add(cb.get());
		if(this.parent!=null && this.parent!=this.root)
			((ReactivePreferences)this.parent).addOwner(b);
	}

	private void addToBundleDomain(Bundle b, ReactivePreferences node, String prop) {
		if(!node.parent.isUserNode()){
			addOwner(b);
			Set<String> s = prefsXbundle.get(b);
			if(s==null) {
				s = new TreeSet<String>();
				prefsXbundle.put(b,  s);
			}
			s.add(prop!=null ? node.absolutePath()+"#"+prop : node.absolutePath());
		}
	}
	
	@Override
	protected AbstractPreferences mkChild(AbstractPreferences parent, String name) {
		Bundle b = cb.get();
		if((!parent.isUserNode()) && null==b)
			throw new RuntimeException("Reactive System Preferences cannot be created by users");

		ReactivePreferences node = new ReactivePreferences((ReactivePreferences)parent, name);
		
		addToBundleDomain(b, node, null);
		
		return node;
	}

	public void put(String key, String value) {
		if (key == null || value == null)
			throw new NullPointerException();
		if (key.length() > MAX_KEY_LENGTH)
			throw new IllegalArgumentException("Key too long: " + key);
		if (value.length() > MAX_VALUE_LENGTH)
			throw new IllegalArgumentException("Value too long: " + value);

		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");

			if(!parent.isUserNode()){
				if(null==cb.get())
					throw new RuntimeException("Reactive System Preferences cannot be modified by users");
				if(vals.containsKey(key))
					throw new RuntimeException("Reactive System Preferences cannot be set twice");
			}

			addToBundleDomain(cb.get(), this, key);

			vals.put(key, value);
			ListenerController.enqueuePreferenceChangeEvent(this, key, value);
		}
	}
	
	public void remove(String key) {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");

			if((!parent.isUserNode()) && null==cb.get())
				throw new RuntimeException("Reactive System Preferences cannot be modified by users");

			vals.remove(key);
			ListenerController.enqueuePreferenceChangeEvent(this, key, null);
		}
	}

	
	public void removeNode() throws BackingStoreException {
		if (this == root)
			throw new UnsupportedOperationException("Can't remove the root!");
		synchronized (parent.lock) {
			Bundle b = cb.get();

			if(!parent.isUserNode()) {
				if(null==b)
					throw new RuntimeException("Reactive System Preferences cannot be removed by users");

				this.owners.remove(b);
				if(this.owners.isEmpty() && this.children.isEmpty()) {
					removeNode2();
					parent.children.remove(name);
					if(parent!=root)
						parent.removeNode();
				}
			} else {
				removeNode2();
				parent.children.remove(name);
			}
		}
	}


	/*
	 * Called with locks on all nodes on path from parent of "removal root" to
	 * this (including the former but excluding the latter).
	 */
	protected void removeNode2() throws BackingStoreException {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node already removed.");

			// Recursively remove all cached children
			for (Iterator<AbstractPreferences> i = children.values().iterator(); i.hasNext();) {
				try {
					((ReactivePreferences)i.next()).removeNode2();
					i.remove();
				} catch (BackingStoreException x) {
				}
			}

			// TODO: #ADD TO REMOVED LIST

			removed = true;
			ListenerController.enqueueNodeRemovedEvent(parent, this);
		}
	}

	public void clear() throws BackingStoreException {
		synchronized (lock) {
			String[] keys = keys();
			for (int i = 0; i < keys.length; i++)
				remove(keys[i]);
		}
	}

	protected void flush2() throws BackingStoreException {
		synchronized (lock) {
			if (removed) {
				parent.children.remove(name);
				return;
			}
			for(AbstractPreferences ap: children.values())
				ap.flush2();
			if(newNode){
				newNode = false;
			}
		}
	}

	protected void sync2() throws BackingStoreException {
		synchronized (lock) {
			if (newNode) {
				parent.children.remove(name);
				return;
			}
			for(AbstractPreferences ap: children.values())
				ap.sync2();
			if(removed){
				removed = false;
			}
		}
	}

	public void forceRemoveNode() {
		parent.children.remove(name);
		removed = true;
	}


}