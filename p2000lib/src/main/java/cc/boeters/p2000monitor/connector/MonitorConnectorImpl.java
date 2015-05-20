package cc.boeters.p2000monitor.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import javax.annotation.PreDestroy;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.model.CapcodeInfo;
import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.processing.capcode.MysqlCapcodeDatabase;
import cc.boeters.p2000monitor.support.LimitedQueue;
import cc.boeters.p2000monitor.support.annotation.NewMessage;
import cc.boeters.p2000monitor.support.annotation.Property;

@Singleton
public class MonitorConnectorImpl implements MonitorConnector {

	enum MessageToken {

		BEGIN("BEGIN MSG"), CAPCODE("Capcode: "), DATE("Date: "), END("END MSG"), MSG(
				"Message: "), TIME("Time: "), TIMESTAMP("Timestamp: "), TYPE(
				"Type: "), UNKNOWN(null);

		public static MessageToken resolve(String line) {

			MessageToken[] values = MessageToken.values();
			for (MessageToken messageToken : values) {
				if (line != null && messageToken.prefix != null
						&& line.startsWith(messageToken.prefix)) {
					return messageToken;
				}
			}
			return UNKNOWN;
		}

		private final String prefix;

		private MessageToken(String prefix) {
			this.prefix = prefix;
		}

	}

	class MonitorClient implements Runnable {

		private final String host;
		private final int port;

		public MonitorClient(String host, int port) {
			this.host = host;
			this.port = port;
		}

		@Override
		public void run() {
			LOG.info("Connector started.");
			running = true;
			Socket socket = null;
			try {
				while (running) {
					socket = new Socket();
					socket.setSoTimeout(120000);
					try {
						socket.connect(new InetSocketAddress(host, Integer
								.valueOf(port)));
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(socket.getInputStream(),
										"UTF-8"));
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							handleLine(line);
						}
					} catch (SocketException | SocketTimeoutException e) {
						LOG.info("Connection lost, reconnecting: {}",
								e.getMessage());
						try {
							socket.close();
						} catch (IOException e1) {
							LOG.info("Can not close connection.", e1);
						}
					}
					Thread.sleep(1000);
				}
				if (socket != null) {
					socket.close();
				}
				LOG.info("Connector stopped.");
			} catch (UnknownHostException e) {
				LOG.warn("Don't know about host", e);
			} catch (IOException e) {
				LOG.warn("Could not connect to server.", e);
			} catch (InterruptedException e) {
				LOG.info("Connector interrupted.", e);
			}
		}

	}

	private static final String CAPCODE_UNKOWN = "???";

	static final Logger LOG = LoggerFactory.getLogger(MonitorConnector.class);

	@Inject
	private MysqlCapcodeDatabase capcodeDatabase;

	private Message currentMessage;

	@Inject
	@NewMessage
	private Event<MonitorEvent> event;

	private final List<CapcodeInfo> group;

	private final Deque<Message> messagesQueue;

	private boolean running;

	public MonitorConnectorImpl() {
		messagesQueue = new LimitedQueue<Message>(100);
		group = new ArrayList<CapcodeInfo>(10);
	}

	@Override
	public List<Message> getMessages() {
		return new ArrayList<Message>(messagesQueue);
	}

	private String getValue(String line, MessageToken token) {
		String[] split = line.split(token.prefix);
		if (split.length > 1) {
			return split[1];
		}
		return null;
	}

	private void handleLine(String line) {

		MessageToken token = MessageToken.resolve(line);
		String val;

		switch (token) {
		case BEGIN:
			currentMessage = new Message();
			break;
		case CAPCODE:
			val = getValue(line, token);
			if (val == null || val.contains(CAPCODE_UNKOWN))
				break;

			Integer capcode = Integer.valueOf(val);
			currentMessage.setCapcode(capcode);
			currentMessage.setCapcodeInfo(capcodeDatabase
					.getCapcodeInfo(capcode));
			break;
		case END:
			currentMessage.getGroup().add(currentMessage.getCapcodeInfo());
			if (currentMessage.isGroupMessage()) {
				group.add(currentMessage.getCapcodeInfo());
			} else if (currentMessage.isAlphaMessage()) {
				currentMessage.getGroup().addAll(group);
				group.clear();
				event.fire(new MonitorEvent(currentMessage));
				messagesQueue.add(currentMessage);
			}

			LOG.trace("New message: {}.", currentMessage);
			break;
		case MSG:
			val = getValue(line, token);
			currentMessage.setMessage(val);
			break;
		case TIMESTAMP:
			val = getValue(line, token);
			currentMessage.setTimestamp(Long.valueOf(val));
			break;
		case TYPE:
			val = getValue(line, token);
			currentMessage.setType(Message.MessageType.valueOf(val));
			break;
		case UNKNOWN:
			LOG.warn("Unknown message token [{}].", line);
			break;
		case DATE:
			val = getValue(line, token);
			currentMessage.setDate(val);
			break;
		case TIME:
			val = getValue(line, token);
			currentMessage.setTime(val);
			break;
		}
	}

	@Inject
	private void start(@Property("host") String host,
			@Property("port") String port) {

		Thread t = new Thread(new MonitorClient(host, Integer.valueOf(port)),
				String.format("Monitor client %s:%s", host, port));
		t.start();
	}

	@Override
	@PreDestroy
	public void stop() {
		running = false;
	}

}
