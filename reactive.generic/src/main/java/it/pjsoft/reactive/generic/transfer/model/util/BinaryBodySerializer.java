package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import it.pjsoft.reactive.generic.transfer.model.msg.BinaryBody;

public class BinaryBodySerializer extends JsonSerializer<BinaryBody>{
	public BinaryBodySerializer() {}
	
	@Override
	public void serialize(BinaryBody bb, JsonGenerator jg, SerializerProvider sp)
			throws IOException, JsonProcessingException {
		jg.writeStartObject();
		
		jg.writeStringField("@dto", "BinaryBody");
		Map<String,String> f=bb.getFeatures();
		if(f!=null && !f.isEmpty()){
			jg.writeObjectFieldStart("features");
			for(Map.Entry<String, String> e: bb.getFeatures().entrySet())
				jg.writeStringField(e.getKey(), e.getValue());
			jg.writeEndObject();
		}

		if(bb.getContent()!=null)
			jg.writeBinaryField("content", bb.getContent());
		
		jg.writeEndObject();
	}
	
	@Override
	public void serializeWithType(BinaryBody value, JsonGenerator jgen, SerializerProvider provider,
			TypeSerializer typeSer) throws IOException, JsonProcessingException {
		serialize(value, jgen, provider);
	}
}