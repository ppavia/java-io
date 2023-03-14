package fr.cimut.ged.entrant.interrogation.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.dto.TypeFilters;
import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.dto.TypeParamsDto;
import fr.cimut.ged.entrant.dto.TypeParamsModificationDto;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.TypeService;

/**
 * 
 */
@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/type")
public class TypeEndpoint extends EndpointAbstract {

	@EJB
	TypeService typeService;

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response searchTypes(@HeaderParam("Authorization") String authKey,
			@QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir,
			@QueryParam("isIDOC") Boolean idoc,
			@QueryParam("isVisibleExtranet") Boolean visibleExtranet,
			@QueryParam("codeType") String codeType,
			@QueryParam("libelleType") String libelleType,
			@QueryParam("category") String category
			)
			throws CimutConfException {
		TypeFilters filters = new TypeFilters();
		filters.setIdoc(idoc);
		filters.setVisibleExtranet(visibleExtranet);
		filters.setCodeType(codeType);
		filters.setLibelleType(libelleType);
		filters.setCategory(category);

		List<TypeDto> list = typeService.search(cmroc, envir, filters);
		return Response.ok(list).build();
	}

	@GET
	@Path("/{idType:[0-9]+}/type")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response listTypes(@HeaderParam("Authorization") String authKey, @PathParam("idType") Long idType, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir) throws CimutConfException {
		final List<TypeDto> list = typeService.list(idType, cmroc, envir);
		return Response.ok(list).build();
	}

	@PUT
	@Path("/{idType:[0-9]+}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response updateType(@HeaderParam("Authorization") String authKey, @PathParam("idType") Long idType, @QueryParam("env") String envir,
			@QueryParam("cmroc") String cmroc, @HeaderParam("user") String user, Type typeDto) throws GedeException {
		if (user != null) {
			typeDto.setUserMaj(user);
		}
		checkIdMatch(idType, typeDto.getId());
		TypeDto typePersisted = typeService.update(typeDto, cmroc);
		return Response.ok(typePersisted).build();
	}

	@DELETE
	@Path("/{idType:[0-9]+}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response deleteType(@HeaderParam("Authorization") String authKey, @PathParam("idType") Long idType, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @HeaderParam("user") String user) throws GedeException {
		typeService.delete(idType, cmroc);
		return Response.ok().build();
	}

	@POST
	@Path("/{idType:[0-9]+}/parameters")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response saveOrganismTypeParameters(
			@HeaderParam("Authorization") String authKey,
			@PathParam("idType") Long idType,
			@QueryParam("env") String envir,
			@HeaderParam("user") String user,
			@QueryParam("cmroc") String cmroc,
			TypeParamsModificationDto typeParamsModificationDto) throws GedeException {

		TypeParamsDto typeParamsDto = typeService.saveOrganismTypeParameters(cmroc, envir, user, idType, typeParamsModificationDto);

		return Response.ok(typeParamsDto).header(
				HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

}
