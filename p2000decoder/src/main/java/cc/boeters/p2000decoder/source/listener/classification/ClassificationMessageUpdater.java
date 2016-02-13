package cc.boeters.p2000decoder.source.listener.classification;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.eclipse.jetty.websocket.server.WebSocketServerFactory;

import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.source.listener.MessageUpdater;
import cc.boeters.p2000decoder.source.model.message.CapcodeInfo;
import cc.boeters.p2000decoder.source.model.message.Message;
import jersey.repackaged.com.google.common.collect.Sets;

public class ClassificationMessageUpdater extends MessageUpdater {

	public ClassificationMessageUpdater(DataSource dataSource, MongoDatabase db,
			WebSocketServerFactory webSocketFactory) {
		super(dataSource, db, webSocketFactory);
	}

	public enum EmergencyService {
		AMBULANCE, FIREDEPARTMENT, KNRM, LIFELINER, POLICE, TEST, OTHER
	}

	enum Urgency {

		HIGH(Pattern.compile("\\b(A1|A 1|HV1|HV 1|PRIO 1|P1|P 1|PR1|PR 1|BR1|BR 1|1BR|1 BR)\\b",
				Pattern.CASE_INSENSITIVE)), /**/
				LOW(Pattern.compile("\\b(A3|A 3|HV3|HV 3|BR3|BR 3|3BR|3 BR)\\b", Pattern.CASE_INSENSITIVE)), /**/
				MEDIUM(Pattern.compile("\\b(A2|A 2|HV2|HV 2|PRIO 2|P2|P 2|PR2|PR 2|BR2|BR 2|2BR|2 BR)\\b",
						Pattern.CASE_INSENSITIVE));

		public static Urgency resolve(String message) {
			Urgency[] values = values();
			for (Urgency urgency : values) {
				if (urgency.patten.matcher(message).find()) {
					return urgency;
				}
			}
			return LOW;
		}

		private final Pattern patten;

		private Urgency(Pattern patten) {
			this.patten = patten;
		}

	}

	private static final Pattern PATTERN_AMBULANCE_MESSAGE = Pattern
			.compile("\\b(A[1-3]|A [1-3]|B[1-3]|B [1-3]|DIA|AMBU|AMBULANCE)\\b", Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_FIREDEPARTMENT_MESSAGE = Pattern.compile(
			"\\b( HV[1-3]|HV [1-3]|PRIO [1-3]|P[1-3]|P [1-3]|PR[1-3]|PR [1-3]|BR[1-3]|BR [1-3]|[1-3]BR|[1-3] BR|DIB|DV)\\b",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_LIFELINER_MESSAGE = Pattern.compile("\\b(LIFELINER)\\b",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_POLICE_MESSAGE = Pattern.compile("(ONGEVAL|LETSEL|AANRIJDING|POLITIE)",
			Pattern.CASE_INSENSITIVE);

	private static final Pattern PATTERN_TEST_MESSAGE = Pattern
			.compile("\\b(TEST|PROEFALARM|TESTPAGE|TESTOPROEP|TESTPAGING)\\b", Pattern.CASE_INSENSITIVE);

	@Override
	public Map<String, Object> getUpdateData(Message message) {

		Map<String, Object> data = new HashMap<String, Object>();

		data.put("urgency", Urgency.resolve(message.getMessage()).toString());
		data.put("service", resolveServices(message));

		return data;
	}

	@Override
	public String getName() {
		return "emergency";
	}

	private Set<String> resolveServices(Message message) {

		Set<String> emergencyServices = Sets.newLinkedHashSet();

		if (PATTERN_TEST_MESSAGE.matcher(message.getMessage()).find()) {
			emergencyServices.add(EmergencyService.TEST.toString());
		}

		if (PATTERN_LIFELINER_MESSAGE.matcher(message.getMessage()).find()) {
			emergencyServices.add(EmergencyService.LIFELINER.toString());
		}

		if (PATTERN_POLICE_MESSAGE.matcher(message.getMessage()).find()) {
			emergencyServices.add(EmergencyService.POLICE.toString());
		}

		if (PATTERN_AMBULANCE_MESSAGE.matcher(message.getMessage()).find()) {
			emergencyServices.add(EmergencyService.AMBULANCE.toString());
		}

		if (PATTERN_FIREDEPARTMENT_MESSAGE.matcher(message.getMessage()).find()) {
			emergencyServices.add(EmergencyService.FIREDEPARTMENT.toString());
		}

		List<CapcodeInfo> group = message.getGroup();
		for (CapcodeInfo capcodeInfo : group) {
			if (capcodeInfo.getDiscipline() == null) {
				emergencyServices.add(EmergencyService.OTHER.toString());
				continue;
			}

			switch (capcodeInfo.getDiscipline()) {
			case "Politie":
				emergencyServices.add(EmergencyService.POLICE.toString());
				break;
			case "Brandweer":
				emergencyServices.add(EmergencyService.FIREDEPARTMENT.toString());
				break;
			case "KNRM":
				emergencyServices.add(EmergencyService.KNRM.toString());
				break;
			case "Ambulance":
				emergencyServices.add(EmergencyService.AMBULANCE.toString());
				break;
			}
		}

		return emergencyServices;
	}
}
