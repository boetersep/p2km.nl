package cc.boeters.p2000decoder.source.listener;

import java.util.Set;

import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.Message;

public class WebsocketBroadcasterListener implements MonitorListener {

	private final WebSocketServerFactory wssf;

	public WebsocketBroadcasterListener(WebSocketServerFactory wssf) {
		this.wssf = wssf;
	}

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		Set<WebSocketSession> openSessions = wssf.getOpenSessions();
		for (WebSocketSession webSocketSession : openSessions) {
			webSocketSession.getRemote().sendString(message.toString());
		}
		return message;
	}
}
