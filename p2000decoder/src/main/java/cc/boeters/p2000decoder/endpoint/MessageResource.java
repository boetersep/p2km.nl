package cc.boeters.p2000decoder.endpoint;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

@Path("/messages")
public class MessageResource {

	@Inject
	private MongoDatabase database;

	@GET
	@Path("/{capcode}/{timestamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessage(@PathParam("capcode") Integer capcode, @PathParam("timestamp") Long timestamp) {
		FindIterable<Document> messages = database.getCollection("messages")
				.find(Filters.and(Filters.eq("capcode", capcode), Filters.eq("timestamp", timestamp)));
		Document message = messages.first();
		if (message != null) {
			return Response.ok(message.toJson()).build();
		}
		return Response.noContent().build();
	}
}