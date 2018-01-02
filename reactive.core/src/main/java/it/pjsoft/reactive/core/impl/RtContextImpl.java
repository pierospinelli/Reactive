package it.pjsoft.reactive.core.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.Callable;

import org.osgi.framework.ServiceReference;

import it.pjsoft.reactive.core.api.ReactiveComponent;
import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.core.internal.Activator;
import it.pjsoft.reactive.core.internal.ReactiveMethodExecuterHelper;
import it.pjsoft.reactive.core.spi.RtContextCtrl;

public class RtContextImpl implements RtContext {
	static RtContextCtrl ctrl = new RtContextCtrlDefaultImpl(); //TODO: aggiungere un tracker che sostituisca con un servizio non di default se presente
	private static ThreadLocal<RtContextImpl> ctxs = new ThreadLocal<RtContextImpl>();

//	private ReactiveDispatcher dispatcher = null;

	private Map<String, Object> sysPars = new HashMap<String, Object>();
	private Map<String, Object> rtPars = new HashMap<String, Object>();
	private Stack<String> layerStack = new Stack<String>();
	private Stack<Object> objStack = new Stack<Object>();
	private boolean sysLevel = true;
	

	private RtContextImpl() throws ReactiveException{
//		dispatcher = ReactiveDispatcher.getInstance();
	}

	
//	static{
//		try{
//			String ctrlTp=getConfigProperty("reactive.rtcontext.ctrl.impl");
//			if(ctrlTp!=null){
//				Class<RtContextCtrl> ctrlClz = (Class<RtContextCtrl>) Class.forName(ctrlTp);
//				RtContextCtrl ctrl = ctrlClz.newInstance();
//				RtContextImpl.setCtrl(ctrl);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			//TODO
//		}
//	}

//	protected static String getConfigProperty(String key) {
//	return ReactiveConfig.getProperty(key);
//}
//
//protected static String getConfigPreference(String node, String key) {
//	return ReactiveConfig.getPreference(node, key);
//}

	static void setCtrl(RtContextCtrl ctxCtrl) {
		ctrl = ctxCtrl;
	}

	void riseSysLevel(){
		sysLevel = true;
	}

	void dropSysLevel(){
		sysLevel = false;
	}
	
	@Override
	public boolean isSysLevel(){
		return sysLevel;
	}
	
//	private String creator=null;
	public static RtContext open() throws ReactiveException{
		if(ctxs.get()!=null)
			throw new ReactiveException("RtContextImpl double initialization");
		RtContextImpl rtctx=new RtContextImpl();
		ctxs.set(rtctx);
		return rtctx;
	}

	@Override
	public void close() {
		RtContextImpl rtctx=ctxs.get();
//		if(rtctx==null)
//			throw new ReactiveException("RtContextImpl not initialized");
		if(!rtctx.isSysLevel())
			throw new RuntimeException("RtContextImpl cannot be close by business code");
		ctxs.remove();
	}
	
	public static RtContext get(){
		return ctxs.get();
	}

	@Override
	public <T> T getSysAttribute(String key) throws ReactiveException{
		Object value=sysPars.get(key);
		attributeCtrl(RtContextCtrl.SYS_COLLECTION, RtContextCtrl.READ_OP, key, value);
		return (T) value;
	}

	@Override
	public void setSysAttribute(String key, Object value) throws ReactiveException{
		attributeCtrl(RtContextCtrl.SYS_COLLECTION, RtContextCtrl.WRITE_OP, key, value);
		sysPars.put(key, value);
	}

	@Override
	public void delSysAttribute(String key) throws ReactiveException{
		Object value = sysPars.get(key);
		attributeCtrl(RtContextCtrl.SYS_COLLECTION, RtContextCtrl.DEL_OP, key, value);
		if(value!=null)
			sysPars.remove(key);
	}
	
	@Override
	public <T> T getAttribute(String key) throws ReactiveException{
		Object value=rtPars.get(key);
		attributeCtrl(RtContextCtrl.RT_COLLECTION, RtContextCtrl.READ_OP, key, value);
		return (T) value;
	}

	@Override
	public void setAttribute(String key, Object value) throws ReactiveException{
		attributeCtrl(RtContextCtrl.RT_COLLECTION, RtContextCtrl.WRITE_OP, key, value);
		rtPars.put(key, value);
	}

	@Override
	public void delAttribute(String key) throws ReactiveException{
		Object value = rtPars.get(key);
		attributeCtrl(RtContextCtrl.RT_COLLECTION, RtContextCtrl.DEL_OP, key, value);
		if(value!=null)
			rtPars.remove(key);
	}

	private void attributeCtrl(String collection, String operation, String key, Object value) throws ReactiveException {
		if(sysLevel)
			return;

		ctrl.checkAttribute(this, collection, operation, key, value); 		
				
	}

	void pushLayer(String layer, Object current){
		layerStack.push(layer);
		objStack.push(current);
	}
	
 	void popLayer(){
		layerStack.pop();
		objStack.pop();
	}
	
 	@Override
	public String getLayer(){
 		if(layerStack.isEmpty())
 			return null;
 		return layerStack.peek();
 	}
 	
 	public Object getExecutingObject(){
 		return objStack.peek();
 	}

	@Override
	public <T, I> T executeComponent(String layer, String name, I input) throws ReactiveException {
		try {
			ServiceReference[] sr = Activator.getContext().getServiceReferences(
					ReactiveComponent.class.getName(), 
					"(&(component.name="+name+")(component.qualifier=reactive)(component.layer="+layer+"))");
			if(sr==null || sr.length==0)
				throw new ReactiveException(400, "ERROR: no component found for "+name);


			ReactiveComponent<T, I> component = (ReactiveComponent<T,I>)Activator.getContext().getService(sr[0]);
			ctrl.checkCall(this, layer, name, component, input);

			try {
				pushLayer(layer, component);
				return runBusinessCode(new Callable<T>() {
					@Override
					public T call() throws Exception {
						return component.execute(input);
					}
				});
			} finally{
				popLayer();
			}
		} catch (ReactiveException e) {
			throw e;
		} catch (Exception e) {
			throw new ReactiveException(500, e);
		}
	}
 	
	public <T> T runBusinessCode(Callable<T> bizzCode)  throws ReactiveException{
		boolean sl = isSysLevel();
		try{
			if(sl)
				dropSysLevel();
			try {
				return (T) bizzCode.call();
			} catch (ReactiveException e) {
				throw e;
			} catch (Exception e) {
				throw new ReactiveException(e);
			}
		}finally {
			if(sl)
				riseSysLevel();
		}
	}


	@Override
	public Set<String> getAttributeNames() {
		return rtPars.keySet();
	}

	@Override
	public Set<String> getSysAttributeNames() {
		return sysPars.keySet();
	}

	@Override
	public <T, I> T executeMehod(Object target, String method, I pars) throws ReactiveException {
		return ReactiveMethodExecuterHelper.execute(target, method, pars);
	}

}
