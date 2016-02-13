package cc.boeters.p2000decoder.source.listener.abbreviation;

import java.util.ArrayList;
import java.util.List;

import cc.boeters.p2000decoder.source.model.message.CapcodeInfo;
import cc.boeters.p2000decoder.source.model.message.Message;

public class NoAbbrMessageDecorator extends Message {

	private final AbbreviationsService abbreviationsService;

	private final Message decoratedMessage;

	public NoAbbrMessageDecorator(Message decoratedMessage, AbbreviationsService abbreviationsService) {
		this.decoratedMessage = decoratedMessage;
		this.abbreviationsService = abbreviationsService;
	}

	@Override
	public int getCapcode() {
		return decoratedMessage.getCapcode();
	}

	@Override
	public CapcodeInfo getCapcodeInfo() {
		if (decoratedMessage.getCapcodeInfo() != null) {
			return new NoAbbrCapcodeInfoDecorator(decoratedMessage.getCapcodeInfo(), abbreviationsService);
		}
		return decoratedMessage.getCapcodeInfo();
	}

	@Override
	public String getDate() {
		return decoratedMessage.getDate();
	}

	@Override
	public List<CapcodeInfo> getGroup() {
		List<CapcodeInfo> decoratedGroup = decoratedMessage.getGroup();
		List<CapcodeInfo> translatedGroup = new ArrayList<CapcodeInfo>(decoratedGroup.size());
		for (CapcodeInfo decoratedCapcodeInfo : decoratedGroup) {
			translatedGroup.add(new NoAbbrCapcodeInfoDecorator(decoratedCapcodeInfo, abbreviationsService));
		}
		return translatedGroup;
	}

	@Override
	public String getMessage() {
		return abbreviationsService.translate(decoratedMessage.getMessage());
	}

	@Override
	public String getTime() {
		return decoratedMessage.getTime();
	}

	@Override
	public long getTimestamp() {
		return decoratedMessage.getTimestamp();
	}

	@Override
	public MessageType getType() {
		return decoratedMessage.getType();
	}

	@Override
	public boolean isAlphaMessage() {
		return decoratedMessage.isAlphaMessage();
	}

	@Override
	public boolean isGroupMessage() {
		return decoratedMessage.isGroupMessage();
	}

	@Override
	public String toString() {
		return "NoAbbrMessageDecorator [decoratedMessage=" + decoratedMessage + ", abbreviationsService="
				+ abbreviationsService + "]";
	}

}
