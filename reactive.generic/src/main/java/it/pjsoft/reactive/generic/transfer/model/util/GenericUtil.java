package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GenericUtil {
	private static ThreadLocal<Boolean> xmlMode = new ThreadLocal<>();
	private static ThreadLocal<JAXBContext> jaxbCtx = new ThreadLocal<>();
	private static ObjectMapper om = new ObjectMapper();

	public static void setXmlMode(boolean mode){
		xmlMode.set(mode);
	}

	public static void setJaxbContext(JAXBContext ctx){
		jaxbCtx.set(ctx);
	}

	public static boolean getXmlMode(){
		Boolean mode = xmlMode.get();
		if(mode==null)
			mode = false;
		return mode;
	}

	public static String toString(Object obj){
		if(obj==null)
			return null;
		if(getXmlMode()){
			return toXML(obj);
		}
		return toJSON(obj);
	}

	public static String toJSON(Object obj){
		if(obj==null)
			return null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			baos.write(("\""+obj.getClass().getSimpleName()+"\": ").getBytes());
			om.writeValue(baos, obj);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return new String(baos.toByteArray());
	}

	public static String toXML(Object obj) {
		if(obj==null)
			return null;

		ByteArrayOutputStream baos;
		try {
			
			JAXBContext ctx = jaxbCtx.get();
			if(ctx==null)
				ctx = JAXBContext.newInstance(obj.getClass().getPackage().getName());
			
			baos = new ByteArrayOutputStream();		
			ctx.createMarshaller().marshal(obj, baos);
			baos.close();
		} catch (JAXBException | IOException e) {
			throw new RuntimeException(e);
		}

		return new String(baos.toByteArray());
	}

}
