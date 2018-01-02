package it.pjsoft.reactive.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

import org.apache.cxf.jaxrs.ext.MessageContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(tags = {"command"})
@Consumes({"application/xml", "application/json"})
@Produces({"application/xml", "application/json"})
@Path("command")
public interface CommandService {

	@GET
	@Produces({"text/plain", "text/json"})
	@Path("exec/{cmd}/{pars:.*}")	
	@ApiOperation(value = "Execute Command", notes = "Return a String", response = String.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Command not found")})
	public String execute(@Context MessageContext messageContext, 
			@PathParam("cmd") String cmd, 
			@PathParam("pars") String parsStr) throws Exception;

}