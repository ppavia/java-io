package fr.cimut.ged.entrant.interrogation.rest;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.beans.CddeCrdeBeanAggregation;
import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.exceptions.BadRequestException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.Rules;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;

/**
 * Recriture de l'interface CRDEE 072016 : stop des appels EJB => passage vers du rest
 * 
 * @author jlebourgocq
 *
 */
@Stateless
@Path("/crde")
@Interceptors({ RestRequestInterceptor.class })
public class CrdeEndpoint extends EndpointAbstract {

	@EJB
	Rules crde;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * implémentation du connecteur REST chargé d'aiguiller vers les méthodes applicables
	 */
	// TODO attention aux attributs transactionnels
	@POST
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Response performAction(@HeaderParam("Authorization") String authKey, @QueryParam("env") String envir, @QueryParam("action") String action,
			CddeCrdeBeanAggregation beanAggregation) throws GedeException {

		//Vérification de la validité de l'environnement
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		logger.info("aiguillage vers " + action);

		Object objReturn = null;

		logger.info(beanAggregation.toString());

		// aiguillage vers les méthodes adaptées
		if ("insert".equals(action)) {
			objReturn = insert(environnement, beanAggregation.getCmroc(), beanAggregation.getRule());
		} else if ("get".equals(action)) {
			objReturn = get(environnement, beanAggregation.getCmroc(), beanAggregation.getId());
		} else if ("list".equals(action)) {
			objReturn = list(environnement, beanAggregation.getCmroc(), beanAggregation.getParametersCrde());
		} else if ("getHist".equals(action)) {
			objReturn = getHist(environnement, beanAggregation.getCmroc(), beanAggregation.getRule());
		} else if ("remove".equals(action)) {
			remove(environnement, beanAggregation.getCmroc(), beanAggregation.getId());
		} else if ("update".equals(action)) {
			update(environnement, beanAggregation.getCmroc(), beanAggregation.getRule());
		} else {
			throw new BadRequestException("Appel du Endpoint CDDE avec une action inconnue : " + action);
		}

		if (null == objReturn) {
			// certaines méthodes retournent void
			return Response.noContent().build();
		} else {
			// fix problèe d'encodage
			return Response.ok(objReturn).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		}

	}

	private Rule insert(String environnement, String cmroc, Rule rule) {
		// Vérification que le CMROC est défini dans la Rule
		if (rule.getCmroc() == null || rule.getCmroc().isEmpty()) {
			rule.setCmroc(cmroc);
		}

		return crde.insert(environnement, cmroc, rule);
	}

	private Rule get(String environnement, String cmroc, String id) {
		return crde.get(environnement, cmroc, id);
	}

	private List<Rule> list(String environnement, String cmroc, Map<String, List<String>> parameters) {
		return crde.list(environnement, cmroc, parameters);
	}

	private void remove(String environnement, String cmroc, String id) {
		crde.remove(environnement, cmroc, id);
	}

	private void update(String environnement, String cmroc, Rule rule) {
		// Vérification que le CMROC est défini dans la Rule
		if (rule.getCmroc() == null || rule.getCmroc().isEmpty()) {
			rule.setCmroc(cmroc);
		}

		crde.update(environnement, cmroc, rule);
	}

	private List<RuleHistorique> getHist(String environnement, String cmroc, Rule rule) {

		return crde.getHistorique(environnement, cmroc, rule.getId());
	}

}
