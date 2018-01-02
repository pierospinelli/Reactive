package it.pjsoft.reactive.generic.transfer.model.msg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import it.pjsoft.reactive.generic.transfer.model.util.GenericUtil;

@XmlType(namespace="http://www.eng.it/reactive/generic")
public class Features implements Serializable{
	private static final long serialVersionUID = 1L;

	@XmlElement(name="feature")
	private List<Feature> features=new ArrayList<Feature>();

	public Features() {
	} // Required by JAXB

	public List<Feature> getFeatures() {
		return features;
	}

	@Override
	public String toString() {
		return GenericUtil.toString(this);
	}
}
