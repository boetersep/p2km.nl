package cc.boeters.p2000server.listener;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cc.boeters.p2000monitor.support.annotation.Property;
import cc.boeters.p2000server.server.P2000Server;

@WebListener
public class P2000ServerContextListener implements ServletContextListener {

	private P2000Server server;

	@Inject
	@Property("p2000.server.start")
	private Boolean startServer;

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		server.stop();
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (!startServer) {
			return;
		}
		server = new P2000Server(2000);
		Thread t = new Thread(server);
		t.start();
	}

}
