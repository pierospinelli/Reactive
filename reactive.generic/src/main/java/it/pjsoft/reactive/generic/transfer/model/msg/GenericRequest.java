package it.pjsoft.reactive.generic.transfer.model.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.eng.it/reactive/generic", propOrder={"header", "body"})
public class GenericRequest extends GenericMessage<RequestHeader>{
	private static final long serialVersionUID = 1L;

	@XmlElement(required=true, nillable=false)
	private RequestHeader header=new RequestHeader();

	@XmlElement(required=false, nillable=false)
	private MessageBody body;

	@Override
	public RequestHeader getHeader() {
		return header;
	}
	
	@Override
	public void setHeader(RequestHeader header) {
		this.header = header;
	}

	@Override
	public MessageBody getBody() {
		return body;
	}
	
	@Override
	public void setBody(MessageBody body) {
		this.body = body;
	}

	


}


