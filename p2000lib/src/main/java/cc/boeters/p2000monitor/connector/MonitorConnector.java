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
import cc.boeters.p2000monitor.support.LimitedQueue;
import cc.boeters.p2000monitor.support.annotation.NewMessage;
import cc.boeters.p2000monitor.support.annotation.Property;

@Singleton
public class MonitorConnector {

	enum MessageToken {

		BEGIN("BEGIN MSG"), CAPCODE("Capcode: "), TYPE("Type: "), MSG(
				"Message: "), TIME("Time: "), DATE("Date: "), TIMESTAMP(
				"Timestamp: "), END("END MSG"), UNKNOWN(null);

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

		private String prefix;

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
							LOG.info("Can not close connection.", e);
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

	@Inject
	private CapcodeDatabase capcodeDatabase;

	@Inject
	@NewMessage
	private Event<MonitorEvent> event;

	static final Logger LOG = LoggerFactory.getLogger(MonitorConnector.class);

	private final Deque<Message> messagesQueue;

	private Message currentMessage;

	private final List<CapcodeInfo> group;

	private boolean running;

	public MonitorConnector() {
		messagesQueue = new LimitedQueue<Message>(100);
		group = new ArrayList<CapcodeInfo>(10);
	}

	public List<Message> getMessages() {
		return new ArrayList<Message>(messagesQueue);
	}

	private void handleLine(String line) {

		MessageToken token = MessageToken.resolve(line);
		String val;

		switch (token) {
		case BEGIN:
			currentMessage = new Message();
			break;
		case CAPCODE:
			val = line.split("Capcode: ")[1];
			Integer capcode = Integer.valueOf(val);
			currentMessage.setCapcode(capcode);
			currentMessage.setCapcodeInfo(capcodeDatabase
					.getCapcodeInfo(capcode));

			break;
		case END:
			if (currentMessage.getCapcodeInfo() != null) {
				currentMessage.getGroup().add(currentMessage.getCapcodeInfo());
			}
			if (currentMessage.isGroupMessage()) {
				if (currentMessage.getCapcodeInfo() != null) {
					group.add(currentMessage.getCapcodeInfo());
				}
			} else if (currentMessage.isAlphaMessage()) {
				currentMessage.getGroup().addAll(group);
				group.clear();
				event.fire(new MonitorEvent(currentMessage));
				messagesQueue.add(currentMessage);
			}

			LOG.trace("New message: {}.", currentMessage);
			break;
		case MSG:
			val = line.split(token.prefix)[1];
			currentMessage.setMessage(val);
			break;
		case TIMESTAMP:
			val = line.split(token.prefix)[1];
			currentMessage.setTimestamp(Long.valueOf(val));
			break;
		case TYPE:
			val = line.split(token.prefix)[1];
			currentMessage.setType(Message.MessageType.valueOf(val));
			break;
		case UNKNOWN:
			LOG.warn("Unknown message token [{}].", line);
			break;
		case DATE:
			val = line.split(token.prefix)[1];
			currentMessage.setDate(val);
			break;
		case TIME:
			val = line.split(token.prefix)[1];
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

	@PreDestroy
	private void stop() {
		running = false;
	}

}
