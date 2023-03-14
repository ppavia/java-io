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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import fr.cimut.ged.entrant.beans.db.Categorie;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.CategorieService;
import fr.cimut.ged.entrant.service.TypeService;

/**
 * 
 */
@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/categorie")
public class CategorieEndpoint extends EndpointAbstract {

	@EJB
	CategorieService categorieService;

	@EJB
	TypeService typeService;

	@POST
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response create(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir,
			@HeaderParam("user") String user, Categorie categorieDto) throws GedeException {
		if (user != null) {
			categorieDto.setUserMaj(user);
		}
		Categorie categoriPersisted = categorieService.create(categorieDto, cmroc);
		return Response.ok(categoriPersisted).build();
	}

	@DELETE
	@Path("/{id:[0-9]+}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response delete(@HeaderParam("Authorization") String authKey, @PathParam("id") Long id, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @HeaderParam("user") String user) throws GedeException {
		categorieService.delete(id, cmroc);
		return Response.ok().build();
	}

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response list(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir) {
		final List<Categorie> list = categorieService.list();
		return Response.ok(list).build();
	}

	@PUT
	@Path("/{idCategorie:[0-9]+}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("Authorization") String authKey, @QueryParam("env") String envir, @QueryParam("cmroc") String cmroc,
			@PathParam("idCategorie") Long idCategorie, @HeaderParam("user") String user, Categorie categorieDto) throws GedeException {
		checkIdMatch(idCategorie, categorieDto.getId());
		if (user != null) {
			categorieDto.setUserMaj(user);
		}
		Categorie categoriPersisted = categorieService.update(categorieDto, cmroc);
		return Response.ok(categoriPersisted).build();
	}

	@POST
	@Path("/{idCategorie:[0-9]+}/type")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response createType(@HeaderParam("Authorization") String authKey, @PathParam("idCategorie") Long idCategorie,
			@QueryParam("cmroc") String cmroc, @QueryParam("env") String envir, @HeaderParam("user") String user, Type typeDto) throws GedeException {
		if (user != null) {
			typeDto.setUserMaj(user);
		}
		TypeDto typePersisted = typeService.create(idCategorie, typeDto, cmroc);
		ResponseBuilder resp = Response.ok(typePersisted);
		return resp.build();
	}

	@DELETE
	@Path("/{idCategorie:[0-9]+}/type/{idType:[0-9]+}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response deleteType(@HeaderParam("Authorization") String authKey, @PathParam("idCategorie") Long idCategorie,
			@PathParam("idType") Long idType, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir, @HeaderParam("user") String user)
			throws GedeException {
		typeService.delete(idType, cmroc);
		return Response.ok().build();
	}

	@GET
	@Path("/{idCategorie:[0-9]+}/type")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response listTypes(@HeaderParam("Authorization") String authKey, @PathParam("idCategorie") Long idCategorie,
			@QueryParam("cmroc") String cmroc, @QueryParam("env") String envir) throws CimutConfException {
		final List<TypeDto> list = typeService.list(idCategorie, cmroc, envir);
		return Response.ok(list).build();
	}

	@PUT
	@Path("/{idCategorie:[0-9]+}/type/{idType:[0-9]+}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response updateType(@HeaderParam("Authorization") String authKey, @PathParam("idCategorie") Long idCategorie,
			@PathParam("idType") Long idType, @QueryParam("env") String envir, @HeaderParam("user") String user, @QueryParam("cmroc") String cmroc,
			Type typeDto) throws GedeException {
		if (user != null) {
			typeDto.setUserMaj(user);
		}
		checkIdMatch(idType, typeDto.getId());
		TypeDto typePersisted = typeService.update(typeDto, cmroc);
		return Response.ok(typePersisted).build();
	}

}
