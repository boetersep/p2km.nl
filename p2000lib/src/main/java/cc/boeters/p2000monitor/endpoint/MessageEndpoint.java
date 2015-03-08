package cc.boeters.p2000monitor.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.connector.MonitorConnector;
import cc.boeters.p2000monitor.connector.MonitorEvent;
import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.support.annotation.NewMessage;

@Named
@ServerEndpoint(value = "/messages/live", encoders = { MessageListEncoder.class })
public class MessageEndpoint {

	@Inject
	private MonitorConnector connector;

	private static Set<Session> clients = Collections
			.synchronizedSet(new HashSet<Session>());

	static final Logger LOG = LoggerFactory.getLogger(MessageEndpoint.class);

	@OnClose
	public void onClose(Session session) {
		LOG.debug("Client {} disconnected.", session);
		clients.remove(session);
	}

	public void onMonitorEvent(@Observes @NewMessage MonitorEvent monitorEvent) {
		List<Message> messages = new ArrayList<Message>(1);
		messages.add(monitorEvent.getMessage());
		for (Session session : clients) {
			sendMessages(session, messages);
		}
	}

	@OnOpen
	public void onOpen(Session session) {
		LOG.debug("Client {} connected.", session);
		sendMessages(session, connector.getMessages());
		clients.add(session);
	}

	private void sendMessages(Session session, List<Message> messages) {
		try {
			session.getBasicRemote().sendObject(messages);
		} catch (IOException | EncodeException e) {
			LOG.info("Unable to send message(s) to client: {}.", e.getMessage());
		}
	}

}
