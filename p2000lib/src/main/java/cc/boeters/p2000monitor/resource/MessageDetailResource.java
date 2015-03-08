package cc.boeters.p2000monitor.resource;

import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import cc.boeters.p2000monitor.model.Message;

@Singleton
@Path("/messages/detail")
public class MessageDetailResource {

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Message get(Message message) {

		return new Message();
	}

}
