package fr.cimut.ged.entrant.interrogation.rest;

import java.util.ArrayList;
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
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

import fr.cimut.ged.entrant.exceptions.AccessForbiddenException;
import fr.cimut.ged.entrant.exceptions.BadRequestException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.interrogation.rest.dto.RequestCompteMailDto;
import fr.cimut.ged.entrant.interrogation.rest.dto.ResponseCompteMailDto;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.service.MailAdminService;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/mailadmin")
public class MailAdminEndPoint extends EndpointAbstract {

	/**
	 * Contrainte de présence ou non applicable à un paramètre d'une requête
	 */
	private static enum ParamConstraint {
		REQUIRED,
		IMMUTABLE
	}

	@EJB
	MailAdminService mailAdminService;

	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response getComptesMail(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc, @QueryParam("email") String email)
			throws GedeException {

		// Vérification de la requête
		checkRequest(cmroc, null, null);

		if (StringUtils.isEmpty(email)) {
			// Si pas d'email : Récupération de tous les comptes mails liés à la mutuelle
			List<CompteMail> compteMailList = mailAdminService.getComptesMail(cmroc);
			List<ResponseCompteMailDto> compteMailDtoList = new ArrayList<ResponseCompteMailDto>();
			for (CompteMail compteMail : compteMailList) {
				compteMailDtoList.add(toResponseCompteMailDto(compteMail));
			}
			return Response.ok(compteMailDtoList).build();
		} else {
			// Si l'email est présent : Récupération du compte mail correspondant pour cette mutuelle
			CompteMail compteMail = mailAdminService.getCompteMail(email, cmroc);
			return Response.ok(toResponseCompteMailDto(compteMail)).build();
		}
	}

	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response createCompteMail(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir,
			RequestCompteMailDto dto) throws GedeException {

		// Vérification de la requête
		checkRequest(cmroc, null, null);
		checkRequestDto(dto, true);
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		// Création du compte mail
		mailAdminService.createCompteMail(toCompteMail(dto, cmroc, environnement));
		return Response.ok("Creation du compte mail : " + dto.getEmail()).build();
	}

	@PUT
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response updateCompteMail(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc,
			@QueryParam("email") String email, RequestCompteMailDto dto) throws GedeException {

		// Vérification de la requête
		checkRequest(cmroc, email, ParamConstraint.REQUIRED);
		checkRequestDto(dto, false);

		// Mise à jour du compte mail
		CompteMail updatedCompteMail = mailAdminService.updateCompteMail(email, cmroc, dto.getLogin(), dto.getPassword(), dto.getInBoxes(),
				dto.getLastUpdateAuthor(), dto.getReportEmails());
		return Response.ok(toResponseCompteMailDto(updatedCompteMail)).build();
	}

	@DELETE
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response deleteCompteMail(@HeaderParam("Authorization") String authKey, @QueryParam("cmroc") String cmroc,
			@QueryParam("email") String email) throws GedeException {

		// Vérification de la requête
		checkRequest(cmroc, email, ParamConstraint.REQUIRED);

		// Suppression du compte mail
		mailAdminService.deleteCompteMail(email);
		return Response.ok("Suppression du compte mail : " + email).build();
	}

	/**
	 * Vérification de la requête : validité de l'appel et des paramètres principaux
	 * 
	 * @param cmroc
	 *            l'identifiant de mutuelle obligatoire
	 * @param email
	 *            l'adresse mail du compte mail
	 * @param emailParamConstraint
	 *            la contrainte de présence de l'email
	 * @throws BadRequestException
	 * @throws AccessForbiddenException
	 */
	private void checkRequest(String cmroc, String email, ParamConstraint emailParamConstraint) throws BadRequestException, AccessForbiddenException {
		// Lance une exception si ce n'est pas le serveur principal qui appelle la requête.
		if (!GlobalVariable.checkIfMasterForScheduledTask()) {
			throw new AccessForbiddenException();
		}
		// Vérifie les paramètres principaux
		checkParameter("cmroc", cmroc, ParamConstraint.REQUIRED);
		checkParameter("email", email, emailParamConstraint);
	}

