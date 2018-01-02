/*===========================================================================
 * Licensed Materials - Property of IBM
 * "Restricted Materials of IBM"
 * 
 * IBM SDK, Java(tm) Technology Edition, v7
 * (C) Copyright IBM Corp. 2000, 2010. All Rights Reserved
 *
 * US Government Users Restricted Rights - Use, duplication or disclosure
 * restricted by GSA ADP Schedule Contract with IBM Corp.
 *===========================================================================
 */

/*
 * Copyright (c) 2000, 2010, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package it.pjsoft.reactive.core.preferences.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import it.pjsoft.reactive.core.internal.Base64;
import it.pjsoft.reactive.core.preferences.NodeChangeListener;
import it.pjsoft.reactive.core.preferences.PreferenceChangeListener;

public abstract class AbstractPreferences extends BasePreferences {
	private static final String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * Name relative to parent.
	 */
	protected final String name;

	/**
	 * Absolute path name.
	 */
	protected final String absolutePath;

	/**
	 * Parent node.
	 */
	protected final AbstractPreferences parent;

	/**
	 * Root node.
	 */
	protected final AbstractPreferences root; // Relative to this node

	/**
	 * This field should be <tt>true</tt> if this node did not exist in the
	 * backing store prior to the creation of this object. The field is
	 * initialized to false, but may be set to true by a subclass constructor
	 * (and should not be modified thereafter). This field indicates whether a
	 * node change event should be fired when creation is complete.
	 */
	protected boolean newNode = false;

	protected Map<String, AbstractPreferences> children = new TreeMap<>();
	protected Map<String, String> vals = new TreeMap<>();

	/**
	 * This field is used to keep track of whether or not this node has been
	 * removed. Once it's set to true, it will never be reset to false.
	 */
	protected boolean removed = false;

	/**
	 * Registered preference change listeners.
	 */
//	private PreferenceChangeListener[] prefListeners = new PreferenceChangeListener[0];

	/**
	 * Registered node change listeners.
	 */
