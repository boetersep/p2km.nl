package cc.boeters.p2000monitor.connector;

import java.util.List;

import cc.boeters.p2000monitor.model.Message;

public interface MonitorConnector {

	List<Message> getMessages();

	void stop();

}
