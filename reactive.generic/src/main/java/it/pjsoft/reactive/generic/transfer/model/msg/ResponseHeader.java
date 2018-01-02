package it.pjsoft.reactive.generic.transfer.model.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.eng.it/reactive/generic",
		propOrder={"success", "retCode", "message", "requestRef"})
public class ResponseHeader extends MessageHeader{
	private static final long serialVersionUID = 1L;

	@XmlElement(required=true, nillable=false)
	private boolean success;

	@XmlElement(required=false, nillable=false)
	private String retCode;

	@XmlElement(required=false, nillable=false)
	private String message;

	@XmlElement(required=true, nillable=false)
	private RequestHeader requestRef;
	
	public ResponseHeader(){
		
	}
	
	public ResponseHeader(RequestHeader requestRef){
		this.requestRef = requestRef;
	}

	public ResponseHeader(RequestHeader requestRef, boolean success, String retCode, String message){
		this.requestRef = requestRef;
		this.success = success;
		this.retCode = retCode;
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getRetCode() {
		return retCode;
	}
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public RequestHeader getRequestRef() {
		return requestRef;
	}
	public void setRequestRef(RequestHeader requestRef) {
		this.requestRef = requestRef;
	}
	
	
}
