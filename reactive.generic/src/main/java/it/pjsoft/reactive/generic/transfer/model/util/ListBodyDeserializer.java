package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import it.pjsoft.reactive.generic.transfer.model.msg.JaxbBody;
import it.pjsoft.reactive.generic.transfer.model.msg.ListBody;
import it.pjsoft.reactive.generic.transfer.model.msg.MessageBody;

@SuppressWarnings("serial")
	public class ListBodyDeserializer extends StdDeserializer<ListBody> {
		public ListBodyDeserializer() {
			super(ListBody.class);
		}

		@Override
		public ListBody deserialize(JsonParser jp, DeserializationContext ctxt)
				throws IOException, JsonProcessingException {
			ListBody lb = new ListBody();
			JsonToken jt = jp.getCurrentToken();
			String tag = jt.name();
			String jn = jp.getCurrentName();
			jp.nextToken();
			jt = jp.getCurrentToken();
			tag = jt.name();
			jn = jp.getCurrentName();

			if (JsonToken.START_ARRAY.name().equals(tag)) {
				Object[] segs = jp.readValueAs(Object[].class);
				for(Object o: segs){
					if(o instanceof MessageBody)
						lb.getSegments().add((MessageBody)o);
					else{
						JaxbBody bb = new JaxbBody();
						bb.setContent(o);
						lb.getSegments().add(bb);
					}
				}
				jp.nextToken();
			}
			return lb;
		}

//		@Override
//		public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer)
//				throws IOException, JsonProcessingException {
//			return deserialize(jp, ctxt);
//		}
	}