package cc.boeters.p2000decoder.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Destroyer implements Runnable {

	static final Logger LOG = LoggerFactory.getLogger(Destroyer.class);

	private final Runner runner;

	public Destroyer(Runner runner) {
		this.runner = runner;
	}

	@Override
	public void run() {
		LOG.info("Running destroyer :D.");
		runner.stop();
	}

}
