package cc.boeters.p2000monitor.archive;

import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import cc.boeters.p2000monitor.connector.MonitorEvent;
import cc.boeters.p2000monitor.support.annotation.NewMessage;

@Singleton
public class ElasticsearchMessageConsumer {

	@Inject
	private ElasticsearchMessageArchive archive;

	@Asynchronous
	public void consumeMessage(@Observes @NewMessage MonitorEvent monitorEvent) {

		archive.storeMessage(monitorEvent.getMessage());

	}

}
