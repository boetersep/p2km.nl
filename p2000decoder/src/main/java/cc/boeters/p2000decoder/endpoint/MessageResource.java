package cc.boeters.p2000decoder.endpoint;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cc.boeters.p2000decoder.search.MessageDatabase;
import cc.boeters.p2000decoder.source.model.area.City;
import cc.boeters.p2000decoder.source.model.area.Country;
import cc.boeters.p2000decoder.source.model.area.Municipality;
import cc.boeters.p2000decoder.source.model.area.Province;

@Path("/messages")
public class MessageResource {

	@Named("mongo")
	@Inject
	private MessageDatabase database;

	@Named("nl")
	@Inject
	private Country netherlands;

	@GET
	@Path("/detail/{capcode}/{timestamp}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMessage(@PathParam("capcode") Integer capcode, @PathParam("timestamp") Long timestamp) {
		Map<String, Object> message = database.find(capcode, timestamp);
		if (message != null) {
			return Response.ok(message).build();
		}
		return Response.noContent().build();
	}

	@GET
	@Path("{province}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProvince(@PathParam("province") String province) {
		Province findSubAreaBySlug = netherlands.findSubAreaBySlug(province);
		List<Map<String, Object>> messages = database.findByProvince(findSubAreaBySlug);
		return Response.ok(messages).build();
	}

	@GET
	@Path("{province}/{municipality}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMunicipality(@PathParam("province") String province,
			@PathParam("municipality") String municipality) {
		Province findSubAreaBySlug = netherlands.findSubAreaBySlug(province);
		Municipality findSubAreaBySlug2 = findSubAreaBySlug.findSubAreaBySlug(municipality);
		List<Map<String, Object>> messages = database.findByMunicipality(findSubAreaBySlug2);
		return Response.ok(messages).build();

	}

	@GET
	@Path("{province}/{municipality}/{city}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCity(@PathParam("province") String province, @PathParam("municipality") String municipality,
			@PathParam("city") String city) {
		Province findSubAreaBySlug = netherlands.findSubAreaBySlug(province);
		Municipality findSubAreaBySlug2 = findSubAreaBySlug.findSubAreaBySlug(municipality);
		City findSubAreaBySlug3 = findSubAreaBySlug2.findSubAreaBySlug(city);
		List<Map<String, Object>> messages = database.findByCity(findSubAreaBySlug3);
		return Response.ok(messages).build();
	}

}