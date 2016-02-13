package cc.boeters.p2000decoder.source.listener;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.bson.Document;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import org.eclipse.jetty.websocket.server.WebSocketServerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.message.Message;

public abstract class MessageUpdater implements MonitorListener {

	static final Logger LOG = LoggerFactory.getLogger(MessageUpdater.class);

	private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

	private final DataSource dataSource;
	private MongoDatabase db;
	private static final UpdateOptions UPDATE_OPTIONS = new UpdateOptions() {
		{
			upsert(true);
		}
	};

	private final ExecutorService threadPool;

	private final WebSocketServerFactory webSocketFactory;

	public MessageUpdater(DataSource dataSource, MongoDatabase db, WebSocketServerFactory webSocketFactory) {
		this.dataSource = dataSource;
		this.db = db;
		this.webSocketFactory = webSocketFactory;
		threadPool = Executors.newFixedThreadPool(4);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public MongoDatabase getDb() {
		return db;
	}

	public abstract Map<String, Object> getUpdateData(Message message);

	public abstract String getName();

	@Override
	public final Message onNewMessage(final Message message) throws Throwable {
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, Object> updateData = getUpdateData(message);
					Document q = new Document().append("capcode", message.getCapcode()).append("timestamp",
							message.getTimestamp());
					Document value = new Document(getName(), updateData);
					UpdateResult updateResult = db.getCollection("messages").updateOne(q, new Document("$set", value),
							UPDATE_OPTIONS);

					if (updateResult.getModifiedCount() == 0) {
						return;
					}

					Map<String, Object> updateJson = new LinkedHashMap<String, Object>();
					updateJson.put("update", true);
					updateJson.put("capcode", message.getCapcode());
					updateJson.put("timestamp", message.getTimestamp());
					updateJson.put("data", value);

					String updateJsonString = JSON_MAPPER.writeValueAsString(updateJson);
					Collection<WebSocketSession> openSessions = webSocketFactory.getOpenSessions();
					for (WebSocketSession webSocketSession : openSessions) {
						if (webSocketSession.isOpen()) {
							webSocketSession.getRemote().sendStringByFuture(updateJsonString);
						}
					}
				} catch (Throwable t) {
					LOG.error("Exception while updating message.", t);
				}
			}
		});
		return message;
	}
}
