package cc.boeters.p2000decoder.app;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import cc.boeters.p2000decoder.config.AppResourceConfig;
import cc.boeters.p2000decoder.endpoint.AppWebSocket;
import cc.boeters.p2000decoder.source.MonitorSource;
import cc.boeters.p2000decoder.source.MysqlCapcodeDatabase;
import cc.boeters.p2000decoder.source.TcpIpMonitorSource;
import cc.boeters.p2000decoder.source.listener.SaveMessageListener;
import cc.boeters.p2000decoder.source.listener.WebsocketBroadcasterListener;
import cc.boeters.p2000decoder.source.listener.abbreviation.AbbreviationsService;
import cc.boeters.p2000decoder.source.listener.abbreviation.ReplaceAbbreviationMonitorListener;
import cc.boeters.p2000decoder.source.listener.classification.ClassificationMessageUpdater;
import cc.boeters.p2000decoder.source.listener.geocoding.GeocodingMessageUpdater;
import cc.boeters.p2000decoder.source.model.area.Country;

public class Runner {

	private MonitorSource source;
	private MongoClient mongoClient;
	private ComboPooledDataSource cpds;

	public static void main(String[] args) throws Exception {
		Options options = createCommandLineOptions();

		try {
			CommandLineParser parser = new DefaultParser();
			CommandLine line = parser.parse(options, args);
			new Runner(line);
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("p2000decoder", "Start p2000decoder", options, "", true);
		}

	}

	private static Options createCommandLineOptions() {
		Options options = new Options();
		options.addOption(Option.builder().required().longOpt("mongo-host").hasArg().desc("MongoDB host").build());
		options.addOption(Option.builder().required().longOpt("mongo-db").hasArg().desc("MongoDB database").build());
		options.addOption(Option.builder().required().longOpt("mongo-user").hasArg().desc("MongoDB user").build());
		options.addOption(Option.builder().required().longOpt("mongo-pass").hasArg().desc("MongoDB password").build());
		options.addOption(Option.builder().required().longOpt("mysql-host").hasArg().desc("Mysql host").build());
		options.addOption(Option.builder().required().longOpt("mysql-db").hasArg().desc("Mysql database").build());
		options.addOption(Option.builder().required().longOpt("mysql-user").hasArg().desc("Mysql user").build());
		options.addOption(Option.builder().required().longOpt("mysql-pass").hasArg().desc("Mysql password").build());
		options.addOption(Option.builder().required().longOpt("monitor-host").hasArg()
				.desc("p2000 monitor endpoint host").build());
		options.addOption(Option.builder().required().longOpt("monitor-port").hasArg()
				.desc("p2000 monitor endpoint port").build());
		return options;
	}

	public Runner(CommandLine line) throws Exception {
		Runtime.getRuntime().addShutdownHook(new Thread(new Destroyer()));

		cpds = new ComboPooledDataSource();
		cpds.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc driver
		cpds.setJdbcUrl("jdbc:mysql://" + line.getOptionValue("mysql-host") + "/" + line.getOptionValue("mysql-db"));
		cpds.setUser(line.getOptionValue("mysql-user"));
		cpds.setPassword(line.getOptionValue("mysql-pass"));
		cpds.setAcquireIncrement(5);
		cpds.setMaxPoolSize(20);

		MongoClientURI uri = new MongoClientURI("mongodb://" + line.getOptionValue("mongo-user") + ":"
				+ line.getOptionValue("mongo-pass") + "@" + line.getOptionValue("mongo-host") + "/?authSource="
				+ line.getOptionValue("mongo-db") + "&authMechanism=SCRAM-SHA-1");

		mongoClient = new MongoClient(uri);
		MongoDatabase database = mongoClient.getDatabase(line.getOptionValue("mongo-db"));

		source = new TcpIpMonitorSource(new MysqlCapcodeDatabase(cpds), line.getOptionValue("monitor-host"),
				Integer.valueOf(line.getOptionValue("monitor-port")));
		ResourceConfig config = new AppResourceConfig(source, cpds, database, getCountryNl());
		JettyHttpContainer restHandler = ContainerFactory.createContainer(JettyHttpContainer.class, config);
		WebSocketHandler.Simple wsHandler = new WebSocketHandler.Simple(AppWebSocket.class);
		final WebSocketServerFactory webSocketFactory = (WebSocketServerFactory) wsHandler.getWebSocketFactory();
		source.addListener(new WebsocketBroadcasterListener(webSocketFactory));
		source.addListener(new ReplaceAbbreviationMonitorListener(new AbbreviationsService()));
		source.addListener(new SaveMessageListener(database));
		source.addListener(new GeocodingMessageUpdater(cpds, database, webSocketFactory));
		source.addListener(new ClassificationMessageUpdater(cpds, database, webSocketFactory));
		source.start();
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { wsHandler, restHandler });
		Server server = new Server(9998);
		server.setHandler(handlers);
		server.start();
	}

	private Country getCountryNl() {
		try {
			// InputStream resourceAsStream =
			// getClass().getResourceAsStream("/data/nederland.json");

			JAXBContext jc = JAXBContext.newInstance(Country.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			InputStream resourceAsStream = getClass().getResourceAsStream("/data/nederland.xml");
			Country country = (Country) unmarshaller.unmarshal(resourceAsStream);
			return country;
			// return new ObjectMapper().readValue(resourceAsStream,
			// Country.class);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	class Destroyer implements Runnable {

		@Override
		public void run() {
			source.stop();
			cpds.close();
			mongoClient.close();
		}
	}

}