//	private NodeChangeListener[] nodeListeners = new NodeChangeListener[0];
	
	Set<Bundle> usedBy = new HashSet<>();

	
	/**
	 * An object whose monitor is used to lock this node. This object is used in
	 * preference to the node itself to reduce the likelihood of intentional or
	 * unintentional denial of service due to a locked node. To avoid deadlock,
	 * a node is <i>never</i> locked by a thread that holds a lock on a
	 * descendant of that node.
	 */
	protected final Object lock = new Object();

	protected AbstractPreferences(AbstractPreferences parent, String name) {
		if (parent == null) {
			if (!name.equals(""))
				throw new IllegalArgumentException("Root name '" + name + "' must be \"\"");
			this.absolutePath = "/";
			root = this;
		} else {
			if (name.indexOf('/') != -1)
				throw new IllegalArgumentException("Name '" + name + "' contains '/'");
			if (name.equals(""))
				throw new IllegalArgumentException("Illegal name: empty string");

			root = parent.root;
			absolutePath = (parent == root ? "/" + name : parent.absolutePath() + "/" + name);
		}
		this.name = name;
		this.parent = parent;
	}


	public String get(String key, String def) {
		if (key == null)
			throw new NullPointerException("Null key");
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");

			String result = null;
			try {
				result = get2(key, def);
			} catch (Exception e) {
				// Ignoring exception causes default to be returned
			}
			return result;
		}
	}

	private String get2(String key, String def) {
		String ret = vals.get(key);
		if(ret==null)
			return def;
		
		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(ret, "{", true);
		
		while(st.hasMoreTokens()){
			String tk = st.nextToken("{");
			if("{".equals(tk)){
				String nn = st.nextToken("#");
				if(!st.hasMoreTokens())
					return ret;
				try {
					if(!systemRoot().nodeExists(nn))
						return ret;
				} catch (BackingStoreException e) {
					return ret;
				}
				Preferences sn = systemRoot().node(nn);
				if(!st.hasMoreTokens())
					return ret;
				st.nextToken("#");
				if(!st.hasMoreTokens())
					return ret;
				String sk = st.nextToken("}");
				String kv = sn.get(sk, null);
				if(kv==null)
					return ret;
				sb.append(kv);
				if(!st.hasMoreTokens())
					return ret;
				st.nextToken("}");
			}else
				sb.append(tk);
		}
		

		return sb.toString();
	}



	public void putInt(String key, int value) {
		put(key, Integer.toString(value));
	}

	public int getInt(String key, int def) {
		int result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}

		return result;
	}

	public void putLong(String key, long value) {
		put(key, Long.toString(value));
	}

	public long getLong(String key, long def) {
		long result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Long.parseLong(value);
		} catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}

		return result;
	}

	public void putBoolean(String key, boolean value) {
		put(key, String.valueOf(value));
	}

	public boolean getBoolean(String key, boolean def) {
		boolean result = def;
		String value = get(key, null);
		if (value != null) {
			if (value.equalsIgnoreCase("true"))
				result = true;
			else if (value.equalsIgnoreCase("false"))
				result = false;
		}

		return result;
	}

	public void putFloat(String key, float value) {
		put(key, Float.toString(value));
	}

	public float getFloat(String key, float def) {
		float result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Float.parseFloat(value);
		} catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}

		return result;
	}

	public void putDouble(String key, double value) {
		put(key, Double.toString(value));
	}

	public double getDouble(String key, double def) {
		double result = def;
		try {
			String value = get(key, null);
			if (value != null)
				result = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			// Ignoring exception causes specified default to be returned
		}

		return result;
	}

	public void putByteArray(String key, byte[] value) {
		put(key, Base64.byteArrayToBase64(value));
	}

	public byte[] getByteArray(String key, byte[] def) {
		byte[] result = def;
		String value = get(key, null);
		try {
			if (value != null)
				result = Base64.base64ToByteArray(value);
		} catch (RuntimeException e) {
			// Ignoring exception causes specified default to be returned
		}

		return result;
	}

	public String[] keys() throws BackingStoreException {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");

			return vals.keySet().toArray(EMPTY_STRING_ARRAY);
		}
	}

	public String[] childrenNames() throws BackingStoreException {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");

			return children.keySet().toArray(EMPTY_STRING_ARRAY);
		}
	}

	// protected final AbstractPreferences[] cachedChildren() {
	// return kidCache.values().toArray(EMPTY_ABSTRACT_PREFS_ARRAY);
	// }

	public Preferences parent() {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");

			return parent;
		}
	}

	public Preferences node(String path) {
		synchronized (lock) {
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			if (path.equals(""))
				return this;
			if (path.equals("/"))
				return root;
			if (path.charAt(0) != '/')
				return node(new StringTokenizer(path, "/", true));
		}

		// Absolute path. Note that we've dropped our lock to avoid deadlock
		return root.node(new StringTokenizer(path.substring(1), "/", true));
	}

	private Preferences node(StringTokenizer path) {
		String token = path.nextToken();
		if (token.equals("/")) // Check for consecutive slashes
			throw new IllegalArgumentException("Consecutive slashes in path");
		synchronized (lock) {
			AbstractPreferences child = children.get(token);
			if (child == null) {
				if (token.length() > MAX_NAME_LENGTH)
					throw new IllegalArgumentException("Node name " + token + " too long");

				child = mkChild(this, token);
				child.newNode = true;
				children.put(token, child);
				ListenerController.enqueueNodeAddedEvent(this, child);

			}
//			child.usedBy.add(FrameworkUtil.getBundle(this.getClass()).)
			if (!path.hasMoreTokens())
				return child;
			path.nextToken(); // Consume slash
			if (!path.hasMoreTokens())
				throw new IllegalArgumentException("Path ends with slash");
			return child.node(path);
		}
	}

	protected abstract AbstractPreferences mkChild(AbstractPreferences abstractPreferences, String token);

	public boolean nodeExists(String path) throws BackingStoreException {
		synchronized (lock) {
			if (path.equals(""))
				return !removed;
			if (removed)
				throw new IllegalStateException("Node has been removed.");
			if (path.equals("/"))
				return true;
			if (path.charAt(0) != '/')
				return nodeExists(new StringTokenizer(path, "/", true));
		}

		// Absolute path. Note that we've dropped our lock to avoid deadlock
		return root.nodeExists(new StringTokenizer(path.substring(1), "/", true));
	}

	/**
	 * tokenizer contains <name> {'/' <name>}*
	 */
	private boolean nodeExists(StringTokenizer path) throws BackingStoreException {
		String token = path.nextToken();
		if (token.equals("/")) // Check for consecutive slashes
			throw new IllegalArgumentException("Consecutive slashes in path");
		synchronized (lock) {
			AbstractPreferences child = children.get(token);
			if (child == null)
				return false;
			if (!path.hasMoreTokens())
				return true;
			path.nextToken(); // Consume slash
			if (!path.hasMoreTokens())
				throw new IllegalArgumentException("Path ends with slash");
			return child.nodeExists(path);
		}
	}



	public String name() {
		return name;
	}

	public String absolutePath() {
		return absolutePath;
	}

	public boolean isUserNode() {
		return root == factory.userRoot();
	}

	public void addPreferenceChangeListener(PreferenceChangeListener pcl) {
//		if (pcl == null)
//			throw new NullPointerException("Change listener is null.");
//		synchronized (lock) {
//			if (removed)
//				throw new IllegalStateException("Node has been removed.");
//
//			// Copy-on-write
//			PreferenceChangeListener[] old = prefListeners;
//			prefListeners = new PreferenceChangeListener[old.length + 1];
//			System.arraycopy(old, 0, prefListeners, 0, old.length);
//			prefListeners[old.length] = pcl;
//		}
//		ListenerController.startEventDispatchThreadIfNecessary();
		
		ListenerController.addPreferenceChangeListener(this.absolutePath(), pcl);
	}

	public void removePreferenceChangeListener(PreferenceChangeListener pcl) {
//		synchronized (lock) {
//			if (removed)
//				throw new IllegalStateException("Node has been removed.");
//			if ((prefListeners == null) || (prefListeners.length == 0))
//				throw new IllegalArgumentException("Listener not registered.");
//
//			// Copy-on-write
//			PreferenceChangeListener[] newPl = new PreferenceChangeListener[prefListeners.length - 1];
//			int i = 0;
//			while (i < newPl.length && prefListeners[i] != pcl)
//				newPl[i] = prefListeners[i++];
//
//			if (i == newPl.length && prefListeners[i] != pcl)
//				throw new IllegalArgumentException("Listener not registered.");
//			while (i < newPl.length)
//				newPl[i] = prefListeners[++i];
//			prefListeners = newPl;
//		}
		
		ListenerController.removePreferenceChangeListener(this.absolutePath(), pcl);

	}



	public void addNodeChangeListener(NodeChangeListener ncl) {
//		if (ncl == null)
//			throw new NullPointerException("Change listener is null.");
//		synchronized (lock) {
//			if (removed)
//				throw new IllegalStateException("Node has been removed.");
//
//			// Copy-on-write
//			if (nodeListeners == null) {
//				nodeListeners = new NodeChangeListener[1];
//				nodeListeners[0] = ncl;
//			} else {
//				NodeChangeListener[] old = nodeListeners;
//				nodeListeners = new NodeChangeListener[old.length + 1];
//				System.arraycopy(old, 0, nodeListeners, 0, old.length);
//				nodeListeners[old.length] = ncl;
//			}
//		}
//		startEventDispatchThreadIfNecessary();
		ListenerController.addNodeChangeListener(this.absolutePath(), ncl);
	}

	public void removeNodeChangeListener(NodeChangeListener ncl) {
//		synchronized (lock) {
//			if (removed)
//				throw new IllegalStateException("Node has been removed.");
//			if ((nodeListeners == null) || (nodeListeners.length == 0))
//				throw new IllegalArgumentException("Listener not registered.");
//
//			// Copy-on-write
//			int i = 0;
//			while (i < nodeListeners.length && nodeListeners[i] != ncl)
//				i++;
//			if (i == nodeListeners.length)
//				throw new IllegalArgumentException("Listener not registered.");
//			NodeChangeListener[] newNl = new NodeChangeListener[nodeListeners.length - 1];
//			if (i != 0)
//				System.arraycopy(nodeListeners, 0, newNl, 0, i);
//			if (i != newNl.length)
//				System.arraycopy(nodeListeners, i + 1, newNl, i, newNl.length - i);
//			nodeListeners = newNl;
//		}
		ListenerController.removeNodeChangeListener(this.absolutePath(), ncl);
	}

	protected AbstractPreferences getChild(String nodeName) throws BackingStoreException {
		synchronized (lock) {
			return children.get(nodeName);
		}
	}

	/**
	 * Returns the absolute path name of this preferences node.
	 */
	public String toString() {
		return (this.isUserNode() ? "User" : "System") + " Preference Node: " + this.absolutePath();
	}

	/**
	 * Implements the <tt>sync</tt> method as per the specification in
	 * {@link Preferences#sync()}.
	 *
	 * <p>
	 * This implementation calls a recursive helper method that locks this node,
	 * invokes syncSpi() on it, unlocks this node, and recursively invokes this
	 * method on each "cached child." A cached child is a child of this node
	 * that has been created in this VM and not subsequently removed. In effect,
	 * this method does a depth first traversal of the "cached subtree" rooted
	 * at this node, calling syncSpi() on each node in the subTree while only
	 * that node is locked. Note that syncSpi() is invoked top-down.
	 *
	 * @throws BackingStoreException
	 *             if this operation cannot be completed due to a failure in the
	 *             backing store, or inability to communicate with it.
	 * @throws IllegalStateException
	 *             if this node (or an ancestor) has been removed with the
	 *             {@link #removeNode()} method.
	 * @see #flush()
	 */
	public void sync() throws BackingStoreException {
		sync2();
	}

	protected abstract void sync2() throws BackingStoreException;

	public void flush() throws BackingStoreException {
		flush2();
	}

	protected abstract void flush2() throws BackingStoreException;

	/**
	 * Returns <tt>true</tt> iff this node (or an ancestor) has been removed
	 * with the {@link #removeNode()} method. This method locks this node prior
	 * to returning the contents of the private field used to track this state.
	 *
	 * @return <tt>true</tt> iff this node (or an ancestor) has been removed
	 *         with the {@link #removeNode()} method.
	 */
	public boolean isRemoved() {
		synchronized (lock) {
			return removed ? true : parent!=null ? parent.isRemoved() : false;
		}
	}





