package it.pjsoft.reactive.generic.transfer.model.msg;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import it.pjsoft.reactive.generic.transfer.model.util.GenericUtil;

@XmlType(namespace="http://www.eng.it/reactive/generic")
public class Feature implements Serializable{
	private static final long serialVersionUID = 1L;

	@XmlAttribute
	public String key;
	@XmlAttribute
	public String value;

	public Feature() {
	} // Required by JAXB

	public Feature(String key, String value) {
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		return GenericUtil.toString(this);
	}
}
