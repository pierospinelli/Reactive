package it.pjsoft.reactive.generic.transfer.model.msg;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.eng.it/reactive/generic",
	     propOrder={"codApplication", "invocationContext",
		"caller", "callee", "user", "codEnte", "service", "method"})
public class RequestHeader extends MessageHeader implements Cloneable{
	private static final long serialVersionUID = 1L;

	@XmlElement(required=true, nillable=false)
	private String codApplication;
	
	@XmlElement(required=false, nillable=true)
	private String codEnte;

	@XmlElement(required=true, nillable=false)
	private String invocationContext;
	
	@XmlElement(required=true, nillable=false)
	private String caller;

	@XmlElement(required=true, nillable=false)
	private String callee;

	@XmlElement(required=true, nillable=true)
	private String user;

	@XmlElement(required=true, nillable=false)
	private String service;
	
	@XmlElement(required=false, nillable=false)
	private String method;
	
	public String getCodEnte() {
		return codEnte;
	}
	public void setCodEnte(String codEnte) {
		this.codEnte = codEnte;
	}
	public String getCodApplication() {
		return codApplication;
	}
	public void setCodApplication(String codApplication) {
		this.codApplication = codApplication;
	}
	public String getInvocationContext() {
		return invocationContext;
	}
	public void setInvocationContext(String invocationContext) {
		this.invocationContext = invocationContext;
	}
	public String getCaller() {
		return caller;
	}
	public void setCaller(String caller) {
		this.caller = caller;
	}
	public String getCallee() {
		return callee;
	}
	public void setCallee(String callee) {
		this.callee = callee;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getService() {
		return service;
	}
	public void setService(String service) {
		this.service = service;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public RequestHeader clone(){
		RequestHeader rh = new RequestHeader();
		rh.callee=this.callee;
		rh.caller=this.caller;
		rh.codApplication=this.codApplication;
		rh.invocationContext=this.invocationContext;
		rh.method=this.method;
		rh.service=this.service;
		rh.user=this.user;
		rh.setTimestamp(""+System.currentTimeMillis());
		rh.setMsgUid(UUID.randomUUID().toString());
		return rh;
	}
}
