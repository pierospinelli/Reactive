package it.pjsoft.reactive.core.preferences.internal;

import java.util.Collections;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.Preferences;

import it.pjsoft.reactive.core.preferences.NodeChangeEvent;
import it.pjsoft.reactive.core.preferences.NodeChangeListener;
import it.pjsoft.reactive.core.preferences.PreferenceChangeEvent;
import it.pjsoft.reactive.core.preferences.PreferenceChangeListener;

public final class ListenerController {
	private static final Set<NodeChangeListener> EMPTY_NODE_LISTENER_SET = Collections.unmodifiableSet(Collections.emptyNavigableSet());
	private static final Set<PreferenceChangeListener> EMPTY_PREFS_LISTENER_SET = Collections.unmodifiableSet(Collections.emptyNavigableSet());

	private static final SortedMap<String, Set<NodeChangeListener>> nodeListeners=new TreeMap<>();
	private static final SortedMap<String, Set<PreferenceChangeListener>> prefListeners = new TreeMap<>();

	private static final Map<Bundle, Map<EventListener, Set<String>>> eventListenersXbundle = new HashMap<>();
	
	private static Thread eventDispatchThread = null;

	private static final List<EventObject> eventQueue = new LinkedList<>();
	
	private static void addPerBundle(String path, EventListener pcl) {
		Bundle bundle = FrameworkUtil.getBundle(pcl.getClass());
		if(bundle==null) 
			return;
		
		 Map<EventListener, Set<String>> lm = eventListenersXbundle.get(bundle);
		 if(lm==null) {
			 lm = new HashMap<EventListener, Set<String>>();
			 eventListenersXbundle.put(bundle, lm);
		 }
		 Set<String> ss = lm.get(pcl);
		 if(ss==null) {
			 ss = new HashSet<>();
			 lm.put(pcl, ss);
		 }
		 ss.add(path);
	}
	private static void removePerBundle(String path, EventListener pcl) {
		Bundle bundle = FrameworkUtil.getBundle(pcl.getClass());
		if(bundle==null) 
			return;

		Map<EventListener, Set<String>> lm = eventListenersXbundle.get(bundle);
		 if(lm==null) 
			 return;

		 Set<String> ss = lm.get(pcl);
		 if(ss==null) 
			 return;
		 
		 ss.remove(path);
		 if(ss.isEmpty()) {
			 lm.remove(pcl);			 
			 if(lm.isEmpty())
				 eventListenersXbundle.remove(bundle);
		 }
	}

	public static void removeAllPerBundle(Bundle bundle) {
		if(bundle==null) 
			return;

		Map<EventListener, Set<String>> lm = eventListenersXbundle.remove(bundle);
		 if(lm==null) 
			 return;

		 for(EventListener el: lm.keySet()) {
			 Set<String> ss = lm.get(el);
			 if(ss==null) 
				 continue;

			 for(String path: ss)
				 if(el instanceof NodeChangeListener)
					 removeNodeChangeListener(path, (NodeChangeListener)el);
				 else
					 removePreferenceChangeListener(path, (PreferenceChangeListener)el);
		 }
		 

	}

	public static void addPreferenceChangeListener(String path, PreferenceChangeListener pcl) {
		if (path == null)
			throw new NullPointerException("Node path is null.");
		if (pcl == null)
			throw new NullPointerException("Change listener is null.");
		synchronized (eventListenersXbundle) {
			Set<PreferenceChangeListener> pchls = prefListeners.get(path);
			if(pchls == null) {
				pchls = new HashSet<PreferenceChangeListener>();
				prefListeners.put(path, pchls);
			}
			pchls.add(pcl);
			addPerBundle(path, pcl);
		}
		ListenerController.startEventDispatchThreadIfNecessary();
	}


	public static void removePreferenceChangeListener(String path, PreferenceChangeListener pcl) {
		if (path == null)
			throw new NullPointerException("Node path is null.");
		if (pcl == null)
			throw new NullPointerException("Change listener is null.");
		synchronized (eventListenersXbundle) {
			Set<PreferenceChangeListener> pchls = prefListeners.get(path);
			if(pchls == null)
				return;
			
			pchls.remove(pcl);
			if(pchls.isEmpty())
				prefListeners.remove(path);
			removePerBundle(path, pcl);
		}
	}


	
	public static void addNodeChangeListener(String path, NodeChangeListener ncl) {
		if (path == null)
			throw new NullPointerException("Node path is null.");
		if (ncl == null)
			throw new NullPointerException("Change listener is null.");
		synchronized(eventListenersXbundle) {
			Set<NodeChangeListener> nchls = nodeListeners.get(path);
			if(nchls == null) {
				nchls = new HashSet<NodeChangeListener>();
				nodeListeners.put(path, nchls);
			}
			nchls.add(ncl);
			addPerBundle(path, ncl);
		}
		startEventDispatchThreadIfNecessary();
	}

	public static void removeNodeChangeListener(String path, NodeChangeListener ncl) {
		if (path == null)
			throw new NullPointerException("Node path is null.");
		if (ncl == null)
			throw new NullPointerException("Change listener is null.");
		synchronized(eventListenersXbundle) {
			Set<NodeChangeListener> nchls = nodeListeners.get(path);
			if(nchls == null)
				return;
			
			nchls.remove(ncl);
			if(nchls.isEmpty())
				nodeListeners.remove(path);
			removePerBundle(path, ncl);
		}
	}
	