//	/**
//	 * Return this node's preference/node change listeners. Even though we're
//	 * using a copy-on-write lists, we use synchronized accessors to ensure
//	 * information transmission from the writing thread to the reading thread.
//	 */
//	PreferenceChangeListener[] prefListeners() {
//		synchronized (lock) {
//			return prefListeners;
//		}
//	}

//	NodeChangeListener[] nodeListeners() {
//		synchronized (lock) {
//			return nodeListeners;
//		}
//	}



	/**
	 * Implements the <tt>exportNode</tt> method as per the specification in
	 * {@link Preferences#exportNode(OutputStream)}.
	 *
	 * @param os
	 *            the output stream on which to emit the XML document.
	 * @throws IOException
	 *             if writing to the specified output stream results in an
	 *             <tt>IOException</tt>.
	 * @throws BackingStoreException
	 *             if preference data cannot be read from backing store.
	 */
	public void exportNode(OutputStream os) throws IOException, BackingStoreException {
		XmlFormat1Support.export(os, this, false);
	}

	/**
	 * Implements the <tt>exportSubtree</tt> method as per the specification in
	 * {@link Preferences#exportSubtree(OutputStream)}.
	 *
	 * @param os
	 *            the output stream on which to emit the XML document.
	 * @throws IOException
	 *             if writing to the specified output stream results in an
	 *             <tt>IOException</tt>.
	 * @throws BackingStoreException
	 *             if preference data cannot be read from backing store.
	 */
	public void exportSubtree(OutputStream os) throws IOException, BackingStoreException {
		XmlFormat1Support.export(os, this, true);
	}

	public Preferences getRoot() {
		return root;
	}
}
