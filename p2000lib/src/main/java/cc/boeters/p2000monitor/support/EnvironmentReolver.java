package cc.boeters.p2000monitor.support;

public final class EnvironmentReolver {

	enum Environment {
		PRODUCTION("prod"), DEVElOPMENT("dev");

		private final String text;

		private Environment(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

	}

	public static boolean isDevelopment() {
		return resolve() == Environment.DEVElOPMENT;
	}

	public static boolean isProduction() {
		return resolve() == Environment.PRODUCTION;
	}

	public static Environment resolve() {
		if (System.getProperty("os.name", "").toLowerCase().contains("windows")) {
			return Environment.DEVElOPMENT;
		}
		return Environment.PRODUCTION;
	}

}
