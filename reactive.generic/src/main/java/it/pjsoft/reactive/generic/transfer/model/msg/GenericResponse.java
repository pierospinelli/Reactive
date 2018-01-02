package it.pjsoft.reactive.generic.transfer.model.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.eng.it/reactive/generic", propOrder={"header", "body"})
public class GenericResponse extends GenericMessage<ResponseHeader>{
	private static final long serialVersionUID = 1L;
		
	@XmlElement(required=true, nillable=false)
	private ResponseHeader header;

	@XmlElement(required=false, nillable=false)
	private MessageBody body;

	public GenericResponse() {

	}

	public GenericResponse(ResponseHeader header, MessageBody body) {
		setHeader(header);
		setBody(body);
	}

	@Override
	public ResponseHeader getHeader() {
		return header;
	}

	@Override
	public void setHeader(ResponseHeader header) {
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
