package cc.boeters.p2000decoder.source.listener;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.Message;
import cc.boeters.p2000decoder.util.MessageUtil;

public class SaveMessageListener implements MonitorListener {

	private MongoDatabase database;

	public SaveMessageListener(MongoDatabase database) {
		this.database = database;
	}

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		Document document = MessageUtil.toDocument(message);
		database.getCollection("messages").insertOne(document);
		return message;
	}

}