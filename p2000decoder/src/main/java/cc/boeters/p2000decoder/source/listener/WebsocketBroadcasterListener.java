package cc.boeters.p2000decoder.source.listener;

import java.util.Collection;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.message.Message;

public class WebsocketBroadcasterListener implements MonitorListener {

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private final WebSocketServerFactory webSocketFactory;

	public WebsocketBroadcasterListener(WebSocketServerFactory webSocketFactory) {
		this.webSocketFactory = webSocketFactory;
	}

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		Collection<WebSocketSession> clients = webSocketFactory.getOpenSessions();
		String jsonString = JSON_MAPPER.writeValueAsString(message);
		for (Session session : clients) {
			if (session.isOpen()) {
				session.getRemote().sendStringByFuture(jsonString);
			}
		}
		return message;
	}
}
