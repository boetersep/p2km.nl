package cc.boeters.p2000decoder.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import cc.boeters.p2000decoder.source.model.area.Area;
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
	@Produces(MediaType.APPLICATION_JSON)
	public Response get() {
		return Response.ok(new AreaDTO(netherlands, netherlands.getSubAreas())).build();
	}

	@GET
	@Path("{province}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getProvince(@PathParam("province") String province) {
		Province provinceArea = netherlands.findSubAreaBySlug(province);
		return Response.ok(new AreaDTO(provinceArea, provinceArea.getSubAreas())).build();
	}

	@GET
	@Path("{province}/{municipality}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getMunicipality(@PathParam("province") String province,
			@PathParam("municipality") String municipality) {
		Province provinceArea = netherlands.findSubAreaBySlug(province);
		Municipality municipalityArea = provinceArea.findSubAreaBySlug(municipality);
		return Response.ok(new AreaDTO(municipalityArea, municipalityArea.getSubAreas())).build();

	}

	@GET
	@Path("{province}/{municipality}/{city}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCity(@PathParam("province") String province, @PathParam("municipality") String municipality,
			@PathParam("city") String city) {
		Province provinceArea = netherlands.findSubAreaBySlug(province);
		Municipality municipalityArea = provinceArea.findSubAreaBySlug(municipality);
		City cityArea = municipalityArea.findSubAreaBySlug(city);
		return Response.ok(new AreaDTO(cityArea, cityArea.getSubAreas())).build();
	}

	class AreaDTO {
		private final Area<?> area;
		private final List<? extends Area<?>> subAreas;

		public AreaDTO(Area<?> area, List<? extends Area<?>> subAreas) {
			super();
			this.area = area;
			this.subAreas = subAreas;
		}

		public Area<?> getArea() {
			return area;
		}

		public List<? extends Area<?>> getSubAreas() {
			return subAreas;
		}

	}

}
