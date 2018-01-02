package it.pjsoft.reactive.rs.impl;

import java.util.Arrays;
import java.util.List;

import org.apache.cxf.dosgi.common.api.IntentsProvider;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;

/**
 * Not needed in the current example config. This shows how to export a custom
 * intent.
 */
@Component //
(//
		property = "org.apache.cxf.dosgi.IntentName=jackson" //
)
public class JacksonIntent implements IntentsProvider {

	@Override
	public List<?> getIntents() {
		return Arrays.asList((Object) new JacksonJsonProvider());
	}

}