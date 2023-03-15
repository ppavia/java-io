package fr.cimut.ged.entrant.interrogation.rest;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.DocumentMongoService;
import fr.cimut.ged.entrant.service.DocumentService;

/**
 * Recherche un eddoc depuis des criteres
 * 
 * @author gyclon
 * @Deprecated utiliser documentEndpoint, deprecated et non supprimé car utilisé depuis plusieurs applications (alto,
 *             starweb, extranet)
 */
@Stateless
@Path("/recherche")
@Interceptors({ RestRequestInterceptor.class })
@Deprecated
public class RechercheEddocEndpoint extends EndpointAbstract {


	@EJB
	DocumentService documentService;

	@EJB DocumentMongoService documentMongoService;

	@GET
	@Produces(javax.ws.rs.core.MediaType.TEXT_HTML)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Response findByIdsBis(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc,
			@QueryParam("codeProduit") String codeProduit, @QueryParam("identifiantPack") String identifiantPack,
			@QueryParam("env") String environnement) throws GedeException {
		return findByIds(authKey, cmroc, codeProduit, identifiantPack, environnement);
	}

	@GET
	@Path("/{cmroc:.*}/{codeProduit:.*}/{identifiantPack:.*}")
	@Produces(javax.ws.rs.core.MediaType.TEXT_HTML)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Response findByIds(@HeaderParam("Authorization") String authKey, @PathParam("cmroc") String cmroc,
			@PathParam("codeProduit") String codeProduit, @PathParam("identifiantPack") String identifiantPack, @QueryParam("env") String envir)
			throws GedeException {

		String eddocId = documentMongoService.getEddocId(cmroc, codeProduit, identifiantPack, envir);
		return Response.ok(eddocId).build();
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
		response.header("Access-Control-Allow-Methods", "GET, OPTIONS");
		response.header("Access-Control-Max-Age", "3600");
		response.header("Access-Control-Allow-Headers",
				"x-requested-with, " + "Authorization," + " content-type, " + "If-Modified-Since, " + "Cache-Control, " + "Pragma");
		return response.build();
	}
}
