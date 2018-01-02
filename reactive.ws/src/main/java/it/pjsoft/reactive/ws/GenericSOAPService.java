package it.pjsoft.reactive.ws;

import javax.jws.WebService;

import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;

@WebService
public interface GenericSOAPService {

	GenericResponse execute(GenericRequest request);

}
