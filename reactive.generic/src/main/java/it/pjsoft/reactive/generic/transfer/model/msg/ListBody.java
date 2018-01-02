package it.pjsoft.reactive.generic.transfer.model.msg;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace="http://www.eng.it/reactive/generic")
@XmlAccessorType(XmlAccessType.FIELD)
//@JsonSerialize(using = ListBodySerializer.class)
//@JsonDeserialize(using=ListBodyDeserializer.class)
public class ListBody extends MessageBody {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "segment")
//	@JsonProperty("segment")
	private List<MessageBody> segments = new ArrayList<MessageBody>();

	public List<MessageBody> getSegments() {
		return segments;
	}
}
