package cc.boeters.p2000decoder.source.listener;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.sql.DataSource;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

import cc.boeters.p2000decoder.source.MonitorListener;
import cc.boeters.p2000decoder.source.model.Message;

public abstract class MessageUpdater implements MonitorListener {

	static final Logger LOG = LoggerFactory.getLogger(MessageUpdater.class);

	private final DataSource dataSource;
	private MongoDatabase db;
	private static final UpdateOptions UPDATE_OPTIONS = new UpdateOptions() {
		{
			upsert(true);
		}
	};

	private final ExecutorService threadPool;

	public MessageUpdater(DataSource dataSource, MongoDatabase db) {
		this.dataSource = dataSource;
		this.db = db;
		threadPool = Executors.newFixedThreadPool(4);
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public MongoDatabase getDb() {
		return db;
	}

	public abstract Map<String, Object> decompose(Message message);

	public abstract String getName();

	@Override
	public final Message onNewMessage(final Message message) throws Throwable {
		threadPool.submit(new Runnable() {
			@Override
			public void run() {
				try {
					Map<String, Object> decompose = decompose(message);
					Document q = new Document().append("capcode", message.getCapcode()).append("timestamp",
							message.getTimestamp());
					db.getCollection("messages").updateOne(q, new Document("$set", new Document(getName(), decompose)),
							UPDATE_OPTIONS);
				} catch (Throwable t) {
					LOG.error("Exception while updating message.", t);
					throw t;
				}
			}
		});
		return message;
	}
}
