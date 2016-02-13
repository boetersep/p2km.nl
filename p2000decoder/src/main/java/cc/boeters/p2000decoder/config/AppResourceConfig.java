package cc.boeters.p2000decoder.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.search.MessageDatabase;
import cc.boeters.p2000decoder.search.MongoMessageDatabase;
import cc.boeters.p2000decoder.source.MonitorSource;
import cc.boeters.p2000decoder.source.model.area.Country;

public class AppResourceConfig extends ResourceConfig {
	public static final String MESSAGES_COLLECTION = "messagesCollection";

	public AppResourceConfig(MonitorSource source, ComboPooledDataSource cpds, MongoDatabase database, Country nl) {
		register(new AppBinder(source, cpds, database, nl));
		register(JacksonFeature.class);

		packages(true, "cc.boeters");
	}

	public class AppBinder extends AbstractBinder {

		private final MonitorSource source;
		private final ComboPooledDataSource cpds;
		private final MongoDatabase database;
		private final Country nl;

		public AppBinder(MonitorSource source, ComboPooledDataSource cpds, MongoDatabase database, Country nl) {
			this.source = source;
			this.cpds = cpds;
			this.database = database;
			this.nl = nl;
		}

		@Override
		protected void configure() {
			bind(nl).to(Country.class).named("nl");
			bind(cpds).to(ComboPooledDataSource.class);
			bind(source).to(MonitorSource.class);
			bind(new MongoMessageDatabase(database)).to(MessageDatabase.class).named("mongo");
		}
	}
}