/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package it.pjsoft.reactive.core.internal;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;
import org.osgi.service.prefs.PreferencesService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import it.pjsoft.reactive.core.preferences.impl.PreferencesServiceImpl;
import it.pjsoft.reactive.core.preferences.internal.AbstractPreferences;
import it.pjsoft.reactive.core.preferences.internal.PreferencesContributor;
import it.pjsoft.reactive.core.preferences.internal.PreferencesFactory;
import it.pjsoft.reactive.core.preferences.internal.ReactivePreferences;
import it.pjsoft.reactive.core.preferences.internal.ReactivePreferencesFactory;

public class Activator implements BundleActivator, ManagedService {
	private static BundleContext context;
	
	static final String preferencesSchema = "schema/preferences.xsd";
	static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
	static final String[] schemas = { preferencesSchema };

	private static final String CONFIG_ID = "it.pjsoft.reactive";
	private ServiceRegistration<ManagedService> configServiceReg;

	private static Map<String, Object> configuration=new HashMap<>();
	
	public static final PreferencesService SERVICE_INSTANCE = new PreferencesServiceImpl();
	private static Set<String> profiles = new HashSet<>();

	private ReactivePreferencesFactory preferencesFactory = new ReactivePreferencesFactory();
	private Map<String, PreferencesContributor> contributors=new HashMap<>();

    public void start(BundleContext context) {
    	this.context = context;
        System.out.println("Starting the bundle "+ context.getBundle().getSymbolicName() +" at "+new Date());
		try {	

			Hashtable<String, Object> properties = new Hashtable<String, Object>();
			properties.put(Constants.SERVICE_PID, CONFIG_ID);
			configServiceReg = (ServiceRegistration<ManagedService>) context.registerService(ManagedService.class.getName(), this, properties);
			
			getContext().addServiceListener(new PrefServiceListener(), "(objectClass="+PreferencesContributor.class.getName()+")");
		} catch (Exception e) {
			e.printStackTrace();
		}

    }

    public void stop(BundleContext context) {
        System.out.println("Stopping the bundle "+ context.getBundle().getSymbolicName() +" at "+new Date());
        configServiceReg.unregister();
        configServiceReg = null;
        this.context = null;
    }

    public static BundleContext getContext() {
		return context;
	}
    
	@Override
	public void updated(Dictionary<String, ?> properties) throws ConfigurationException {
		configuration.clear();
		profiles.clear();
		for(Enumeration<String> en=properties.keys(); en.hasMoreElements();) {
			String k=en.nextElement();
			Object v=properties.get(k);
			if(k.startsWith("profile.")) {
				if(v==null)
					continue;
				String p=v.toString();
				if(!p.startsWith("#"))
					p = "#" + p;
				this.profiles.add(p);
			} else {
				configuration.put(k,  v);
			}
		}
		
		
		initPreferenceService();

		try {
			Set<URL> prefsRess = getConfResouces(); 
			mount(".system/reactive", AbstractPreferences.systemRoot(), getContext().getBundle(), prefsRess);

			ServiceReference<PreferencesContributor>[] contrs = (ServiceReference<PreferencesContributor>[]) getContext().getAllServiceReferences(PreferencesContributor.class.getName(), null);
			if(contrs!=null)
				for(ServiceReference<PreferencesContributor> contr: contrs) {
					PreferencesContributor pc = getContext().getService(contr);
					String bName = (String) contr.getProperty("bundle");
					mount(bName, AbstractPreferences.systemRoot(), 
							contr.getBundle(),
							pc.getResources());
					contributors.put(bName, pc);
				}
		} catch (Exception e) {
			throw new ConfigurationException("general", "load configration failure", e);
		}

	}
	

