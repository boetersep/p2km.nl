package cc.boeters.p2000decoder.app;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import cc.boeters.p2000decoder.config.AppResourceConfig;
import cc.boeters.p2000decoder.endpoint.AppWebSocket;
import cc.boeters.p2000decoder.source.MonitorSource;
import cc.boeters.p2000decoder.source.TcpIpMonitorSource;
import cc.boeters.p2000decoder.source.listener.WebsocketBroadcasterListener;

public class Runner {

	private MonitorSource source;

	public static void main(String[] args) throws Exception {

		new Runner();
	}

	public Runner() throws Exception {

		ComboPooledDataSource cpds = new ComboPooledDataSource();
		cpds.setDriverClass("com.mysql.jdbc.Driver"); // loads the jdbc driver
		cpds.setJdbcUrl("jdbc:mysql://localhost/p2000");
		cpds.setUser("p2000");
		cpds.setPassword("p2000");

		source = new TcpIpMonitorSource();
		ResourceConfig config = new AppResourceConfig(source, cpds);
		JettyHttpContainer restHandler = ContainerFactory.createContainer(JettyHttpContainer.class, config);
		WebSocketHandler.Simple wsHandler = new WebSocketHandler.Simple(AppWebSocket.class);
		final WebSocketServletFactory webSocketFactory = wsHandler.getWebSocketFactory();
		source.addListener(new WebsocketBroadcasterListener((WebSocketServerFactory) webSocketFactory));
		source.start();
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { wsHandler, restHandler });
		Server server = new Server(9998);
		server.setHandler(handlers);
		server.start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Destroyer(this)));
	}

	public void stop() {
		source.stop();
	}

}