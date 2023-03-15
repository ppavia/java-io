/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.indexation.ejb;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.appelmetier.SudeManager;
import fr.cimut.ged.entrant.beans.IntegrationReport;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.appelmetier.Eddm;
import fr.cimut.ged.entrant.beans.appelmetier.Sude;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.service.DocumentService;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.mongo.DocumentQueryBuilder;
import fr.cimut.multicanal.web.client.beans.Destinataire;
import fr.cimut.multicanal.web.client.beans.Emetteur;
import fr.cimut.multicanal.web.client.beans.Job;
import fr.cimut.multicanal.web.client.beans.Mail;
import fr.cimut.multicanal.web.client.beans.PieceJointe;

/**
 * Class pour cree les Demande Assurées
 * 
 * @author gyclon
 *
 */
public class PoolerHelper {

	private Logger LOGGER = Logger.getLogger(PoolerHelper.class);
	/**
	 * Ejb pour la base oracle
	 */
	private DocumentService documentService;

	/**
	 * Accesseur mongoDB
	 */
	private InteractionMongo inter;

	private Metier metier;

	private final SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");

	/**
	 * Default constructor.
	 * 
	 * @param interactionMongo
	 * @param ejbBd
	 * @param metier
	 */
	public PoolerHelper(DocumentService documentService, InteractionMongo interactionMongo, Metier metier) {
		this.documentService = documentService;
		this.inter = interactionMongo;
		this.metier = metier;
	}

	/**
	 * realise l'appel EDDM de ceux dont le status a ete changer de "En erreur" à "A traiter"
	 * 
	 * @param documentManager
	 * @param environnement
	 * @throws CimutMongoDBException
	 * @throws GedeCommonException
	 * @throws CimutConfException
	 * @throws CimutFileException
	 */
	public void processFailedEDDM(Manager<DocumentMongo> documentManager, String environnement)
			throws CimutMongoDBException, CimutFileException, CimutConfException, GedeCommonException {

		LOGGER.debug("processFailedEDDM");

		BasicDBObject queryDoc = DocumentQueryBuilder.getQueryForFailedEDDM();
		DBCursor cursor = documentManager.getCursor(queryDoc);
		ObjectMapper mapper = new ObjectMapper();

		while (cursor.hasNext()) {
			String json = cursor.next().toString();
			DocumentMongo documentMongo = null;

			if (json != null) {

				try {
					documentMongo = mapper.readValue(json, DocumentMongo.class);
				} catch (Exception e2) {
					throw new CimutMongoDBException(e2);
				}

				fr.cimut.ged.entrant.beans.db.Document documentDb = documentService.get(documentMongo.getId());
				if (documentDb == null) {
					throw new CimutMongoDBException("Impossible de trouver l'occurence : " + documentMongo.getId());
					// FIXME remove occurence here from mongoDB
				}

				// on recupere le json depuis la base, c'est la base qui est la plus a jour ...
				try {
					documentMongo = DocumentHelper.getDocMongoFromJson(documentDb);
				} catch (CimutDocumentException e2) {
					throw new CimutMongoDBException(e2);
				}

				String eddocId = documentMongo.getEddocId();
				if (eddocId == null || eddocId.isEmpty()) {
					try {

						// on cree un eddocId
						EddmManager eddmManager = metier.getEddmManager();
						Eddm eddm = eddmManager.create(documentDb, metier, environnement);

						if (eddm.getTypeEntite() == TypeEntite.PERSONNE) {
							documentMongo.setAssuInsee(eddm.getInsee());
							documentMongo.setNumAdherent(eddm.getSassu());
						}
						// integre l'eddocId au json et a la base
						documentMongo.setEddocId(eddm.getDocId());
						documentMongo.setErreurEddm(null);
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
						if (documentMongo != null) {
							documentDb.setTsstar(0);
							documentDb.setIdstar(0);
							documentMongo.setStatus(GlobalVariable.STATUS_ERROR);
							documentDb.setStatus(GlobalVariable.STATUS_ERROR);
							documentMongo.setErreurEddm(e.getMessage());
							documentMongo.setEddocId(null);
						}
					} finally {
						try {
							documentDb.getJson().setData(DocumentHelper.stringify(documentMongo));
							documentService.update(documentDb);
							documentManager.update(documentMongo);
						} catch (CimutDocumentException e) {
							throw new CimutMongoDBException(e);
						}
					}
				}
			}
		}
	}

