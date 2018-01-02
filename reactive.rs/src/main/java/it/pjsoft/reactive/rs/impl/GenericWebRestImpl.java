package it.pjsoft.reactive.rs.impl;

import static java.util.Arrays.asList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.dosgi.common.api.IntentsProvider;
import org.apache.cxf.jaxrs.ext.MessageContext;
import org.apache.cxf.jaxrs.swagger.Swagger2Feature;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;
import it.pjsoft.reactive.generic.transfer.model.msg.RequestHeader;
import it.pjsoft.reactive.generic.transfer.model.msg.ResponseHeader;
import it.pjsoft.reactive.rs.GenericWebRestService;
import it.pjsoft.reactive.rs.strategy.RestUriStrategy;

@Component(immediate = true, name = "GenericWebRestService", 
property = { 
//		  "service.exported.interfaces=it.pjsoft.reactive.rs.GenericWebRestService", 
  "service.exported.interfaces=*", 
  "service.exported.configs=org.apache.cxf.rs", 
  "org.apache.cxf.rs.address=/reactive",
  "org.apache.cxf.rs.provider=com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider",
  "cxf.bus.prop.skip.default.json.provider.registration=true"
} 
)
@Provider
public class GenericWebRestImpl implements GenericWebRestService, IntentsProvider  {

	private ThreadLocal<MessageContext> messageContextTL=new ThreadLocal<>(); 


	public GenericWebRestImpl() {

	}


	@Override
	public Response get(MessageContext messageContext,
			String cmd, 
			String parsStr) {
		messageContextTL.set(messageContext);
		GenericRequest requestMsg = new GenericRequest();
		requestMsg.setHeader(new RequestHeader());
		return execute(HttpMethod.GET, requestMsg);
    }

	@Override
	public Response post(MessageContext messageContext,
			GenericRequest requestMsg)  {
		messageContextTL.set(messageContext);
		return execute(HttpMethod.POST, requestMsg);
	}

	@Override
	public Response put(GenericRequest request) {
		return mkErrorResponse(request, Status.FORBIDDEN);
	}

	@Override
	public Response delete(GenericRequest request) {
		return mkErrorResponse(request, Status.FORBIDDEN);
	}

	private Response execute(String method, GenericRequest request){
		try (RtContext rtctx = RtContext.open();){
			MessageContext messageContext = messageContextTL.get();

			parseHttp(request, method);

			String srvName=request.getHeader().getService();
			rtctx.setSysAttribute(RtContext.RT_PROTOCOL, "REST");
			rtctx.setSysAttribute(RtContext.RT_REQUEST, request);
			rtctx.setSysAttribute(RtContext.RT_HTTP_REQUEST, messageContext.getHttpServletRequest()); 
			rtctx.setSysAttribute(RtContext.RT_REST_METHOD, method);

			GenericResponse response = rtctx.executeComponent(RtContext.LAYER_INNER_BOUNDARY, srvName, request);
			return WrapResponse(response);
		} catch (ReactiveException e) {
			return mkErrorResponse(request, Status.fromStatusCode(e.getCode()));
		}

	}

	private Response WrapResponse(GenericResponse response) {
		Status status=Status.INTERNAL_SERVER_ERROR;
		if(response==null){
			status = Status.INTERNAL_SERVER_ERROR;
		}else{
			ResponseHeader header = response.getHeader();
			if(header!=null){
				if(header.isSuccess()){
					status = Status.OK;
				}else {
					String rc = header.getRetCode();
					if(rc!=null)
						try {
							int retCode = Integer.parseInt(rc);
							status = Status.fromStatusCode(retCode);
							if(status==null)
								status = Status.INTERNAL_SERVER_ERROR;
						} catch (Exception e) {
							status = Status.INTERNAL_SERVER_ERROR;
						}
				}
			}
		}
		return Response.status(status).entity(response).build();
	}

	private Response mkErrorResponse(GenericRequest request, Status status) {
		GenericResponse response = new GenericResponse();
		ResponseHeader header=new ResponseHeader();
		header.setRequestRef(request.getHeader());
		header.setRetCode(""+status);
		header.setSuccess(false);
		response.setHeader(header);
		return WrapResponse(response);
	}

		
	private void parseHttp(GenericRequest requestMsg, String method) throws ReactiveException {
		RtContext rtctx = RtContext.get();

		MessageContext messageContext = messageContextTL.get();
		HttpServletRequest httpRequest = messageContext.getHttpServletRequest(); 
		
		RestUriStrategy us = RestUriStrategy.get(method);
		Map<String, String> pathParams = us.parsePath(httpRequest);
		
		Map<String, String[]> httpParams = HttpUtil.getParameterMap(httpRequest);
		Map<String, String> httpHeaders = new HashMap<String, String>();
		for(Enumeration<String> it=HttpUtil.getHeaderNames(httpRequest);it.hasMoreElements();){
			String hn=it.nextElement();
			httpHeaders.put(hn, HttpUtil.getHeader(httpRequest, hn));
		}
		Map<String,String> pms = mergeParams(pathParams, httpParams, httpHeaders);
		rtctx.setAttribute(RtContext.RT_REST_PARSED_PARAMETERS, pms);
		completeHeader(requestMsg.getHeader(), pms);
	}

	@SuppressWarnings("unchecked")
	private void completeHeader(RequestHeader header, Map<String, String> params) {
		if(header.getCaller()==null)
			header.setCaller(params.get("caller"));
		if(header.getCallee()==null)
			header.setCallee(params.get("callee"));
		if(header.getCodApplication()==null)
			header.setCodApplication(params.get("codApplication"));
		if(header.getInvocationContext()==null)
			header.setInvocationContext(params.get("invocationContext"));
		if(header.getMethod()==null)
			header.setMethod(params.get("method"));
		if(header.getService()==null)
			header.setService(params.get("service"));
		if(header.getUser()==null)
			header.setUser(params.get("user"));
	}

	private Map<String,String> mergeParams(Map<String, ?>... mps) {
		Map<String,String> ret = new TreeMap<String, String>();
		for(Map<String, ?> m: mps){
			for(Map.Entry<String, ?> e: m.entrySet()){
				Object v=e.getValue();
				if(v==null)
					continue;
				if(v.getClass().isArray()){
					if(Array.getLength(v)==1)
						v=Array.get(v, 0);
					else
						v=Arrays.asList(v);
				} 
				ret.put(e.getKey(), v.toString());
			}
		}
		return ret;
	}

    @Override
    public List<?> getIntents() {
        return asList(createSwaggerFeature(), new JacksonJsonProvider());
    }

	  private Swagger2Feature createSwaggerFeature() {
	        Swagger2Feature swagger = new Swagger2Feature();
	        swagger.setDescription("Generic Command Execution Service");
	        swagger.setTitle("Generic Command Execution Service");
	        swagger.setUsePathBasedConfig(true); // Necessary for OSGi
	        // swagger.setScan(false); // Must be set for cxf < 3.2.x
	        swagger.setPrettyPrint(true);
	        swagger.setSupportSwaggerUi(true);
	        return swagger;
	    }
}
