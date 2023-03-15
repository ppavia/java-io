package fr.cimut.ged.entrant.interrogation.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.CddeCrdeBeanAggregation;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Historique;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.db.User;
import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.exceptions.BadRequestException;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.service.Recherche;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.OrganismeHelper;

/**
 * Recriture de l'interface CDDE 072016 : stop des appels EJB => passage vers du rest
 * 
 * @author jlebourgocq
 *
 */
@Stateless
@Path("/cdde")
@Interceptors({ RestRequestInterceptor.class })
public class CddeEndpoint extends EndpointAbstract {

	@EJB
	Manager oracle;

	@EJB
	Recherche mongoDB;

	@EJB
	Metier metier;

	private Logger logger = Logger.getLogger(this.getClass());

	private static final String ID_MONGO = "_id";

	private static final List<String> IMMUTABLE = Arrays.asList(ID_MONGO, GlobalVariable.ATTR_DA_ID, GlobalVariable.ATTR_EDDOC_ID,
			GlobalVariable.ATTR_EDDOC_IDS, GlobalVariable.ATTR_ID_ORGANISME, GlobalVariable.ATTR_DTCREATE, GlobalVariable.ATTR_DTINTEGRATION);

	private static final List<String> REQUIRED = Arrays.asList(GlobalVariable.ATTR_ID, GlobalVariable.ATTR_ID_ORGANISME);

	/**
	 * implémentation du connecteur REST chargé d'aiguiller vers les méthodes applicables
	 */
	// TODO attention aux attributs transactionnels
	@POST
	@Produces("application/json; charset=UTF-8")
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Response performAction(@HeaderParam("Authorization") String authKey, @QueryParam("env") String envir, @QueryParam("action") String action,
			CddeCrdeBeanAggregation beanAggregation)
			throws GedeException {

		//Vérification de la validité de l'environnement
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);

		logger.info("aiguillage vers " + action);

		Object objReturn = null;

		logger.info(beanAggregation.toString());

		// aiguillage vers les méthodes adaptées
		if ("listIndexDocument".equals(action)) {
			objReturn = listIndexDocument(environnement, beanAggregation.getParametersCdde());
		} else if ("listString".equals(action)) {
			objReturn = listString(environnement, beanAggregation.getParametersCdde());
		} else if ("get".equals(action)) {
			objReturn = get(beanAggregation.getId(), beanAggregation.getUser());
		} else if ("getHistoriques".equals(action)) {
			objReturn = getHistoriques(beanAggregation.getId());
		} else if ("stats".equals(action)) {
			objReturn = stats(environnement, beanAggregation.getParametersCdde());
		} else if ("update".equals(action)) {
			objReturn = update(environnement, beanAggregation.getParametersCdde(), beanAggregation.getUser());
		} else if ("status".equals(action)) {
			objReturn = status(environnement, beanAggregation.getParametersCdde(), beanAggregation.getUser());
		} else if ("getParameter".equals(action)) {
			objReturn = getParameter(environnement, beanAggregation.getCmroc(), beanAggregation.getParametersCdde());
		} else if ("deleteSude".equals(action)) {
			deleteSude(environnement, beanAggregation.getEddocIds(), beanAggregation.getUser());
		} else if ("updateStatusSude".equals(action)) {
			updateStatusSude(environnement, beanAggregation.getEddocIds(), beanAggregation.getUser());
		} else {
			throw new BadRequestException("Appel du Endpoint CDDE avec une action inconnue : " + action);
		}

