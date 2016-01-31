package cc.boeters.p2000decoder.endpoint;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/hello")
public class HelloWorldResource {


    @GET
    @Produces("text/plain")
    public Response getHello() {
    	return Response.ok("Hello").build();
    }
}