package cc.boeters.p2000monitor.processing.tokenizer;

import java.util.Map;

import javax.inject.Singleton;

import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.processing.MessageDecomposer;

@Singleton
public class MessageTokenizer implements MessageDecomposer {

	@Override
	public Map<String, Object> decompose(Message message) {
		return null;
	}

	@Override
	public String getName() {
		return "tokens";
	}

}