		if (null == objReturn) {
			// certaines méthodes retournent void
			return Response.noContent().build();
		} else {
			return Response.ok(objReturn).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		}

	}

	private List<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> listIndexDocument(String environnement, Map<String, String> parameters) {

		List<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> listDocs = null;
		String listJson = mongoDB.recherche(environnement, parameters);

		if (null != listJson) {
			// deserialisation Json
			ObjectMapper mapper = new ObjectMapper();
			try {
				listDocs = Arrays.asList(mapper.readValue(listJson, fr.cimut.ged.entrant.beans.mongo.DocumentMongo[].class));
			} catch (Exception e) {
				logger.error("error while deserializing json list : " + listJson, e);
			}
		}

		return listDocs;
	}

	private String listString(String environnement, Map<String, String> parameters) {
		return mongoDB.recherche(environnement, parameters);
	}

	private Document get(String id, User user) {
		Document document = oracle.get(id);
		if (document == null) {
			return null;
			//throw new RuntimeException("Aucun document trouv&eacute;");
		}
		try {
			if (DocumentHelper.checkRight(document, user)) {
				return document;
			} else {
				return null;
			}
		} catch (CimutConfException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Historique> getHistoriques(String id) {
		return oracle.getHistoriques(id);
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private Document update(String environnement, Map<String, String> parameters, User user) {
		logger.debug("update(" + parameters.toString() + ")");

		// on cherche le doc dans la base
		Document oldDocument = null;
		if (parameters.containsKey(GlobalVariable.ATTR_ID)) {
			oldDocument = oracle.get(parameters.get(GlobalVariable.ATTR_ID));
		} else {
			throw new RuntimeException("Aucun identifiant fournis !");
		}
		if (oldDocument == null) {
			throw new RuntimeException("Document introuvable : " + parameters.get("ID_DOC"));
		}

		// on verifie que le user peux consulter le document
		try {
			if (!DocumentHelper.checkRight(oldDocument, user)) {
				throw new RuntimeException("Droit insuffisant : " + parameters.get(GlobalVariable.ATTR_ID));
			}
		} catch (CimutConfException e1) {
			throw new RuntimeException(e1);
		}

		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonColumn = mapper.createObjectNode();
			for (Entry<String, String> param : parameters.entrySet()) {
				String key = param.getKey();
				String value = param.getValue();
				if (!IMMUTABLE.contains(key)) {
					jsonColumn.put(key, value);
				}
			}

			Document newDocument = new Document();
			newDocument.setId(oldDocument.getId());
			newDocument.getJson().setData(mapper.writeValueAsString(jsonColumn));

			// on met a jour oracle
			newDocument = oracle.replace(oldDocument, newDocument, user.getLogin(), environnement);

			// on met a jour mongoDB
			mongoDB.update(environnement, newDocument.getJson().getData());

			// on retourne notre nouveau doc
			return newDocument;

		} catch (Exception e) {
			logger.error("commentaire manquant", e);
			throw new RuntimeException(e);
		}

	}

	/**
	 * Update status of the documents
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private Document status(String environnement, Map<String, String> map, User user) {

		Map<String, String> parameters = new HashMap<String, String>();

		if (map.containsKey(GlobalVariable.ATTR_ID)) {
			parameters.put(GlobalVariable.ATTR_ID, map.get(GlobalVariable.ATTR_ID));
		} else if (map.containsKey(GlobalVariable.ATTR_EDDOC_ID)) {
			// TODO no way to do better than this ?
			String id = mongoDB.idFromEddoc(environnement, map.get(GlobalVariable.ATTR_EDDOC_ID), user.getCmroc());
			parameters.put(GlobalVariable.ATTR_ID, id);
		} else {
			throw new RuntimeException("Aucun identifiant transmit");
		}

		if (map.containsKey(GlobalVariable.ATTR_STATUS)) {
			parameters.put(GlobalVariable.ATTR_STATUS, map.get(GlobalVariable.ATTR_STATUS));
		}
		if (map.containsKey(GlobalVariable.ATTR_COMMENTAIRES)) {
			parameters.put(GlobalVariable.ATTR_COMMENTAIRES, map.get(GlobalVariable.ATTR_COMMENTAIRES));
		}
		if (parameters.size() < 2) {
			throw new RuntimeException("Aucune mise à jour requise");
		}
		return this.update(environnement, parameters, user);
	}

	/**
	 * get the stats
	 */
	private String stats(String environnement, Map<String, String> parameters) {
		try {
			return mongoDB.statistiques(environnement, parameters);
		} catch (Exception e) {
			logger.error("commentaire manquant", e);
			return null;
		}
	}

	/**
	 * get the parameters to build the rules
	 */
	private Parameter getParameter(String environnement, String cmroc, Map<String, String> search) {
		try {
			return mongoDB.getParameters(environnement, cmroc, search);
		} catch (Exception e) {
			logger.error("commentaire manquant", e);
			return null;
		}
	}

	/**
	 * Update status of the documents
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void updateStatusSude(String environnement, List<String> eddocIds, User user) {
		if (eddocIds == null || eddocIds.isEmpty()) {
			throw new RuntimeException("Aucun identifiant transmit");
		} else {
			for (String string : eddocIds) {
				if (!string.matches("\\d{12}_\\d{14}")) {
					throw new RuntimeException("Identifiant invalid");
				}
			}
		}

		for (String eddocId : eddocIds) {

			try {

				Document document = oracle.getByEddocId(eddocId);

				if (document == null) {
					logger.warn("document non trouvé pour mise a jour du status depuis SUDE " + eddocId + " (" + user.getCmroc() + ", "
							+ user.getLogin() + ")");
				} else if (document.getJson() != null) {
					if (OrganismeHelper.getTutelles(user.getCmroc()).contains(document.getCmroc())) {
						document.setStatus("Traité");
						fr.cimut.ged.entrant.beans.mongo.DocumentMongo docMongo = DocumentHelper.getDocMongoFromJson(document);
						docMongo.setStatus(document.getStatus());

						if (docMongo.getId() != null && !docMongo.getId().isEmpty()) {

							String strJson = DocumentHelper.stringify(docMongo);

							Json js = new Json();
							js.setId(document.getId());
							js.setOrganisme(document.getCmroc());
							js.setData(strJson);
							document.setJson(js);

							try {
								mongoDB.update(environnement, strJson);
							} catch (Exception e) {

							}
						}
						oracle.updateWithoutTransac(document);
					} else {
						logger.fatal("User " + user.getLogin() + " de " + user.getCmroc() + " n'a pas les droits pour mettre a jour le document : "
								+ document.getId() + " rattaché au cmroc  " + document.getCmroc() + ". eddocid :  " + eddocId);
					}
				} else {
					logger.warn("document trouvé mais pas de json a mettre a jour" + eddocId + " (" + user.getCmroc() + ", " + user.getLogin() + ")");
				}
			} catch (Exception e) {
				logger.fatal("Erreur de mise a jour du status pour l'eddoc id : " + eddocId + " " + user.getCmroc() + " " + user.getLogin(), e);
			}
		}

	}

	/**
	 * Update status of the documents
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	private void deleteSude(String environnement, List<String> eddocIds, User user) {
		if (eddocIds.isEmpty()) {
			throw new RuntimeException("Aucun identifiant transmit");
		} else {
			for (String string : eddocIds) {
				if (!string.matches("\\d{12}_\\d{14}")) {
					throw new RuntimeException("Identifiant invalid");
				}
			}
		}

		try {
			for (String eddocId : eddocIds) {
				Document document = oracle.getByEddocId(eddocId);

				if (document != null) {
					if (OrganismeHelper.getTutelles(user.getCmroc()).contains(document.getCmroc())) {
						document.setStatus("delete");
						oracle.updateWithoutTransac(document);
						EddmManager eddmManager = metier.getEddmManager();
						eddmManager.remove(eddocId, user.getCmroc(), environnement);
					} else {
						throw new Exception("n'a pas les droits pour effacer le document : " + document.getId() + " " + user.getCmroc() + " pas dans "
								+ document.getCmroc());
					}
				}
			}
		} catch (Exception e) {
			logger.fatal("commentaire manquant", e);
			throw new RuntimeException(e);
		}
	}

}
