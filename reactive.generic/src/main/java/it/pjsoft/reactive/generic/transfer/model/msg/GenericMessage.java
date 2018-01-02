package it.pjsoft.reactive.generic.transfer.model.msg;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import it.pjsoft.reactive.generic.transfer.model.util.GenericUtil;


@XmlType(namespace="http://www.eng.it/reactive/generic")
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class GenericMessage<H extends MessageHeader> implements Serializable {
	private static final long serialVersionUID = 1L;
	
//	@JsonIgnore
//	private transient Map<String, Object> rtContext = new HashMap<String, Object>();
	
	public abstract H getHeader();
	public abstract void setHeader(H header);
	
	public abstract MessageBody getBody();
	public abstract void setBody(MessageBody body);

//	public Map<String, Object> getRtContext() {
//		return rtContext;
//	}
//	
//	public void setRtContext(Map<String, Object> rtContext) {
//		this.rtContext = rtContext;
//	}
	
	@Override
	public String toString() {
		return GenericUtil.toString(this);
	}
}
