package cc.boeters.p2000decoder.endpoint;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cc.boeters.p2000decoder.source.model.area.City;
import cc.boeters.p2000decoder.source.model.area.Country;
import cc.boeters.p2000decoder.source.model.area.Municipality;
import cc.boeters.p2000decoder.source.model.area.Province;

@Path("/locatie")
public class AreaResource {

	@Named("nl")
	@Inject
	private Country netherlands;

	@GET
	@Path("{province}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProvince(@PathParam("province") String province) {
		Province findSubAreaBySlug = netherlands.findSubAreaBySlug(province);
		return Response.ok(findSubAreaBySlug).build();
	}

	@GET
	@Path("{province}/{municipality}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMunicipality(@PathParam("province") String province,
			@PathParam("municipality") String municipality) {
		Province findSubAreaBySlug = netherlands.findSubAreaBySlug(province);
		Municipality findSubAreaBySlug2 = findSubAreaBySlug.findSubAreaBySlug(municipality);
		return Response.ok(findSubAreaBySlug2).build();

	}

	@GET
	@Path("{province}/{municipality}/{city}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCity(@PathParam("province") String province, @PathParam("municipality") String municipality,
			@PathParam("city") String city) {
		Province findSubAreaBySlug = netherlands.findSubAreaBySlug(province);
		Municipality findSubAreaBySlug2 = findSubAreaBySlug.findSubAreaBySlug(municipality);
		City findSubAreaBySlug3 = findSubAreaBySlug2.findSubAreaBySlug(city);
		return Response.ok(findSubAreaBySlug3).build();
	}

}
