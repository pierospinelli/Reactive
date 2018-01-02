package it.pjsoft.reactive.generic.transfer.model.msg;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import it.pjsoft.reactive.generic.transfer.model.util.FeaturesAdapter;
import it.pjsoft.reactive.generic.transfer.model.util.GenericUtil;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(namespace="http://www.eng.it/reactive/generic", propOrder={"msgUid", "timestamp", "metadata"})
public abstract class MessageHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(required=true, nillable=false)
	private String msgUid;
	
	@XmlElement(required=true, nillable=false)
	private String timestamp;
	
//	@XmlElement(required=false, nillable=true)
	@XmlJavaTypeAdapter(FeaturesAdapter.class)
	private Map<String, String> metadata = new TreeMap<>();
	
	public MessageHeader(){
		msgUid = UUID.randomUUID().toString();
		timestamp = ""+System.currentTimeMillis();
	}
	
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	public String getMsgUid() {
		return msgUid;
	}
	public void setMsgUid(String msgUid) {
		this.msgUid = msgUid;
	}
	
	public Map<String, String> getMetadata(){
		if(metadata==null)
			metadata= new HashMap<String, String>();
		return metadata;
	}

	@Override
	public String toString() {
		return GenericUtil.toString(this);
	}
}