	/**
	 * Cree une DA pour tous les document correspondant a la regle, env, cmroc
	 * 
	 * @param rule
	 * @param documentManager
	 * @param environnement
	 * @throws CimutMongoDBException
	 * @throws CimutConfException
	 * @throws GedeCommonException
	 * @throws CimutFileException
	 */
	public void processRule(Rule rule, Manager<DocumentMongo> documentManager, String environnement)
			throws CimutMongoDBException, CimutConfException, CimutFileException, GedeCommonException {

		LOGGER
				.debug("name : " + rule.getName() + " P:" + rule.getPriority() + " Actif:" + ((rule.isActif()) ? "Oui" : "Non"));
		BasicDBObject queryDoc = DocumentQueryBuilder.getQueryFromRuleForDA(rule);

		// on ne traite jamais les regle qui s'applique aux emails ici.
		if (queryDoc.containsField(GlobalVariable.ATTR_DESTINATAIRE)) {
			return;
		}

		DBCursor cursor = documentManager.getCursor(queryDoc);
		RuleDa ruleDa = rule.getService();
		// on ne traite que les regles qui ont un service. sinon on ne peux pas cree la SUDE.
		if (ruleDa != null && ruleDa.getId() != null) {

			while (cursor.hasNext()) {
				// on boucle sur tous les documents mongoDB qui match la regle en cours
				DBObject cur = cursor.next();
				if (cur != null) {
					String json = cur.toString();
					if (json != null) {
						try {
							callMetier(rule, json, documentManager, environnement);
						} catch (CimutMetierException e) {
							LOGGER.error("commentaire manquant", e);
						}
					}
				}

			}
		}
	}

	public void callMetier(Rule rule, String json, Manager<DocumentMongo> docMongoManager, String environnement)
			throws CimutConfException, CimutMetierException, CimutFileException, GedeCommonException {

		DocumentMongo docMongo = null;
		ObjectMapper mapper = new ObjectMapper();
		IntegrationReport report = new IntegrationReport();
		report.setDate(new Date());

//		Document documentDb = null;
		List<Document> listDocumentsDb = new ArrayList<Document>();

		try {

			SudeManager daManager = metier.getSudeManager();
			docMongo = mapper.readValue(json, DocumentMongo.class);
			if (docMongo.getEddocId() != null && (docMongo.getEddocIds() == null || docMongo.getEddocIds().isEmpty())) {
				listDocumentsDb.add(documentService.getByEddocId(docMongo.getEddocId()));
			} else if (docMongo.getEddocIds() != null && !docMongo.getEddocIds().isEmpty()) {
				for(String eddocId : docMongo.getEddocIds()) {
					listDocumentsDb.add(documentService.getByEddocId(eddocId));
				}
			}
			// met a jour la base de donnée Oracle
			if (listDocumentsDb.isEmpty()) {
				// on a perdu l'occurrence dans la base oracle,
				//Je set le status Da en erreur pour ne pas repasser dessus plus tard
				// Sinon on reboucle dessus à l'infini.
				docMongo.setErreurDa("Impossible de trouver la donnée en base Oracle.");
				docMongo.setStatus(GlobalVariable.STATUS_ERROR);
				docMongoManager.update(docMongo);
				throw new CimutMetierException("Impossible de retrouver le bean : " + docMongo.getId() + " dans la base Oracle");
			}

			
			report.setDocumentName(listDocumentsDb.get(0).getId());
			// convert
			RuleDa service = rule.getService();

			RuleDa service_ = new RuleDa();
			service_.setId(service.getId());
			service_.setName(service.getName());
			service_.setSujet(service.getSujet());
			service_.setSupport(service.getSupport());
			service_.setType(service.getType());
			service_.setCategorie(service.getCategorie());
			// ask metier SUDE
			Sude da = daManager.create(service_, docMongo, listDocumentsDb, environnement);
			report.setInformations("RuleDa : " + rule.getName() + " - " + "SUDE : " + da.getId());
			report.setOK(true);
			docMongo.setErreurDa(null);
			// met à jour la table MongoDb
			docMongo.setSudeId(da.getId());
			for(Document docDb : listDocumentsDb) {
				docDb.getJson().setData(DocumentHelper.stringify(docMongo));
				documentService.update(docDb);
			}
			docMongoManager.update(docMongo);
			LOGGER.info("created DA : " + da.getId());
			LOGGER.info(report.toString());
		} catch (CimutConfException e) {
			// on arrete la tache totalement et on envoie un mail ...
			throw new CimutConfException(e);
		} catch (Exception e) {
			report.setOK(false);
			if (docMongo != null) {
				String messageError = rule.getName() + " => ";
				String erreurMsg = messageError + e.getMessage();
				report.setInformations(erreurMsg);
				LOGGER.error("Erreur lors de la creation de la DA", e);
				LOGGER.error(report.toString());
				docMongo.setErreurDa(erreurMsg);
				docMongo.setStatus(GlobalVariable.STATUS_ERROR);
				try {
					
					for(Document docDb : listDocumentsDb) {
						docDb.setStatus(GlobalVariable.STATUS_ERROR);
						docDb.getJson().setData(DocumentHelper.stringify(docMongo));
						docDb.getJson().setData(DocumentHelper.stringify(docMongo));
						// update MongoDB, only the status and Da_erreur libelle
						documentService.update(docDb);
					}

					
					docMongoManager.update(docMongo);
				} catch (CimutMongoDBException e1) {
					LOGGER.error("Impossible de changer le status en Erreur", e1);
				} catch (CimutDocumentException e1) {
					LOGGER.error("Impossible de changer le status en Erreur", e1);
				}
			}
			throw new CimutMetierException(e);
		}
	}

