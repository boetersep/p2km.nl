package cc.boeters.p2000monitor.archive;

import cc.boeters.p2000monitor.model.CapcodeInfo;

public class NoAbbrCapcodeInfoDecorator extends CapcodeInfo {

	private final CapcodeInfo decoratedCapcodeInfo;

	private final AbbreviationsService abbreviationsService;

	public NoAbbrCapcodeInfoDecorator(CapcodeInfo decoratedCapcodeInfo,
			AbbreviationsService abbreviationsService) {
		this.decoratedCapcodeInfo = decoratedCapcodeInfo;
		this.abbreviationsService = abbreviationsService;
	}

	@Override
	public int getCapcode() {
		return decoratedCapcodeInfo.getCapcode();
	}

	@Override
	public String getDescription() {
		return abbreviationsService.translate(decoratedCapcodeInfo
				.getDescription());
	}

	@Override
	public String getDiscipline() {
		return decoratedCapcodeInfo.getDiscipline();
	}

	@Override
	public String getRegion() {
		return abbreviationsService.translate(decoratedCapcodeInfo.getRegion());
	}

	@Override
	public String getSector() {
		return abbreviationsService.translate(decoratedCapcodeInfo.getSector());
	}

	@Override
	public String getShortdesc() {
		return decoratedCapcodeInfo.getShortdesc();
	}

	@Override
	public String toString() {
		return "NoAbbrCapcodeInfoDecorator [decoratedCapcodeInfo="
				+ decoratedCapcodeInfo + ", abbreviationsService="
				+ abbreviationsService + "]";
	}

}
