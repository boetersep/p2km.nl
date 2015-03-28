package cc.boeters.p2000monitor.processing;

import java.util.Map;

import cc.boeters.p2000monitor.model.Message;

public interface MessageDecomposer {

	Map<String, Object> decompose(Message message);

	String getName();

}
