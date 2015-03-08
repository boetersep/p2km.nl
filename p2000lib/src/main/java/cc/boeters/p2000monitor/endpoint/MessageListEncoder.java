package cc.boeters.p2000monitor.endpoint;

import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import cc.boeters.p2000monitor.model.CapcodeInfo;
import cc.boeters.p2000monitor.model.Message;

public class MessageListEncoder implements Encoder.Text<List<Message>> {

	@Override
	public void destroy() {
	}

	@Override
	public String encode(List<Message> messages) throws EncodeException {
		JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for (Message message : messages) {
			JsonObjectBuilder jsonMsg = Json.createObjectBuilder()
					.add("capcode", message.getCapcode())
					.add("date", message.getDate())
					.add("message", message.getMessage())
					.add("time", message.getTime())
					.add("timestamp", message.getTimestamp())
					.add("type", message.getType().toString());

			if (!message.getGroup().isEmpty()) {
				JsonArrayBuilder groupBuilder = Json.createArrayBuilder();
				for (CapcodeInfo capcode : message.getGroup()) {
					groupBuilder.add(Json.createObjectBuilder()
							.add("discipline", capcode.getDiscipline())
							.add("region", capcode.getRegion())
							.add("sector", capcode.getSector())
							.add("description", capcode.getDescription())
							.add("shortdesc", capcode.getShortdesc()));
				}
				jsonMsg.add("group", groupBuilder);
			}
			arrayBuilder.add(jsonMsg);
		}

		return arrayBuilder.build().toString();
	}

	@Override
	public void init(EndpointConfig config) {
	}

}
