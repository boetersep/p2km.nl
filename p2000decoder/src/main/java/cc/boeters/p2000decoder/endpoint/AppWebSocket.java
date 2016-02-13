package cc.boeters.p2000decoder.endpoint;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

@WebSocket
public class AppWebSocket {

	private Set<Session> clients = Collections.synchronizedSet(new HashSet<Session>());

	@OnWebSocketConnect
	public void onConnect(Session session) {
		clients.add(session);
	}

	@OnWebSocketClose
	public void onClose(Session session, int statusCode, String reason) {
		clients.remove(session);
	}

}