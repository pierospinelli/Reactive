package it.pjsoft.reactive.generic.transfer.model.util;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import it.pjsoft.reactive.generic.transfer.model.msg.ListBody;
import it.pjsoft.reactive.generic.transfer.model.msg.MessageBody;

@SuppressWarnings("rawtypes")
public class ListBodySerializer extends JsonSerializer<ListBody> {

	// public ListBodySerializer() {
	// super(ListBody.class, false);
	// }

	@SuppressWarnings("unchecked")
	@Override
	public void serialize(ListBody value, JsonGenerator jgen, SerializerProvider sp)
			throws IOException, JsonGenerationException {

		jgen.writeStartObject();

		jgen.writeStringField("@dto", "ListBody");
		List<MessageBody> f = value.getSegments();
		if (f != null && !f.isEmpty()) {
			jgen.writeArrayFieldStart("segment");
			// jgen.writeStartArray();
			for (MessageBody entry : value.getSegments()) {
				jgen.writeObject(entry);
			}
			jgen.writeEndArray();
		}

		jgen.writeEndObject();
	}

	@Override
	public void serializeWithType(ListBody value, JsonGenerator jgen, SerializerProvider provider,
			TypeSerializer typeSer) throws IOException, JsonProcessingException {
		serialize(value, jgen, provider);
	}

}