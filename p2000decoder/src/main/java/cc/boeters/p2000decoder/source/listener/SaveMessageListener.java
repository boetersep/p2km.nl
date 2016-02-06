package cc.boeters.p2000decoder.source.listener;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.CapcodeInfo;
import cc.boeters.p2000decoder.source.model.Message;

public class SaveMessageListener implements MonitorListener {

	private MongoDatabase database;

	public SaveMessageListener(MongoDatabase database) {
		this.database = database;
	}

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		Document document = new Document();
		document.put("capcode", message.getCapcode());
		document.put("capcodeInfo", createCapcodeDocument(message.getCapcodeInfo()));
		document.put("date", message.getDate());
		document.put("group", createGroupDocument(message.getGroup()));
		document.put("message", message.getMessage());
		document.put("time", message.getTime());
		document.put("timestamp", message.getTimestamp());
		document.put("type", message.getType().toString());
		database.getCollection("messages").insertOne(document);
		return message;
	}

	private List<Document> createGroupDocument(List<CapcodeInfo> group) {
		List<Document> documents = new ArrayList<Document>(group.size());
		for (CapcodeInfo capcodeInfo : group) {
			documents.add(createCapcodeDocument(capcodeInfo));
		}
		return documents;
	}

	private Document createCapcodeDocument(CapcodeInfo capcodeInfo) {
		Document document = new Document();
		document.put("capcode", capcodeInfo.getCapcode());
		document.put("description", capcodeInfo.getDescription());
		document.put("discipline", capcodeInfo.getDiscipline());
		document.put("region", capcodeInfo.getRegion());
		document.put("sector", capcodeInfo.getSector());
		document.put("shortdesc", capcodeInfo.getShortdesc());
		return document;
	}
}