	public static Set<NodeChangeListener> nodeListeners(AbstractPreferences node) {
		Set<NodeChangeListener> ret = null;
		synchronized (eventListenersXbundle) {
			ret = nodeListeners.get(node.absolutePath());
			if(ret==null)
				ret = EMPTY_NODE_LISTENER_SET;
		}
		return new HashSet<>(ret);
	}

	public static Set<PreferenceChangeListener> prefListeners(AbstractPreferences node) {
		Set<PreferenceChangeListener> ret = null;
		synchronized (eventListenersXbundle) {
			ret = prefListeners.get(node.absolutePath());
			if(ret==null)
				ret = EMPTY_PREFS_LISTENER_SET;
		}
		return new HashSet<>(ret);
	}

	
	/**
	 * Enqueue a preference change event for delivery to registered preference
	 * change listeners unless there are no registered listeners. Invoked with
	 * this.lock held.
	 */
	public static void enqueuePreferenceChangeEvent(Preferences node, String key, String newValue) {
//		if (prefListeners.length != 0) {
			synchronized (eventQueue) {
				eventQueue.add(new PreferenceChangeEvent(node, key, newValue));
				eventQueue.notify();
			}
//		}
	}

	/**
	 * Enqueue a "node added" event for delivery to registered node change
	 * listeners unless there are no registered listeners. Invoked with
	 * this.lock held.
	 */
	public static void enqueueNodeAddedEvent(Preferences parent, Preferences child) {
//		if (nodeListeners.length != 0) {
//		if(!nodeListeners().isEmpty()) {
			synchronized (eventQueue) {
				eventQueue.add(new NodeAddedEvent(parent, child));
				eventQueue.notify();
			}
//		}
	}

	/**
	 * Enqueue a "node removed" event for delivery to registered node change
	 * listeners unless there are no registered listeners. Invoked with
	 * this.lock held.
	 */
	public static void enqueueNodeRemovedEvent(Preferences parent, Preferences child) {
//		if (nodeListeners.length != 0) {
//		if(!nodeListeners().isEmpty()) {
			synchronized (eventQueue) {
				eventQueue.add(new NodeRemovedEvent(parent, child));
				eventQueue.notify();
			}
//		}
	}
	
	
	/**
	 * A single background thread ("the event notification thread") monitors the
	 * event queue and delivers events that are placed on the queue.
	 */
	private static class EventDispatchThread extends Thread {
		public void run() {
			while (true) {
				// Wait on eventQueue till an event is present
				EventObject event = null;
				synchronized (eventQueue) {
					try {
						while (eventQueue.isEmpty())
							eventQueue.wait();
						event = eventQueue.remove(0);
					} catch (InterruptedException e) {
						// XXX Log "Event dispatch thread interrupted. Exiting"
						return;
					}
				}

				// Now we have event & hold no locks; deliver evt to listeners
				AbstractPreferences src = (AbstractPreferences) event.getSource();
				if (event instanceof PreferenceChangeEvent) {
					PreferenceChangeEvent pce = (PreferenceChangeEvent) event;
					Set<PreferenceChangeListener> listeners = prefListeners(src);
					for(PreferenceChangeListener pl: listeners)
						pl.preferenceChange(pce);
				} else {
					NodeChangeEvent nce = (NodeChangeEvent) event;
//					NodeChangeListener[] listeners = src.nodeListeners();
//					if (nce instanceof NodeAddedEvent) {
//						for (int i = 0; i < listeners.length; i++)
//							listeners[i].childAdded(nce);
//					} else {
//						// assert nce instanceof NodeRemovedEvent;
//						for (int i = 0; i < listeners.length; i++)
//							listeners[i].childRemoved(nce);
//					}
					Set<NodeChangeListener> parentListeners = nodeListeners(src);
					Set<NodeChangeListener> childListeners = nodeListeners((AbstractPreferences)((NodeChangeEvent)event).getChild());
					Set<NodeChangeListener> listeners = new HashSet<>(parentListeners);
					listeners.addAll(childListeners);
					if (nce instanceof NodeAddedEvent) {
						for (NodeChangeListener l: listeners)
							l.childAdded(nce);
					} else {
						for (NodeChangeListener l: listeners)
							l.childRemoved(nce);
					}

				}
			}
		}

	}
	/**
	 * This method starts the event dispatch thread the first time it is called.
	 * The event dispatch thread will be started only if someone registers a
	 * listener.
	 */
	public static synchronized void startEventDispatchThreadIfNecessary() {
		if (eventDispatchThread == null) {
			// XXX Log "Starting event dispatch thread"
			eventDispatchThread = new EventDispatchThread();
			eventDispatchThread.setDaemon(true);
			eventDispatchThread.start();
		}
	}

	
	
	/**
	 * These two classes are used to distinguish NodeChangeEvents on eventQueue
	 * so the event dispatch thread knows whether to call childAdded or
	 * childRemoved.
	 */
	public static class NodeAddedEvent extends NodeChangeEvent {
		private static final long serialVersionUID = -6743557530157328528L;

		NodeAddedEvent(Preferences parent, Preferences child) {
			super(parent, child);
		}
	}

	public static class NodeRemovedEvent extends NodeChangeEvent {
		private static final long serialVersionUID = 8735497392918824837L;

		NodeRemovedEvent(Preferences parent, Preferences child) {
			super(parent, child);
		}
	}

}
