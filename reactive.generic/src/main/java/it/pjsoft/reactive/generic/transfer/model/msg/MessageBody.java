package it.pjsoft.reactive.generic.transfer.model.msg;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import it.pjsoft.reactive.generic.transfer.model.util.GenericUtil;


@XmlType(namespace="http://www.eng.it/reactive/generic")
@XmlAccessorType(XmlAccessType.FIELD)
//@JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="@dto")
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@dto")
@JsonSubTypes({
    @JsonSubTypes.Type(value=ListBody.class, name="ListBody"),
    @JsonSubTypes.Type(value=JaxbBody.class, name="JaxbBody"),
    @JsonSubTypes.Type(value=BinaryBody.class, name="BinaryBody")
})
public abstract class MessageBody implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Override
	public String toString() {
		return GenericUtil.toString(this);
	}
}
