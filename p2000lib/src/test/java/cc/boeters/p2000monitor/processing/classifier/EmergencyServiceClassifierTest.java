package cc.boeters.p2000monitor.processing.classifier;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import cc.boeters.p2000monitor.model.Message;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EmergencyServiceClassifierTest {

	private SearchHit[] messages;

	private ObjectMapper objectMapper;

	private EmergencyServiceClassifier service;

	@Before
	public void setUp() throws JsonParseException, JsonMappingException,
	IOException {
		objectMapper = new ObjectMapper();
		objectMapper.configure(
				DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		service = new EmergencyServiceClassifier();
		messages = objectMapper
				.readValue(getClass().getResourceAsStream("/test.json"),
						SearchHit[].class);
	}

	@Test
	public void test() {

		int s = 0;

		for (SearchHit searchHit : messages) {
			Message message = searchHit.get_source();
			Map<String, Object> decompose = service.decompose(message);

			if (((Collection) decompose.get("service")).isEmpty()) {

				System.out.println(message.getMessage() + ": "
						+ decompose.get("service"));
				s++;
			}

		}
		System.out.println(messages.length + ": " + s);

	}
}
