package cc.boeters.p2000decoder.config;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.source.MonitorSource;

public class AppResourceConfig extends ResourceConfig {
	public static final String MESSAGES_COLLECTION = "messagesCollection";

	public AppResourceConfig(MonitorSource source, ComboPooledDataSource cpds, MongoDatabase database) {
		register(new AppBinder(source, cpds, database));
		packages(true, "cc.boeters");
	}

	public class AppBinder extends AbstractBinder {

		private final MonitorSource source;
		private final ComboPooledDataSource cpds;
		private final MongoDatabase database;

		public AppBinder(MonitorSource source, ComboPooledDataSource cpds, MongoDatabase database) {
			this.source = source;
			this.cpds = cpds;
			this.database = database;
		}

		@Override
		protected void configure() {

			bind(cpds).to(ComboPooledDataSource.class);
			bind(source).to(MonitorSource.class);
			bind(database).to(MongoDatabase.class);

			// DB helloworld;
			// try {
			// helloworld = new MongoClient("localhost").getDB("helloworld");
			// } catch (UnknownHostException e) {
			// throw new RuntimeException(e);
			// }
			// Jongo jongo = new Jongo(helloworld);
			//
			// bind(jongo.getCollection("messages")).to(MongoCollection.class).named(MESSAGES_COLLECTION);
			//
			// bind(HelloService.class).to(HelloService.class);
		}
	}
}