	/**
	 * Boucle sur les schema pour inserer les DAs
	 * 
	 * @param environnement
	 *            Nom de l'environnement
	 * @param cmroc
	 *            CMROC sur lequel le traitement est effectué
	 * @throws CimutConfException
	 * @throws GedeCommonException
	 * @throws CimutFileException
	 */
	public void processBaseName(String environnement, String cmroc) throws CimutConfException, CimutFileException, GedeCommonException {
		try {
			Manager<DocumentMongo> documentManager = MongoManagerFactory.getDocumentManager(environnement, cmroc, inter);

			//processFailedEDDM(documentManager);
			// recupere les regles pour l'env et le cmroc
			Manager<Rule> ruleManager = MongoManagerFactory.getRuleManager(environnement, cmroc, inter);
			BasicDBObject sort = new BasicDBObject();
			sort.put("priority", 1);
			sort.put("name", -1);
			BasicDBObject query = new BasicDBObject();
			query.put("actif", true);
			List<Rule> listing = ruleManager.list(query, sort);
			// pour chacune des regles, on va chercher dans mongo DB celles qui corresponde
			for (Rule rule : listing) {
				processRule(rule, documentManager, environnement);
			}
		} catch (CimutMongoDBException e1) {
			Logger.getLogger(PoolerMdb.class).fatal("Erreur sur l'environnement " + environnement + " " + e1.getMessage(), e1);
		}
	}

	/**
	 * Methode permettant d'envoyer un mail recapitulatif des documents traité dans la journée. à 8:00 du lundi au
	 * vendredi.
	 * 
	 * @param environnement
	 * @param cmroc
	 * @throws CimutConfException
	 * @throws CimutMongoDBException
	 */
	public void processBaseNameMail(String environnement, String cmroc) throws CimutConfException, CimutMongoDBException {

		Manager<DocumentMongo> documentManager = MongoManagerFactory.getDocumentManager(environnement, cmroc, inter);

		Manager<Rule> ruleManager = MongoManagerFactory.getRuleManager(environnement, cmroc, inter);
		BasicDBObject sort = new BasicDBObject();
		sort.put("priority", -1);
		sort.put("name", -1);
		BasicDBObject query = new BasicDBObject();
		query.put("actif", true);
		List<Rule> listing = ruleManager.list(query, sort);
		for (Rule rule : listing) {
			List<String> mails = rule.getMails();
			if (mails != null && !mails.isEmpty()) {
				processRuleMail(rule, documentManager);
			}
		}
	}

