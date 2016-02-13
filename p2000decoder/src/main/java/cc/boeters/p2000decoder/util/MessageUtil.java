package cc.boeters.p2000decoder.util;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import cc.boeters.p2000decoder.source.model.message.CapcodeInfo;
import cc.boeters.p2000decoder.source.model.message.Message;

public class MessageUtil {

	public static final Document toDocument(Message message) {
		Document document = new Document();
		document.put("capcode", message.getCapcode());
		document.put("capcodeInfo", createCapcodeDocument(message.getCapcodeInfo()));
		document.put("date", message.getDate());
		document.put("group", createGroupDocument(message.getGroup()));
		document.put("message", message.getMessage());
		document.put("time", message.getTime());
		document.put("timestamp", message.getTimestamp());
		document.put("type", message.getType().toString());
		return document;
	}

	private static List<Document> createGroupDocument(List<CapcodeInfo> group) {
		List<Document> documents = new ArrayList<Document>(group.size());
		for (CapcodeInfo capcodeInfo : group) {
			documents.add(createCapcodeDocument(capcodeInfo));
		}
		return documents;
	}

	private static Document createCapcodeDocument(CapcodeInfo capcodeInfo) {
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
