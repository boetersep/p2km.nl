package cc.boeters.p2000decoder.source;

import cc.boeters.p2000decoder.source.model.message.Message;

public interface MonitorListener {

	Message onNewMessage(Message message) throws Throwable;

}
