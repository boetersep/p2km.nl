package cc.boeters.p2000decoder.app;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.glassfish.jersey.jetty.JettyHttpContainer;
import org.glassfish.jersey.server.ContainerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import cc.boeters.p2000decoder.config.AppResourceConfig;
import cc.boeters.p2000decoder.endpoint.AppWebSocket;
import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.MonitorSource;
import cc.boeters.p2000decoder.source.TcpIpMonitorSource;
import cc.boeters.p2000decoder.source.model.Message;

public class Runner {

	private final MonitorSource source;

	public static void main(String[] args) throws Exception {

		ResourceConfig config = new AppResourceConfig();
		JettyHttpContainer restHandler = ContainerFactory.createContainer(JettyHttpContainer.class, config);
		WebSocketHandler.Simple wsHandler = new WebSocketHandler.Simple(AppWebSocket.class);
		HandlerList handlers = new HandlerList();
		handlers.setHandlers(new Handler[] { wsHandler, restHandler });
		Server server = new Server(9998);
		server.setHandler(handlers);
		server.start();
		System.out.println("Server started at " + server.dump());

		new Runner();
	}

	public Runner() {
		source = new TcpIpMonitorSource();
		source.addListener(new MonitorListener() {

			@Override
			public void onNewMessage(Message message) {
				System.out.println(message);
			}
		});
		source.start();
		Runtime.getRuntime().addShutdownHook(new Thread(new Destroyer(this)));
	}

	public void stop() {
		source.stop();
	}

}