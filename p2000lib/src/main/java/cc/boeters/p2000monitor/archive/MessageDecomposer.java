package cc.boeters.p2000monitor.archive;

import java.util.Map;

import cc.boeters.p2000monitor.model.Message;

public interface MessageDecomposer {

	Map<String, Object> decompose(Message message);

}
