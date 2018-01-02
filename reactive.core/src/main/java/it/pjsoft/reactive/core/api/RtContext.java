package it.pjsoft.reactive.core.api;

import java.io.Closeable;
import java.util.Set;

import it.pjsoft.reactive.core.impl.RtContextImpl;

public interface RtContext extends Closeable{
	final String LAYER_SYSTEM = "reactive.layer.system";
	final String LAYER_INNER_BOUNDARY = "reactive.layer.baundary.inner";
	final String LAYER_OUTER_BOUNDARY = "reactive.layer.baundary.outer";
	final String LAYER_BUSINESS = "reactive.layer.business";

	final String RT_PROTOCOL = "it.eng.ws.gen.protocol"; //SOAP, REST
	final String RT_REST_METHOD = "it.eng.ws.gen.rest.method"; //GET, POST, PUT, DELETE
	final String RT_HTTP_REQUEST = "it.eng.ws.gen.rest.http.request";
	final String RT_REST_PARSED_PARAMETERS = "it.eng.ws.gen.rest.parsedParams";
	final String RT_SOAP_METHOD = "it.eng.ws.gen.soap.method";
	final String RT_SOAP_METHOD_OP = "SOAP_GEN_SIMPLE_OP";
	final String RT_SOAP_METHOD_EXT = "SOAP_GEN_EXTENDED_OP";
	final String RT_REST_JSON_PARS = "it.eng.ws.gen.rest.jsonpars";
	final String RT_REQUEST = "it.eng.ws.gen.cmd.request";
	final String RT_RESPONSE = "it.eng.ws.gen.cmd.response";
	final String RT_LAYER_CMP = "it.eng.ws.gen.layer.component";

	static RtContext open() throws ReactiveException{
		return RtContextImpl.open();
	}

	static RtContext get() {
		return RtContextImpl.get();
	}

	public void close();

	public <T> T getSysAttribute(String key) throws ReactiveException;
	public void setSysAttribute(String key, Object value) throws ReactiveException;
	public void delSysAttribute(String key) throws ReactiveException;
	public Set<String> getSysAttributeNames();

	public <T> T getAttribute(String key) throws ReactiveException;
	public void setAttribute(String key, Object value) throws ReactiveException;
	public void delAttribute(String key) throws ReactiveException;
	public Set<String> getAttributeNames();

	public String getLayer();
	public boolean isSysLevel();

	public <T, I> T executeComponent(String layer, String name, I input) throws ReactiveException;

	public <T, I> T executeMehod(Object target, String method, I pars) throws ReactiveException;


}