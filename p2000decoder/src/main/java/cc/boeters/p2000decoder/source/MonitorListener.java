package cc.boeters.p2000decoder.source;

import cc.boeters.p2000decoder.source.model.Message;

public interface MonitorListener {

	void onNewMessage(Message message);

}
