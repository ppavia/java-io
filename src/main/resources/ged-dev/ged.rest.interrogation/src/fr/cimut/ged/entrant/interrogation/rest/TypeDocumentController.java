package fr.cimut.ged.entrant.interrogation.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.dto.TypeModificationDto;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.TypeService;

/**
 * 
 */
@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/types")
public class TypeDocumentController {
	
	@EJB
	TypeService typeService;
	
	@POST
	@Path("/{id}")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response partialUpdateType(@HeaderParam("Authorization") String authKey, @PathParam("id") Long idType, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @HeaderParam("user") String user, TypeModificationDto typeData) throws Exception {
		TypeDto type = typeService.partialUpdate(idType, cmroc, typeData, user);
		return Response.ok(type).build();
	}
	
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response listAllTypes(@HeaderParam("Authorization") String authKey,
			@QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir,
			@QueryParam("page") Integer pageNumber,
			@QueryParam("size") Integer sizePage,
			@QueryParam("code") String codeType,
			@QueryParam("libelle") String libelleType)
			throws CimutConfException {
		List<TypeDto> list = typeService.listByPageAndSize(cmroc, envir, pageNumber,  sizePage, codeType, libelleType);
		return Response.ok(list).build();
	}
	
	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response createTypeWithDefaults(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @HeaderParam("user") String user, TypeModificationDto typeData) throws Exception {
		TypeDto type = typeService.createWithDefaults(cmroc, typeData, user);
		return Response.ok(type).build();
	}

	@DELETE
	@Path("/{id}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response deleteType(@HeaderParam("Authorization") String authKey, @PathParam("id") Long idType, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @HeaderParam("user") String user) throws GedeException {
		typeService.delete(idType, cmroc);
		return Response.ok().build();
	}

}
