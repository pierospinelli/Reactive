
package it.pjsoft.reactive.generic.service.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

import it.pjsoft.reactive.generic.transfer.model.msg.GenericRequest;
import it.pjsoft.reactive.generic.transfer.model.msg.GenericResponse;


@WebService (name="genericWS", 
		targetNamespace="http://www.eng.it/reactive/generic/ws/", 
		wsdlLocation="META-INF/schemas/generic.wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
	it.pjsoft.reactive.generic.transfer.model.msg.ObjectFactory.class,
    it.pjsoft.reactive.generic.service.soap.ObjectFactory.class
})
public interface GenericWS {


    /**
     * 
     * @param requestMsg
     * @return
     *     returns it.eng.reactive.generic.GenericResponse
     */
    @WebMethod(operationName = "GenericOperation", action = "http://www.eng.it/reactive/generic/ws/GenericOperation")
    @WebResult(name = "response", targetNamespace = "http://www.eng.it/reactive/generic", partName = "responseMsg")
    public GenericResponse genericOperation(
        @WebParam(name = "request", targetNamespace = "http://www.eng.it/reactive/generic", partName = "requestMsg")
        GenericRequest requestMsg);

}
