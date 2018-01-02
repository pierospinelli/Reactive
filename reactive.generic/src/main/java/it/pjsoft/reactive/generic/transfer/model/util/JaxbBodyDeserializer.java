package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBody;

public class JaxbBodyDeserializer extends JsonDeserializer<JaxbBody> {
	private static final DateFormat df = new SimpleDateFormat("yyyy.MM.dd hh:mm:ss");

	@Override
	public JaxbBody deserialize(JsonParser jp, DeserializationContext dc) throws IOException, JsonProcessingException {
		Object content = null;
		Class<?> cc = null;
		while (jp.hasCurrentToken()) {
			JsonToken jt = jp.getCurrentToken();
			String tag = jt.name();
			String jn = jp.getCurrentName();
			// System.out.println(tag+": "+jn);
			if (JsonToken.FIELD_NAME.name().equals(tag)) {
				// DO NOTHING
			} else if ("@contentType".equals(jn)) {
				String t = jp.getText();
				if("SERIALIZED OBJECT".equals(t)){
					cc = Serializable.class;
				}else{
					try {
						cc = Class.forName(t);
					} catch (ClassNotFoundException e) {
						throw new JsonProcessingException(e){};
					}
				}
			} else if ("content".equals(jn)) {
				if (JsonToken.START_ARRAY.name().equals(tag)) {
					if(cc == Serializable.class){
						ByteArrayInputStream bais = new ByteArrayInputStream(jp.getBinaryValue());
						ObjectInputStream ois = new ObjectInputStream(bais);
						try {
							content = ois.readObject();
						} catch (ClassNotFoundException e) {
							throw new JsonProcessingException(e){};
						}
						ois.close();
					}else{
						content = jp.readValueAs(Object[].class);
					}
					jp.nextToken();
					break;
				} else if (JsonToken.START_OBJECT.name().equals(tag)) {
					content = cc!=null ? jp.readValueAs(cc) : jp.readValueAsTree();
					jp.nextToken();
					break;
				} else if (JsonToken.VALUE_NULL.name().equals(tag)) {
					content = null;
					jp.nextToken();
					break;
				} else if (JsonToken.VALUE_STRING.name().equals(tag)) {
					if(cc!=null && Date.class.isAssignableFrom(cc)){
						try {
//							synchronized(df){
							content = df.parse(jp.readValueAs(String.class));
//							}
						} catch (Exception e) {
							throw new JsonProcessingException(e){};
						}
					} else if(cc!=null && Calendar.class.isAssignableFrom(cc)){
						try{
//							synchronized(df){
							Date dt = df.parse(jp.readValueAs(String.class));
							Calendar c = new GregorianCalendar();
							c.setTime(dt);
							content = c;
//							}
						} catch (Exception e) {
							throw new JsonProcessingException(e){};
						}
					} else {
						content = cc!=null ? jp.readValueAs(cc) : jp.readValueAs(String.class);
					}
					jp.nextToken();
					break;
				} else if (JsonToken.VALUE_NUMBER_INT.name().equals(tag)) {
					content = cc!=null ? jp.readValueAs(cc) : jp.readValueAs(Long.class);
					jp.nextToken();
					break;
				} else if (JsonToken.VALUE_NUMBER_FLOAT.name().equals(tag)) {
					content = cc!=null ? jp.readValueAs(cc) : jp.readValueAs(Double.class);
					jp.nextToken();
					break;
				} else if (JsonToken.VALUE_TRUE.name().equals(tag)) {
					content = true;
					jp.nextToken();
					break;
				} else if (JsonToken.VALUE_FALSE.name().equals(tag)) {
					content = false;
					jp.nextToken();
					break;
				}
			}

			jp.nextToken();

		}

		JaxbBody jb = new JaxbBody();
		jb.setContent(content);
		return jb;
	}

	@Override
	public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
			throws IOException, JsonProcessingException {
		return deserialize(jp, ctxt);
	}

}