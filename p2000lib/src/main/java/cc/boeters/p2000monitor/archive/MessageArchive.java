package cc.boeters.p2000monitor.archive;

import cc.boeters.p2000monitor.model.Message;

public interface MessageArchive {

	Message retrieveMessage(String hash);

	String storeMessage(Message message);

}
