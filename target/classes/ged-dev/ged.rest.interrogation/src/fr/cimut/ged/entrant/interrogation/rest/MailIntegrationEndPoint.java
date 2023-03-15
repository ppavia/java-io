package fr.cimut.ged.entrant.interrogation.rest;

import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.mail.EmailIntegResult;
import fr.cimut.ged.entrant.service.CompteMailFileService;
import fr.cimut.ged.entrant.service.MailIntegrationService;

@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/mailinteg")
@TransactionAttribute(value = TransactionAttributeType.NEVER)
public class MailIntegrationEndPoint extends EndpointAbstract {

	@EJB
	CompteMailFileService fileService;

	@EJB
	MailIntegrationService integService;

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response testMailIntegration(@HeaderParam("Authorization") String authKey, @QueryParam("email") String email,
			@QueryParam("emailsCountMax") Integer emailsCountMax) throws GedeException {
		CompteMail compteMail = fileService.getCompteMailFromFileByEmail(email);
		Map<String, EmailIntegResult> result = integService.checkMailBox(compteMail, emailsCountMax);
		return Response.ok(result).build();
	}
}