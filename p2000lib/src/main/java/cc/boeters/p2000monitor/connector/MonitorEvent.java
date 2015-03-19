package cc.boeters.p2000monitor.connector;

import cc.boeters.p2000monitor.model.Message;

public class MonitorEvent {

	private final Message message;

	public MonitorEvent(Message message) {
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}
}
