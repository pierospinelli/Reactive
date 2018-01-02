package it.pjsoft.reactive.ws.impl;

import javax.ws.rs.core.Response.Status;

import org.osgi.service.component.annotations.Component;

import it.pjsoft.reactive.core.api.ReactiveException;
import it.pjsoft.reactive.core.api.RtContext;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;
import it.pjsoft.reactive.generic.transfer.model.msg.ResponseHeader;
import it.pjsoft.reactive.ws.GenericSOAPService;

@Component(immediate = true,  name = "GenericSOAPService", 
    property = { 
      "service.exported.interfaces=*", 
      "service.exported.configs=org.apache.cxf.ws", 
      "org.apache.cxf.ws.address=/genericService" 
    })
public class GenericSOAPServiceImpl implements GenericSOAPService{

	public GenericResponse execute(GenericRequest request) {
		try (RtContext rtctx = RtContext.open();){

			String srvName=request.getHeader().getService();
			rtctx.setSysAttribute(RtContext.RT_PROTOCOL, "SOAP");
			rtctx.setSysAttribute(RtContext.RT_REQUEST, request);

			GenericResponse response = rtctx.executeComponent(RtContext.LAYER_INNER_BOUNDARY, srvName, request);
			return response;
		} catch (ReactiveException e) {
			return mkErrorResponse(request, Status.fromStatusCode(e.getCode()));
		}
	
	}
	
	private GenericResponse mkErrorResponse(GenericRequest request, Status status) {
		GenericResponse response = new GenericResponse();
		ResponseHeader header=new ResponseHeader();
		header.setRequestRef(request.getHeader());
		header.setRetCode(""+status);
		header.setSuccess(false);
		response.setHeader(header);
		return response;
	}

}
