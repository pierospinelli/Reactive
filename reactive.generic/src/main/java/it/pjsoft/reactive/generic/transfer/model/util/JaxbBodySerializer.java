package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBody;

public class JaxbBodySerializer extends JsonSerializer<JaxbBody> {
	private static final DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");
	
	@Override
	public void serialize(JaxbBody jaxbBody, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {


		jg.writeStartObject();
		jg.writeStringField("@dto", "JaxbBody");
		Object cnt = jaxbBody.getContent();
		if (cnt != null) {
		   	if(!(cnt.getClass().isPrimitive() || 
		   			cnt.getClass().isArray() || 
	    			(cnt instanceof String) ||
	    			(cnt instanceof Number) ||
	    			(cnt instanceof Dictionary) ||
	    			(cnt instanceof Map) ||
	    			(cnt instanceof Collection) ||
	    			(cnt instanceof ObjectNode)))
		   			jg.writeStringField("@contentType", cnt.getClass().getName());
		   	if(cnt instanceof Date){
		   		cnt = df.format(cnt);
		   	}else if(cnt instanceof Calendar){
		   		cnt = df.format(((Calendar) cnt).getTime());
		   	}
		    try {
				jg.writeObjectField("content", cnt);
			} catch (Exception e) {
				if(cnt instanceof Serializable){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(cnt);
					oos.close();
		   			jg.writeStringField("@contentType", "SERIALIZED OBJECT");
		   			jg.writeBinaryField("content", baos.toByteArray());
				} else {
					throw new JsonProcessingException(e){};
				}
			}
		} else {
			 jg.writeNullField("content");
		}
		jg.writeEndObject();
	}

	@Override
	public void serializeWithType(JaxbBody value, JsonGenerator jgen, SerializerProvider provider,
			TypeSerializer typeSer) throws IOException, JsonProcessingException {
		serialize(value, jgen, provider);
	}

}