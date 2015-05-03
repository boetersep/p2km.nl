package cc.boeters.p2000monitor.processing.tokenizer;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.processing.MessageDecomposer;

@Singleton
public class EmergencyServiceClassifier implements MessageDecomposer {

	enum Urgency {
		HIGH, MEDIUM, LOW
	}

	private final Map<Pattern, Urgency> PRIO_MAPPINGS = new HashMap<Pattern, EmergencyServiceClassifier.Urgency>();

	private final Pattern PRIO_1 = Pattern
			.compile("A1|A 1|HV1|HV 1|Prio 1|P1|P 1|PR1|PR 1|BR1|BR 1");

	private final Pattern PRIO_2 = Pattern
			.compile("A2|A 2|HV2|HV 2|Prio 2|P2|P 2|PR2|PR 2|BR2|BR 2");
	private final Pattern PRIO_3 = Pattern.compile("A3|A 3|HV3|HV 3|BR3|BR 3");

	public EmergencyServiceClassifier() {

	}

	@Override
	public Map<String, Object> decompose(Message message) {

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("urgency", resolveUrgency(message));
		data.put("service", resolveService(message));

		return null;
	}

	@Override
	public String getName() {
		return "emergency";
	}

	private String resolveService(Message message) {
		// TODO Auto-generated method stub
		return null;
	}

	private Urgency resolveUrgency(Message message) {
		String messageStr = message.getMessage();
		if (PRIO_1.matcher(messageStr).matches()) {
			return Urgency.HIGH;
		} else if (PRIO_2.matcher(messageStr).matches()) {
			return Urgency.MEDIUM;
		} else if (PRIO_3.matcher(messageStr).matches()) {
			return Urgency.LOW;
		}
		return Urgency.LOW;
	}

}
