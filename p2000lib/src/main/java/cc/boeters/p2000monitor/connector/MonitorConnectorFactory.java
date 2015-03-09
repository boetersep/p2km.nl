package cc.boeters.p2000monitor.connector;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import cc.boeters.p2000monitor.support.Stage;
import cc.boeters.p2000monitor.support.StageResolver;

// The dynamic producer
public class MonitorConnectorFactory {

	@SuppressWarnings("serial")
	public static class StageQualifier extends AnnotationLiteral<Stage>
			implements Stage {

		private final cc.boeters.p2000monitor.support.StageResolver.Stage value;

		public StageQualifier(
				cc.boeters.p2000monitor.support.StageResolver.Stage value) {
			this.value = value;
		}

		@Override
		public cc.boeters.p2000monitor.support.StageResolver.Stage value() {
			return value;
		}
	}

	@Inject
	@Any
	Instance<MonitorConnector> greetings;

	public cc.boeters.p2000monitor.support.StageResolver.Stage getEnvironment() {
		return StageResolver.resolve();
	}

	@Produces
	public MonitorConnector getGreeting() {
		Instance<MonitorConnector> found = greetings.select(new StageQualifier(
				getEnvironment()));
		if (!found.isUnsatisfied() && !found.isAmbiguous()) {
			return found.get();
		}
		throw new RuntimeException("Error ...");
	}

}