	/**
	 * Vérification de la validité du DTO de la requête
	 * 
	 * @param dto
	 *            le DTO
	 * @param isNewDataContext
	 *            = true si on est dans un contexte de création du compte mail / = false si c'est une mise à jour
	 * 
	 * @throws BadRequestException
	 */
	private void checkRequestDto(RequestCompteMailDto dto, boolean isNewDataContext) throws BadRequestException {
		checkParameter("email", dto.getEmail(), isNewDataContext ? ParamConstraint.REQUIRED : ParamConstraint.IMMUTABLE);
		checkParameter("host", dto.getHost(), isNewDataContext ? ParamConstraint.REQUIRED : ParamConstraint.IMMUTABLE);
		checkParameter("port", dto.getPort(), isNewDataContext ? ParamConstraint.REQUIRED : ParamConstraint.IMMUTABLE);
		checkParameter("protocole", dto.getProtocole(), isNewDataContext ? ParamConstraint.REQUIRED : ParamConstraint.IMMUTABLE);
		if (isNewDataContext) {
			checkParameter("login", dto.getLogin(), ParamConstraint.REQUIRED);
			checkParameter("password", dto.getPassword(), ParamConstraint.REQUIRED);
		}
		checkTypeListParameter("inBoxes", dto.getInBoxes(), isNewDataContext ? ParamConstraint.REQUIRED : null);
		checkTypeListParameter("reportEmails", dto.getReportEmails(), null);

		checkParameter("author", dto.getLastUpdateAuthor(), ParamConstraint.REQUIRED);
	}

	/**
	 * Vérifie la présence du paramètre selon sa contrainte
	 * 
	 * @param parameterName
	 *            nom du paramètre
	 * @param paramterValue
	 *            valeur du paramètre
	 * @param constraint
	 *            contrainte de présence ou non
	 * @throws BadRequestException
	 */
	private <V> void checkParameter(String parameterName, V parameterValue, ParamConstraint constraint) throws BadRequestException {
		if (constraint != null) {
			switch (constraint) {
			default:
			case IMMUTABLE:
				if (parameterValue != null) {
					throw new BadRequestException(parameterName + " : ce parametre est non modifiable");
				}
				break;
			case REQUIRED:
				if (parameterValue == null || (parameterValue instanceof String && ((String) parameterValue).isEmpty())) {
					throw new BadRequestException(parameterName + " : ce parametre n'est pas present");
				}
				break;
			}
		}
	}

	/**
	 * Vérifie la présence et le contenu d'un paramètre liste selon sa contrainte
	 * 
	 * @param parameterName
	 * @param parameterValue
	 * @param constraint
	 * @throws BadRequestException
	 */
	private <V> void checkTypeListParameter(String parameterName, List<V> parameterValue, ParamConstraint constraint) throws BadRequestException {
		// Vérification standard
		checkParameter(parameterName, parameterValue, constraint);
		if (parameterValue != null) {
			// Vérification du contenu de la liste
			// Si REQUIRED, la liste doit comporter au moins un élément
			if (ParamConstraint.REQUIRED.equals(constraint) && parameterValue.isEmpty()) {
				throw new BadRequestException(parameterName + " : la liste doit comporter au moins un élément");
			}
			// Aucun élément de la liste ne doit être vide.
			for (V element : parameterValue) {
				checkParameter("Element de " + parameterName, element, ParamConstraint.REQUIRED);
			}
		}
	}

	/**
	 * Construit le CompteMail à partir d'un RequestCompteMailDto et des paramètres des requêtes
	 * 
	 * @param dto
	 *            RequestCompteMailDto
	 * @param cmroc
	 *            Identifiant de la mutuelle
	 * @param envir
	 * @return le CompteMail créé
	 */
	private CompteMail toCompteMail(RequestCompteMailDto dto, String cmroc, String envir) {
		CompteMail compteMail = new CompteMail();
		compteMail.setCmroc(cmroc);
		compteMail.setEnvironnement(envir);
		compteMail.setEmail(dto.getEmail());
		compteMail.setHost(dto.getHost());
		compteMail.setPort(dto.getPort());
		compteMail.setProtocole(dto.getProtocole());
		compteMail.setLogin(dto.getLogin());
		compteMail.setPassword(dto.getPassword());
		compteMail.setInBoxes(dto.getInBoxes());
		compteMail.setLastUpdateAuthor(dto.getLastUpdateAuthor());
		compteMail.setRapportEmails(dto.getReportEmails());
		return compteMail;
	}

	/**
	 * Construit le CompteMailDto à partir d'un CompteMail
	 * 
	 * @param compteMail
	 * @return le ResponseCompteMailDto créé
	 */
	private ResponseCompteMailDto toResponseCompteMailDto(CompteMail compteMail) {
		ResponseCompteMailDto dto = new ResponseCompteMailDto();
		dto.setEmail(compteMail.getEmail());
		dto.setLogin(compteMail.getLogin());
		dto.setInBoxes(compteMail.getInBoxes());
		dto.setLastUpdateDate(compteMail.getLastUpdateDate());
		dto.setLastUpdateAuthor(compteMail.getLastUpdateAuthor());
		dto.setReportEmails(compteMail.getRapportEmails());
		dto.setHost(compteMail.getHost());
		dto.setPort(compteMail.getPort());
		dto.setProtocole(compteMail.getProtocole());
		return dto;
	}
}