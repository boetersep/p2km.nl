package cc.boeters.p2000monitor.support;

public final class StageResolver {

	public enum Stage {
		PRODUCTION("prod"), DEVElOPMENT("dev");

		private final String text;

		private Stage(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

	}

	public static boolean isDevelopment() {
		return resolve() == Stage.DEVElOPMENT;
	}

	public static boolean isProduction() {
		return resolve() == Stage.PRODUCTION;
	}

	public static Stage resolve() {
		if (System.getProperty("os.name", "").toLowerCase().contains("windows")) {
			return Stage.DEVElOPMENT;
		}
		return Stage.PRODUCTION;
	}

}
