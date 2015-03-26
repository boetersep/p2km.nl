package cc.boeters.p2000monitor.support;

public final class StageResolver {

	public enum Stage {
		PRODUCTION, DEVELOPMENT;
	}

	public static Stage resolve() {
		String stageVal = System.getProperty("stage");
		try {
			return Stage.valueOf(stageVal.toUpperCase());
		} catch (Throwable t) {
			return Stage.PRODUCTION;
		}
	}
}
