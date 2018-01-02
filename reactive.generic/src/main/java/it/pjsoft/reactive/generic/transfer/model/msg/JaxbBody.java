package it.pjsoft.reactive.generic.transfer.model.msg;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlMixed;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.pjsoft.reactive.generic.transfer.model.util.JaxbBodyContentAdapter;
import it.pjsoft.reactive.generic.transfer.model.util.JaxbBodyDeserializer;
import it.pjsoft.reactive.generic.transfer.model.util.JaxbBodySerializer;


@XmlType(namespace="http://www.eng.it/reactive/generic")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonSerialize(using = JaxbBodySerializer.class)
@JsonDeserialize(using = JaxbBodyDeserializer.class)
public class JaxbBody extends MessageBody {
	private static final long serialVersionUID = 1L;

	@XmlMixed
	@XmlAnyElement(lax = true)
	@XmlJavaTypeAdapter(JaxbBodyContentAdapter.class)
	private Object content;

	public Object getContent() {
//		if (content == null)
//			return null;
//		if (content instanceof JAXBElement<?>)
//			return ((JAXBElement<?>) content).getValue();

		return content;
	}

	public void setContent(Object content) {
		// if(content != null && content instanceof JAXBElement<?>)
		// this.content = ((JAXBElement<?>)content).getValue();
		// else
		this.content = content;
	}

}