package cc.boeters.p2000decoder.source;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000decoder.source.model.CapcodeInfo;
import cc.boeters.p2000decoder.source.model.Message;

public class TcpIpMonitorSource implements MonitorSource {

	enum MessageToken {

		BEGIN("BEGIN MSG"), CAPCODE("Capcode: "), DATE("Date: "), END("END MSG"), MSG("Message: "), TIME(
				"Time: "), TIMESTAMP("Timestamp: "), TYPE("Type: "), UNKNOWN(null);

		public static MessageToken resolve(String line) {

			MessageToken[] values = MessageToken.values();
			for (MessageToken messageToken : values) {
				if (line != null && messageToken.prefix != null && line.startsWith(messageToken.prefix)) {
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
						socket.connect(new InetSocketAddress(host, Integer.valueOf(port)));
						BufferedReader bufferedReader = new BufferedReader(
								new InputStreamReader(socket.getInputStream(), "UTF-8"));
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							handleLine(line);
						}
					} catch (SocketException | SocketTimeoutException e) {
						LOG.info("Connection lost, reconnecting: {}", e.getMessage());
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

	static final Logger LOG = LoggerFactory.getLogger(MonitorSource.class);

	private Message currentMessage;

	private final List<CapcodeInfo> group;

	private boolean running;

	private List<MonitorListener> listeners;

	private final CapcodeDatabase capcodeDatabase;

	private final String host;
	private final int port;

	public TcpIpMonitorSource(CapcodeDatabase capcodeDatabase, String host, int port) {
		this.capcodeDatabase = capcodeDatabase;
		this.host = host;
		this.port = port;
		listeners = new ArrayList<MonitorListener>();
		group = new ArrayList<CapcodeInfo>(10);

	}

	@Override
	public void start() {
		Thread t = new Thread(new MonitorClient(host, port), String.format("Monitor client %s:%s", host, port));
		t.start();
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
			currentMessage.setCapcodeInfo(capcodeDatabase.getCapcodeInfo(capcode));
			break;
		case END:
			currentMessage.getGroup().add(currentMessage.getCapcodeInfo());
			if (currentMessage.isGroupMessage()) {
				group.add(currentMessage.getCapcodeInfo());
			} else if (currentMessage.isAlphaMessage()) {
				currentMessage.getGroup().addAll(group);
				group.clear();

				Message newMessage = currentMessage;
				for (MonitorListener listener : listeners) {
					try {
						newMessage = listener.onNewMessage(newMessage);
					} catch (Throwable e) {
						LOG.warn("Exception while invoking onNewMessage.", e);
					}
				}
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

	@Override
	public void stop() {
		running = false;
	}

	@Override
	public void addListener(MonitorListener listener) {
		listeners.add(listener);
	}

}
