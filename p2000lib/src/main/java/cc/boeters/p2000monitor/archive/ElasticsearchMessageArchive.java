package cc.boeters.p2000monitor.archive;

import java.io.IOException;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cc.boeters.p2000monitor.model.Message;
import cc.boeters.p2000monitor.processing.AbbreviationsService;
import cc.boeters.p2000monitor.processing.MessageDecomposer;
import cc.boeters.p2000monitor.processing.NoAbbrMessageDecorator;
import cc.boeters.p2000monitor.support.annotation.Property;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Singleton
public class ElasticsearchMessageArchive implements MessageArchive {

	static final Logger LOG = LoggerFactory
			.getLogger(ElasticsearchMessageArchive.class);

	@Inject
	private AbbreviationsService abbreviationsService;

	private Client client;

	@Inject
	@Any
	private Instance<MessageDecomposer> decomposers;

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Inject
	@Property("es.index")
	private String p2000Index;

	@Inject
	private void createClient(@Property("es.host") String host,
			@Property("es.port") String port,
			@Property("es.cluster") String cluster) {

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
		String src = client.prepareGet(p2000Index, "message", hash).get()
				.getSourceAsString();
		try {
			return objectMapper.readValue(src, Message.class);
		} catch (IOException e) {
			LOG.error("Unable to unmarshall Elasticsearch source.", e);
			return null;
		}
	}

	@Override
	public String storeMessage(Message message) {

		message = new NoAbbrMessageDecorator(message, abbreviationsService);

		StringBuilder hash = new StringBuilder();
		hash.append(message.getCapcode());
		hash.append("-");
		hash.append(message.getTimestamp());
		String id = hash.toString();

		for (MessageDecomposer decomposer : decomposers) {
			message.getMetadata().put(decomposer.getName(),
					decomposer.decompose(message));
		}

		try {
			String jsonMessage = objectMapper.writeValueAsString(message);
			client.prepareIndex(p2000Index, "message", id)
					.setSource(jsonMessage).get();
		} catch (JsonProcessingException e) {
			LOG.error("Unable to marshall Elasticsearch source.", e);

		}

		return id;
	}

}
