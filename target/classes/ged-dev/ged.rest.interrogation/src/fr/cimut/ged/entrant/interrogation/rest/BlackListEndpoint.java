package fr.cimut.ged.entrant.interrogation.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.validation.ValidateRequest;

import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.BlackList;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.service.MongoConnection;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;

/**
 * 
 */
@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/blacklist")
public class BlackListEndpoint extends EndpointAbstract {

	@EJB
	MongoConnection mongoConnection;

	@POST
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response create(@HeaderParam("Authorization") String authKey, @QueryParam("env") String envir, BlackList blackList) throws GedeException {

		// Vérification de la validité de l'environnement
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		if (blackList.getCmroc() == null || blackList.getCmroc().isEmpty() || blackList.getEmail() == null || blackList.getEmail().isEmpty()) {
			throw new BadRequestException("objet blacklist invalide");
		}
		return Response.ok(getManager(environnement, blackList.getCmroc()).insert(blackList)).build();

	}

	@DELETE
	@Path("/{id:.*}/{cmroc:\\d\\d\\d\\d}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response delete(@HeaderParam("Authorization") String authKey, @PathParam("id") String id, @PathParam("cmroc") String cmroc,
			@QueryParam("env") String envir) throws GedeException {

		// Vérification de la validité de l'environnement
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		getManager(environnement, cmroc).remove(id);
		return Response.noContent().build();

	}

	@GET
	@Path("/{id:.*}/{cmroc:\\d\\d\\d\\d}")
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response findById(@HeaderParam("Authorization") String authKey, @PathParam("id") String id, @PathParam("cmroc") String cmroc,
			@QueryParam("env") String envir) throws GedeException {

		// Vérification de la validité de l'environnement
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		BlackList blacklist = getManager(environnement, cmroc).get(id);
		if (blacklist == null) {
			Logger.getLogger(this.getClass()).warn("Entrée non trouvee dans la blacklist en base [id: (" + id + "," + cmroc + ")]");
			return Response.status(Status.NOT_FOUND).build();
		}
		return Response.ok(blacklist).build();

	}

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@ValidateRequest
	public Response listAll(@HeaderParam("Authorization") String authKey, @NotNull @QueryParam("email") String email, @QueryParam("page") int page,
			@QueryParam("pageSize") int pageSize, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir) throws GedeException {

		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		BasicDBObject query = new BasicDBObject();
		if (email != null && !email.isEmpty()) {
			email = ".*" + email.replaceAll("\\*+", ".*") + ".*";
			query.put("email", java.util.regex.Pattern.compile(email));
		}
		//List<BlackList> list = getManager(environnement, cmroc).list(query, pageSize, page);
		List<BlackList> list = getManager(environnement, cmroc).list(query);
		return Response.ok(list).build();

	}

	@PUT
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response update(@HeaderParam("Authorization") String authKey, @QueryParam("env") String envir, BlackList blacklist) throws GedeException {

		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		if (blacklist.getId() == null || blacklist.getId().isEmpty()) {
			throw new BadRequestException("objet blacklist invalide");
		}

		getManager(environnement, blacklist.getCmroc()).update(blacklist);
		return Response.ok(blacklist).build();

	}

	/**
	 * Methode permettant a un client hors domaine cimut de tester l accessibilite cross-domaine.
	 * 
	 * @return Les autorisations
	 */
	@OPTIONS
	@Path("{path : .*}")
	public Response validateCrossDomain() {
		return crossDomainResponse();
	}

	/**
	 * Methode permettant a un client hors domaine cimut de tester l accessibilite cross-domaine. Version pour services
	 * racine (sans sous-element d url).
	 * 
	 * @return Les autorisations
	 */
	@OPTIONS
	public Response validateCrossDomainNoParam() {
		return crossDomainResponse();
	}

	private Response crossDomainResponse() {
		ResponseBuilder response = Response.noContent();
		response.header("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE");
		response.header("Access-Control-Max-Age", "3600");
		response.header("Access-Control-Allow-Headers",
				"x-requested-with, " + "Authorization," + " content-type, " + "If-Modified-Since, " + "Cache-Control, " + "Pragma");
		return response.build();
	}

	private Manager<BlackList> getManager(String environnement, String cmroc) throws CimutMongoDBException {
		return MongoManagerFactory.getBlackListManager(environnement, cmroc, mongoConnection.getMongoClient());
	}

}
