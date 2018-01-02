package it.pjsoft.reactive.rs;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.ext.MessageContext;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;

@Api(tags = {"generic"})
@Path("generic")
@Produces(MediaType.APPLICATION_JSON)
public interface GenericWebRestService {
	
	@GET
	@Path("command/{cmd}/{pars:.*}")	
	@ApiOperation(value = "Execute a Generic Command", notes = "Return a GenericResponse", response = GenericResponse.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Command not found")})
	Response get(@Context MessageContext messageContext,
			@PathParam("cmd") String cmd, 
			@PathParam("pars") String parsStr);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Execute a Generic Command", notes = "Return a GenericResponse", response = GenericResponse.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Command not found")})
	Response post(@Context MessageContext messageContext, GenericRequest requestMsg);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Execute a Generic Command", notes = "Return a GenericResponse", response = GenericResponse.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Command not found")})
	Response put(GenericRequest request);

	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@ApiOperation(value = "Execute a Generic Command", notes = "Return a GenericResponse", response = GenericResponse.class)
    @ApiResponses({@ApiResponse(code = 404, message = "Command not found")})
	Response delete(GenericRequest request);

}