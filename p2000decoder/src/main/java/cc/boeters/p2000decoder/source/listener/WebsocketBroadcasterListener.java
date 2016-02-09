package cc.boeters.p2000decoder.source.listener;

import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.Message;
import cc.boeters.p2000decoder.util.MessageUtil;

public class WebsocketBroadcasterListener implements MonitorListener {

	private final WebSocketServerFactory webSocketFactory;

	public WebsocketBroadcasterListener(WebSocketServerFactory webSocketFactory) {
		this.webSocketFactory = webSocketFactory;
	}

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		Set<WebSocketSession> clients = webSocketFactory.getOpenSessions();
		for (Session session : clients) {
			if (session.isOpen()) {
				session.getRemote().sendString(MessageUtil.toDocument(message).toJson());
			}
		}
		return message;
	}
}
