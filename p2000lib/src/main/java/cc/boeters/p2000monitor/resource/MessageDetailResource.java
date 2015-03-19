package cc.boeters.p2000monitor.resource;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cc.boeters.p2000monitor.archive.ElasticsearchMessageArchive;
import cc.boeters.p2000monitor.model.Message;

@Singleton
@Path("/messages/detail")
public class MessageDetailResource {

	@Inject
	private ElasticsearchMessageArchive archive;

	@GET
	@Path("/{hash}")
	@Produces(MediaType.APPLICATION_JSON)
	public Message get(@PathParam("hash") String hash) {
		return archive.retrieveMessage(hash);
	}

}
