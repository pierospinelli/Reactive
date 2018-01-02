package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import it.pjsoft.reactive.generic.transfer.model.msg.BinaryBody;

public class BinaryBodyDeserializer extends JsonDeserializer<BinaryBody>{
		public BinaryBodyDeserializer() {}
		
		@Override
		public BinaryBody deserialize(JsonParser jp, DeserializationContext dc)
				throws IOException, JsonProcessingException {
			boolean inFeatures = false;
			BinaryBody bb = new BinaryBody();
			while(jp.hasCurrentToken()){
				JsonToken jt=jp.getCurrentToken();
				String tag = jt.name();
				String jn = jp.getCurrentName();
//				System.out.println(tag+": "+jn);

				if("content".equals(jn) && JsonToken.VALUE_STRING.name().equals(tag)){
					bb.setContent(jp.getBinaryValue());
				}else if("features".equals(jn) && JsonToken.START_OBJECT.name().equals(tag)){
					inFeatures = true;
				}else if("features".equals(jn) && JsonToken.END_OBJECT.name().equals(tag)){
					inFeatures = false;
				}else if(JsonToken.END_OBJECT.name().equals(tag)){
					break;
				} else {
					if(inFeatures && JsonToken.VALUE_STRING.name().equals(tag)){
						@SuppressWarnings("unused")
						String k = jp.getCurrentName();
						String v=jp.getText();
						bb.getFeatures().put(jn, v);
					}
				}
				jp.nextToken();
			}
			return bb;
		}
		
		@Override
		public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
				throws IOException, JsonProcessingException {
			return deserialize(jp, ctxt);
		}
	}