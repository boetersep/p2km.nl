package cc.boeters.p2000monitor.archive;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.support.annotation.Property;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class ElasticsearchMessageArchive implements MessageArchive {

	private Client client;

	private final ObjectMapper ObjectMapper = new ObjectMapper();

	private boolean enable;

	@Inject
	private void createClient(@Property("es.enable") String enable,
			@Property("es.host") String host, @Property("es.port") String port,
			@Property("es.cluster") String cluster) {

		this.enable = Boolean.valueOf(enable);

		if (!this.enable) {
			return;
		}

		Settings settings = ImmutableSettings.settingsBuilder()
				.put("cluster.name", cluster).build();

		client = new TransportClient(settings)
				.addTransportAddress(new InetSocketTransportAddress(host,
						Integer.valueOf(port)));
	}

	@PreDestroy
	private void destroyClient() {
		client.close();
	}

	@Override
	public Message retrieveMessage(String hash) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String storeMessage(Message message) {
		if (!this.enable) {
			return null;
		}

		StringBuilder hash = new StringBuilder();
		hash.append(message.getCapcode());
		hash.append("-");
		hash.append(message.getTimestamp());
		String id = hash.toString();

		try {
			String jsonMessage = ObjectMapper.writeValueAsString(message);
			client.prepareIndex("p2000", "message", id).setSource(jsonMessage)
					.get();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return id;
	}

}
