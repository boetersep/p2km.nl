package cc.boeters.p2000monitor.support;

import java.io.IOException;
import java.util.Properties;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.apache.commons.lang3.BooleanUtils;

import cc.boeters.p2000monitor.support.annotation.Property;

public class PropertyInjectionManager {

	private final Properties properties;

	public PropertyInjectionManager() throws IOException {
		String propertiesFile = "/connector."
				+ StageResolver.resolve().toString().toLowerCase()
				+ ".properties";
		properties = new Properties();
		properties.load(PropertyInjectionManager.class
				.getResourceAsStream(propertiesFile));

	}

	@Produces
	@Property
	public Boolean injectConfigurationBoolean(InjectionPoint ip)
			throws IllegalStateException {
		Property param = ip.getAnnotated().getAnnotation(Property.class);
		return BooleanUtils.toBoolean(properties.getProperty(param.value()));
	}

	@Produces
	@Property
	public String injectConfigurationString(InjectionPoint ip)
			throws IllegalStateException {
		Property param = ip.getAnnotated().getAnnotation(Property.class);
		return properties.getProperty(param.value());
	}

}