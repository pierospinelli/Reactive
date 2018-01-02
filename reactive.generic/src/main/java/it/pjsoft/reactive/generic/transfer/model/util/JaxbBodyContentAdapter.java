package it.pjsoft.reactive.generic.transfer.model.util;

import java.util.Date;

import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBodyPayload;

public class JaxbBodyContentAdapter extends XmlAdapter<Object, Object> {
    public JaxbBodyContentAdapter() {
    }

    public Object unmarshal(Object coded) throws Exception {
    	if(coded==null)
    		return null;

       	if(coded instanceof JaxbBodyPayload){
       		return ((JaxbBodyPayload)coded).getValue();
      	}else if(coded.getClass().getAnnotation(XmlRootElement.class)!=null){
    		return coded;
    	}else if(coded.getClass().getAnnotation(XmlElementDecl.class)!=null){
        	return coded;
    	}
        return coded.toString(); //TODO
    }

    public Object marshal(Object plain) throws Exception {
    	if(plain==null)
    		return null;
    	
    	if(plain.getClass().isPrimitive() || 
    			(plain instanceof String) ||
    			(plain instanceof Number) ||
    			(plain instanceof Date)){
    		return new JaxbBodyPayload(plain);
//      	}else if(plain.getClass().getAnnotation(XmlRootElement.class)!=null){
//    		return plain;
//    	}else if(plain.getClass().getAnnotation(XmlElementDecl.class)!=null){
//        		return plain;
       	}else{
    		return plain; //TODO
    	}
    }
}