package cc.boeters.p2000decoder.source.listener;

import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;

import cc.boeters.p2000decoder.endpoint.AppWebSocket;
import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.Message;
import cc.boeters.p2000decoder.util.MessageUtil;

public class WebsocketBroadcasterListener implements MonitorListener {

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		Set<Session> clients = AppWebSocket.getClients();
		for (Session session : clients) {
			session.getRemote().sendString(MessageUtil.toDocument(message).toJson());
		}
		return message;
	}
}
