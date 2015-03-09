package cc.boeters.p2000monitor.support;

import java.io.IOException;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import cc.boeters.p2000monitor.support.annotation.Property;

public class PropertyInjectionManager {

	private final Properties properties;

	public PropertyInjectionManager() throws IOException {
		String propertiesFile = "/connector."
				+ StageResolver.resolve().getText() + ".properties";
		properties = new Properties();
		properties.load(PropertyInjectionManager.class
				.getResourceAsStream(propertiesFile));

	}

	@Produces
	@Property
	public String injectConfiguration(InjectionPoint ip)
			throws IllegalStateException {
		Property param = ip.getAnnotated().getAnnotation(Property.class);
		return properties.getProperty(param.value());
	}
}