	private static void initPreferenceService() {
		try {
			ReactivePreferences.doWithBundle(getContext().getBundle(), new Callable<Object>() {
				@Override
				public Object call() throws Exception {
						Preferences prefs = SERVICE_INSTANCE.getSystemPreferences();
						prefs.sync();
						for(String k: prefs.keys())
							prefs.remove(k);
						for(String n: prefs.childrenNames()) {
							Preferences ch = prefs.node(n);
							ch.removeNode();
						}
						Preferences n1 = prefs.node(".system");
						Preferences n2 = n1.node("reactive");
						n2.put("start-tm" , ""+new Date());
						prefs.flush();
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

    
	
	public PreferencesFactory getPreferencesFactory() {
		return preferencesFactory;
	}

	public static Set<String> getProfiles(){
		return profiles;
	}

	public Set<URL> getConfResouces() throws Exception {
		Set<URL> ret = new HashSet<>();
		
		Bundle bundle = getContext().getBundle();
		BundleWiring bw = bundle.adapt(BundleWiring.class);
		ClassLoader bcl = bw.getClassLoader();
		
		Collection<String> res = bw.listResources("conf", "*preferences.xml", BundleWiring.LISTRESOURCES_RECURSE);

		for (String cn : res) {
			URL url = bcl.getResource(cn);
			ret.add(url);
		}					

		return ret;
	}



	private Element mkDom(ClassLoader resourceClassLoader, Set<URL> resources)
			throws SAXException, IOException, ParserConfigurationException, BackingStoreException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setAttribute(JAXP_SCHEMA_SOURCE, schemas);
		Document doc = factory.newDocumentBuilder().newDocument();
		Element rootEl = doc.createElement("root");
		doc.appendChild(rootEl);
		for (URL resource : resources) {
			InputStream is = resource.openStream();
			if (is == null)
				return null;

			Document d = factory.newDocumentBuilder().parse(is);
			Element r = d.getDocumentElement();
			NodeList nl = r.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++)
				rootEl.appendChild(doc.importNode(nl.item(i), true));
		}
		return rootEl;
	}

	private <T> void mount(final String bName, final Preferences prefs, final Bundle bundle,
			Set<URL> resources) throws BackingStoreException, SAXException, IOException, ParserConfigurationException {
		prefs.sync();
		
		ClassLoader resourceClassLoader = bundle.adapt(ClassLoader.class);
		Element rootEl = mkDom(resourceClassLoader, resources);

		try {
			ReactivePreferences.doWithBundle(bundle, new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					mountPrefDescription(bName, prefs, rootEl);
					addPreferences(prefs, rootEl);
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void unmount(final String bName, final Preferences prefs, final Bundle bundle,
			Set<URL> resources) throws SAXException, IOException, ParserConfigurationException, BackingStoreException {
		prefs.sync();

		ClassLoader resourceClassLoader = bundle.adapt(ClassLoader.class);
		Element rootEl = mkDom(resourceClassLoader, resources);
		
		try {
			ReactivePreferences.doWithBundle(bundle, new Callable<Object>() {
				@Override
				public Object call() throws Exception {
//					removePreferences(prefs, rootEl);
					ReactivePreferences.unmountBundle(bundle);
					unmountPrefDescription(bName, prefs, rootEl);
					return null;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
			
	}

	private void mountPrefDescription(String bName, final Preferences prefs, final Element rootEl)
			throws BackingStoreException {
		String id = rootEl.getAttribute("id");
		Preferences mounted = prefs.node(".system").node("mount").node(bName);
		mounted.clear();
		mounted.put("mount-tm", "" + new Date());
		mounted.put("preferences-id", id);
		Preferences custom = mounted.node("properties");
		Preferences contrs = mounted.node("contribution");

		NodeList chs = rootEl.getChildNodes();
		for (int i = 0; i < chs.getLength(); i++) {
			Node n = chs.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element nEl = (Element) n;
				try {
					if ("node".equals(nEl.getLocalName()))
						contrs.put("$$" + i, nEl.getAttribute("node-path"));
					else if ("property".equals(nEl.getLocalName()))
						setProperty(custom, nEl);
				} catch (Exception e) {
					System.out.println("Error: " + e + " setting "+nEl.getLocalName()+" "+nEl.getAttribute("name"));
				}
			}
		}

		prefs.flush();
	}

	private void addPreferences(Preferences prefs, Element rootEl) throws BackingStoreException {
		NodeList chs = rootEl.getChildNodes();
		for (int i = 0; i < chs.getLength(); i++) {
			Node n = chs.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element nEl = (Element) n;
				if ("node".equals(nEl.getLocalName())) {
					String[] nps = nEl.getAttribute("node-path").split("/");
					Preferences tn = prefs;
					for (String np : nps)
						tn = tn.node(np);
					copyNode(tn, nEl);
				}
			}
		}

		prefs.flush();
	}

	private void copyNode(Preferences destNode, Element sourceEl) {
		NodeList chs = sourceEl.getChildNodes();
		for (int i = 0; i < chs.getLength(); i++) {
			Node n = chs.item(i);
			if (n.getNodeType() == Node.ELEMENT_NODE) {
				Element nEl = (Element) n;
				if ("node".equals(nEl.getLocalName())) {
					String nodeName = nEl.getAttribute("name");
					if(nodeName.startsWith("#")){
						if(profiles.contains(nodeName))
							copyNode(destNode, nEl);
					} else {
						Preferences chNode = destNode.node(nodeName);
						copyNode(chNode, nEl);
					}
				} else if ("property".equals(nEl.getLocalName()))
					setProperty(destNode, nEl);
			}
		}
	}

	private void setProperty(Preferences destNode, Element nEl) {
		String type = nEl.getAttribute("type");
		if(type==null || type.trim().length()==0)
			type = "string";

		String k = nEl.getAttribute("name");
		String sv = nEl.getTextContent();
		switch (type) {
		case "boolean":
			destNode.putBoolean(k, Boolean.parseBoolean(sv));
			break;
		case "integer":
			destNode.putInt(k, Integer.parseInt(sv));
			break;
		case "long":
			destNode.putLong(k, Long.parseLong(sv));
			break;
		case "float":
			destNode.putFloat(k, Float.parseFloat(sv));
			break;
		case "double":
			destNode.putDouble(k, Double.parseDouble(sv));
			break;
		case "binaryB64":
			destNode.put(k, sv); // TODO putBinary
			break;
		case "binaryHex":
			destNode.put(k, sv); // TODO putBinary
			break;
		default:
			destNode.put(k, sv);
		}
	}

	private void unmountPrefDescription(String bName, final Preferences prefs, final Element rootEl)
			throws BackingStoreException {
		// String id=rootEl.getAttribute("id");
		ReactivePreferences mounted = (ReactivePreferences)prefs.node(".system").node("mount").node(bName);
		mounted.forceRemoveNode();
		prefs.flush();
	}

//	private void removePreferences(Preferences prefs, Element rootEl) throws BackingStoreException { //TODO: rovedere
//		NodeList chs = rootEl.getChildNodes();
//		for (int i = 0; i < chs.getLength(); i++) {
//			Node n = chs.item(i);
//			if (n.getNodeType() == Node.ELEMENT_NODE) {
//				Element nEl = (Element) n;
//				if ("node".equals(nEl.getLocalName())) {
//					String np = nEl.getAttribute("node-path");
//					if(np!=null) {
//						String[] nps = np.split("/");
//						Preferences tn = prefs;
//						for (String nn : nps)
//							tn = tn.node(nn);
//						removeNodes(tn, nEl);
//					}else {
//						String nodeName = nEl.getAttribute("name");
//						if(nodeName!=null) {
//							if(nodeName.startsWith("#")){
//								if(profiles.contains(nodeName))
//									removePreferences(prefs, nEl);
//							} else {
//								Preferences tn = prefs.node(nodeName);	
//								removeNodes(tn, nEl);
//							}
//						}
//					}
//				} else if ("property".equals(nEl.getLocalName()))
//					prefs.remove(nEl.getAttribute("name"));
//			}
//		}
//
//		prefs.flush();
//	}
//
//	private void removeNodes(Preferences destNode, Element sourceEl) throws BackingStoreException {
//		NodeList chs = sourceEl.getChildNodes();
//		for (int i = 0; i < chs.getLength(); i++) {
//			Node n = chs.item(i);
//			if (n.getNodeType() == Node.ELEMENT_NODE) {
//				Element nEl = (Element) n;
//				if ("node".equals(nEl.getLocalName())) {
//					Preferences chNode = destNode.node(nEl.getAttribute("name"));
//					chNode.removeNode();
//				} else if ("property".equals(nEl.getLocalName()))
//					destNode.remove(nEl.getAttribute("name"));
//			}
//		}
//	}

	
    public class PrefServiceListener implements ServiceListener {
		@Override
		public void serviceChanged(ServiceEvent event) {
			System.out.println("Preference Contributor: " + event.getServiceReference()+"="+event.getType());
			try {
				String bName = (String)event.getServiceReference().getProperty("bundle");
				switch(event.getType()){
				case ServiceEvent.REGISTERED:
					PreferencesContributor pc = (PreferencesContributor) getContext().getService(getContext().getServiceReferences(PreferencesContributor.class, "(bundle="+bName+")").iterator().next());						
					mount(bName, AbstractPreferences.systemRoot(), 
							event.getServiceReference().getBundle(),
							pc.getResources());
					contributors.put(bName, pc);
					break;
				case ServiceEvent.UNREGISTERING:
					PreferencesContributor pc1 = contributors.remove(bName);
					if(pc1!=null)
						unmount(bName, AbstractPreferences.systemRoot(), 
								event.getServiceReference().getBundle(),
								pc1.getResources());
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
    
//    public class ProfileServiceListener implements ServiceListener {
//		@Override
//		public void serviceChanged(ServiceEvent event) {
//			System.out.println("Profile Provider: " + event.getServiceReference()+"="+event.getType());
//			try {
//				String bName = (String)event.getServiceReference().getProperty("bundle");
//				switch(event.getType()){
//				case ServiceEvent.REGISTERED:
//					ProfileProvider pc = (ProfileProvider) getContext().getService(getContext().getServiceReferences(ProfileProvider.class, "(bundle="+bName+")").iterator().next());						
//					loadProfiles(pc);
//					profilers.put(bName, pc);
//					break;
//				case ServiceEvent.UNREGISTERING:
//					ProfileProvider pc1 = profilers.remove(bName);
//					if(pc1!=null)
//						unloadProfiles(pc1);
//					break;
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//

    

}