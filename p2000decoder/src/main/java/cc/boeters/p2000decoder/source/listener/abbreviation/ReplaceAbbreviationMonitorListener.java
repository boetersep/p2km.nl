package cc.boeters.p2000decoder.source.listener.abbreviation;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.Message;

public class ReplaceAbbreviationMonitorListener implements MonitorListener {

	private final AbbreviationsService abbreviationsService;

	public ReplaceAbbreviationMonitorListener(AbbreviationsService abbreviationsService) {
		super();
		this.abbreviationsService = abbreviationsService;
	}

	@Override
	public Message onNewMessage(Message message) throws Throwable {
		return new NoAbbrMessageDecorator(message, abbreviationsService);
	}

}
