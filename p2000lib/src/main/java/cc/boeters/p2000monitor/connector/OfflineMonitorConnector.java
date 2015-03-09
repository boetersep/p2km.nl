package cc.boeters.p2000monitor.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Singleton;

import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.support.Stage;
import cc.boeters.p2000monitor.support.annotation.NewMessage;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

@Singleton
@Stage(cc.boeters.p2000monitor.support.StageResolver.Stage.DEVElOPMENT)
public class OfflineMonitorConnector implements MonitorConnector {

	private final ObjectMapper mapper = new ObjectMapper();

	@Inject
	@NewMessage
	private Event<MonitorEvent> event;

	private final List<Message> messages;

	public OfflineMonitorConnector() throws JsonParseException,
			JsonMappingException, IOException {
		final CollectionType javaType = mapper.getTypeFactory()
				.constructCollectionType(List.class, Message.class);
		messages = mapper.readValue(OfflineMonitorConnector.class
				.getResourceAsStream("/data/dummydata.json"), javaType);
	}

	@Override
	public List<Message> getMessages() {
		return new ArrayList<Message>();
	}

	@PostConstruct
	private void start() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				for (int i = 0; i < messages.size(); i++) {
					event.fire(new MonitorEvent(messages.get(i)));

					if (i + 1 == messages.size()) {
						i = 0;
					}

					try {
						Thread.sleep((long) (3000 + (Math.random() * 20000D)));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		}).start();
	}

	@Override
	public void stop() {
	}

}
