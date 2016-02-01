package cc.boeters.p2000decoder.endpoint;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import cc.boeters.p2000decoder.source.MonitorSource;

@Path("/hello")
public class HelloWorldResource {

	@Inject
	private MonitorSource source;

	@GET
	@Produces("text/plain")
	public Response getHello() {
		return Response.ok(source.toString()).build();
	}
}