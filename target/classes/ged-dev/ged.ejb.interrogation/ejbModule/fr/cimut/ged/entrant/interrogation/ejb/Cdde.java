package fr.cimut.ged.entrant.interrogation.ejb;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Historique;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.db.User;
import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.service.Recherche;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.OrganismeHelper;

/**
 * Session Bean implementation class Cdde
 */

@Stateless(mappedName = "Cdde")
@LocalBean
@Deprecated
public class Cdde implements CddeRemote {

	@EJB
	Manager oracle;

	@EJB
	Recherche mongoDB;

	@EJB
	Metier metier;

	private Logger logger = Logger.getLogger(this.getClass());

	private static final String ID_MONGO = "_id";

	public static final List<String> IMMUTABLE = Arrays.asList(ID_MONGO, GlobalVariable.ATTR_DA_ID, GlobalVariable.ATTR_EDDOC_ID,
			GlobalVariable.ATTR_EDDOC_IDS, GlobalVariable.ATTR_ID_ORGANISME, GlobalVariable.ATTR_DTCREATE, GlobalVariable.ATTR_DTINTEGRATION);

	public static final List<String> REQUIRED = Arrays.asList(GlobalVariable.ATTR_ID, GlobalVariable.ATTR_ID_ORGANISME);

	/**
	 * Default constructor.
	 */

	public Cdde() {

	}

	/**
	 * Vérifie la validité de l'environnement fourni
	 * 
	 * @param environnement
	 *            L'environnement à vérifier
	 * @return L'environnement à utiliser
	 * @throws RuntimeException
	 *             Levée si l'environnement fourni est invalide ou si une {@link CimutConfException} est levée
	 * @see EnvironnementHelper#determinerEnvironnement
	 */
	private String checkEnvironnement(String environnement) throws RuntimeException {
		String envirGere = null;
		try {
			envirGere = EnvironnementHelper.determinerEnvironnement(environnement);
		} catch (CimutConfException e) {
			logger.fatal("Erreur de configuration : " + e.getMessage(), e);
			throw new RuntimeException(e);
		}
		if (envirGere == null) {
			logger.fatal("Environnement invalide (non géré) : " + environnement);
			throw new RuntimeException("Environnement invalide (non géré) : " + environnement);
		}
		return envirGere;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> listIndexDocument(String environnement, Map<String, String> parameters, User user) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String listString(String environnement, Map<String, String> parameters, User user) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		return mongoDB.recherche(environnement, parameters);
	}

	@Override
	public Document get(String id, User user) {
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

	@Override
	public List<Historique> getHistoriques(String id, User user) {
		return oracle.getHistoriques(id);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Document update(String environnement, Map<String, String> parameters, User user) {
		logger.debug("update(" + parameters.toString() + ")");

		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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

			//		fr.cimut.ged.entrant.beans.mongo.Document newJson = DocumentHelper.getBean(newDocument);
			//		if (newJson.getStatus() != null && !newJson.getStatus().equals(GlobalVariable.STATUS_ERROR)){
			//			newJson.setErreurDa(null);
			//			newJson.setErreurEddm(null);
			//			newDocument.getJson().setData(DocumentHelper.getJson(newJson));
			//		}

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
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Document status(String environnement, Map<String, String> map, User user) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String stats(String environnement, Map<String, String> parameters, User user) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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
	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Parameter getParameter(String environnement, String cmroc, Map<String, String> search) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateStatusSude(String environnement, List<String> eddocIds, User user) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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

				if (document != null) {

					if (OrganismeHelper.getTutelles(user.getCmroc()).contains(document.getCmroc())) {
						document.setStatus("Traité");
						fr.cimut.ged.entrant.beans.mongo.DocumentMongo json = DocumentHelper.getDocMongoFromJson(document);
						json.setStatus(document.getStatus());

						if (json.getId() != null && !json.getId().isEmpty()) {

							String strJson = DocumentHelper.stringify(json);

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
					logger.warn("document non trouvé pour mise a jour du status depuis SUDE " + eddocId + " (" + user.getCmroc() + ", "
							+ user.getLogin() + ")");
				}
			} catch (Exception e) {
				logger.fatal("Erreur de mise a jour du status pour l'eddoc id : " + eddocId + " " + user.getCmroc() + " " + user.getLogin(), e);
			}
		}

	}

	/**
	 * Update status of the documents
	 */
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteSude(String environnement, List<String> eddocIds, User user) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

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

	// ===========================================================================
	// Anciennes méthodes ne comportant par l'environnement dans leur signature
	// (gardées pour rétro-compatibilité, mais devront être supprimées)
	// L'environnement utilisé est le premier environnement configuré de la liste
	// ===========================================================================

	@Deprecated
	private String getEnvironnementParDefaut() {
		String environnement = null;
		try {
			environnement = EnvironnementHelper.getEnvironnements().get(0);
		} catch (CimutConfException e) {
		}
		return environnement;
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public List<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> listIndexDocument(Map<String, String> parameters, User user) {
		return listIndexDocument(getEnvironnementParDefaut(), parameters, user);
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String listString(Map<String, String> parameters, User user) {
		return listString(getEnvironnementParDefaut(), parameters, user);
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String stats(Map<String, String> parameters, User user) {
		return stats(getEnvironnementParDefaut(), parameters, user);
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Document update(Map<String, String> parameters, User user) {
		return update(getEnvironnementParDefaut(), parameters, user);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Document status(Map<String, String> map, User user) {
		return status(getEnvironnementParDefaut(), map, user);
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Parameter getParameter(String cmroc, Map<String, String> search) {
		return getParameter(getEnvironnementParDefaut(), cmroc, search);
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void deleteSude(List<String> eddocIds, User user) {
		deleteSude(getEnvironnementParDefaut(), eddocIds, user);
	}

	@Override
	@Deprecated
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateStatusSude(List<String> eddocIds, User user) {
		updateStatusSude(getEnvironnementParDefaut(), eddocIds, user);
	}

	//	/**
	//	 * get the parameters to build the rules
	//	 */
	//	@TransactionAttribute(TransactionAttributeType.NEVER)
	//	public List<Document> getDocByRule(String cmroc,String id) {
	//		return mongoDB.getDocByRule(cmroc,id);
	//	}

}