	public void processRuleMail(Rule rule, Manager<DocumentMongo> documentManager) throws CimutMongoDBException, CimutConfException {

		LOGGER
				.debug("name : " + rule.getName() + " P:" + rule.getPriority() + " Actif:" + ((rule.isActif()) ? "Oui" : "Non"));
		BasicDBObject queryDoc = DocumentQueryBuilder.getQueryFromRuleForMail(rule);

		DBCursor cursor = documentManager.getCursor(queryDoc);

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Traitement de la règle : " + rule.getName() + "\n\n");
		stringBuilder.append("Critères de séléction : \n");
		List<RuleCriteres> listCriteres = rule.getCriteres();
		for (RuleCriteres critere : listCriteres) {
			stringBuilder.append(critere.getId() + " " + critere.getParameters().toString() + "\n\n\n");
		}

		boolean isEmpty = true;

		stringBuilder.append("<table><tr><th>Identifiant</th><th>Type dossier</th><th>Statut</th><th>Demande assuré</th></tr>");

		while (cursor.hasNext()) {
			String json = cursor.next().toString();
			if (json != null) {
				try {
					// FIXME : updater le bean qu'aprés un envoie du mail en success !
					DocumentMongo document = callMail(rule, json, documentManager);
					stringBuilder.append("<tr><td>" + document.getId() + "</td><td>" + document.getTypeDocument() + "</td><td>" + document.getStatus()
							+ "</td><td>" + ((document.getSudeId() == null) ? "Aucune DA" : document.getSudeId()) + "</td></tr>");
					isEmpty = false;
				} catch (CimutMetierException e) {
					LOGGER.error("commentaire manquant", e);
				}
			}
		}
		stringBuilder.append("</table>");

		LOGGER.debug(stringBuilder.toString());

		if (!isEmpty) {
			List<Destinataire> destList = new ArrayList<Destinataire>();
			Emetteur emit = new Emetteur(rule.getCmroc(), rule.getCmroc(), rule.getCmroc(), "noReply@cimut.fr", "ORGANISME");
			emit.setTypeEmetteur("INTERNE");
			Map<String, String> map = new HashMap<String, String>();
			for (String destinataire : rule.getMails()) {
				Destinataire dest = new Destinataire(destinataire, map, "", rule.getCmroc(), "ORGANISME");
				destList.add(dest);
			}
			Job job = new Job();
			job.setDestinataires(destList);
			job.setEmetteur(emit);
			Set<PieceJointe> piecesJointes = new HashSet<PieceJointe>();
			Mail mail = new Mail("GED entrant rapport", "", stringBuilder.toString().replace("\n", "<br>"), piecesJointes);
			job.setMail(mail);

			try {
				ObjectMapper mapper = new ObjectMapper();
				String stringigyJob = "";
				stringigyJob = mapper.writeValueAsString(job);
				byte ptext[] = stringigyJob.getBytes();
				stringigyJob = new String(ptext, "UTF-8");

				ClientRequest request = new ClientRequest(GlobalVariable.getMulticanalUrl());
				request.accept("application/json");
				request.body("application/json", stringigyJob);
				int status = request.post().getStatus();
				if (status != 201) {
					LOGGER.fatal("Fail to send email, code statut : " + status);
				}
			} catch (Exception e) {
				throw new CimutMongoDBException(e);
			}
		}
	}

	public DocumentMongo callMail(Rule rule, String json, Manager<DocumentMongo> documentManager) throws CimutConfException, CimutMetierException {

		DocumentMongo document = null;
		ObjectMapper mapper = new ObjectMapper();
		fr.cimut.ged.entrant.beans.db.Document documentDb = null;

		try {

			document = mapper.readValue(json, DocumentMongo.class);

			// met a jour la base de donnée Oracle
			documentDb = documentService.get(document.getId());

			// FIXME => erreur sur la generation du objectId de mongoDB ..... ????
			//document=DocumentHelper.getBean(documentDb);
			if (documentDb == null) {
				throw new CimutMetierException("Impossible de retrouver le bean : " + document.getId() + " dans la base Oracle");
			}

			document.setMailed(df.format(new Date()));
			documentDb.getJson().setData(DocumentHelper.stringify(document));

			documentService.update(documentDb);
			documentManager.update(document);

			return document;

		} catch (Exception e) {
			throw new CimutMetierException(e);
		}
	}

}
