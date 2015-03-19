package cc.boeters.p2000monitor.endpoint;

import java.util.List;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.model.Message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageListEncoder implements Encoder.Text<List<Message>> {

	static final Logger LOG = LoggerFactory.getLogger(MessageListEncoder.class);

	private ObjectMapper objectMapper;

	@Override
	public void destroy() {
		objectMapper = null;
	}

	@Override
	public String encode(List<Message> messages) throws EncodeException {
		try {
			return objectMapper.writeValueAsString(messages);
		} catch (JsonProcessingException e) {
			LOG.error("Unable to marshall message list.", e);
			return null;
		}
	}

	@Override
	public void init(EndpointConfig config) {
		objectMapper = new ObjectMapper();
	}

}
