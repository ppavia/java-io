/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.mail.Message;

import fr.cimut.ged.entrant.utils.*;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.joda.time.DateTime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cimut.ged.entrant.appelP360.P360Manager;
import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.appelmetier.SudeManager;
import fr.cimut.ged.entrant.beans.IntegrationReport;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.appelmetier.DocumentConverter;
import fr.cimut.ged.entrant.beans.appelmetier.Eddm;
import fr.cimut.ged.entrant.beans.appelmetier.Sude;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Historique;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.mongo.Departement;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.Region;
import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.dao.HistoriqueDao;
import fr.cimut.ged.entrant.dao.JsonDao;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMailBlackListedException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.mail.CompteMailManager;
import fr.cimut.ged.entrant.mail.MailerHelper;
import fr.cimut.multicanal.web.client.beans.Destinataire;
import fr.cimut.multicanal.web.client.beans.Emetteur;
import fr.cimut.multicanal.web.client.beans.Job;
import fr.cimut.multicanal.web.client.beans.Mail;

/**
 * Session Bean implementation class Manager
 */

/**
 * @author spare@ad-cimut.priv
 *
 */
@Stateless
@LocalBean
public class Manager {

	@EJB
	DocumentService documentService;

	@EJB
	HistoriqueDao daoHisto;

	@EJB
	JsonDao daoJson;

	@EJB
	Metier metier;

	@EJB
	Blacklists blackListManager;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Default constructor.
	 */
	public Manager() {

	}

	@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
	public Document add(Document document, String environnement) throws GedeException {

		DocumentMongo docMongo = DocumentHelper.getDocMongoFromJson(document);

		// si y a pas d'eddoc, on va en cree un
		if (StringUtils.isBlank(docMongo.getEddocId())) {

			EddmManager eddmManager = null;

			try {
				eddmManager = metier.getEddmManager();
				Eddm eddm = eddmManager.create(document, metier, environnement);

				// le code postal nous importe, on arrive a retrouver la region et le departement
				if (StringUtils.isBlank(docMongo.getCodePostal()) && StringUtils.isNotBlank(eddm.getCodePostal())) {
					docMongo.setCodePostal(eddm.getCodePostal());
				}

				// Le retour d'EDDM nous donne les ids interne a starweb concerant les Entreprise/Partenaire/adherent 
				// necessaire à la creation de demande SUDE.Donc nous les stockont ici.
				TypeEntite typeEntite = eddm.getTypeEntite();
				if (typeEntite == TypeEntite.PERSONNE) {
					docMongo.setAssuInsee(eddm.getInsee());
					docMongo.setNumAdherent(eddm.getSassu());
				} else if (typeEntite == TypeEntite.ENTREPRISE) {
					docMongo.setIdEntreprise(eddm.getIdentreprise());
				} else if (typeEntite == TypeEntite.PARTENAIRE) {
					docMongo.setIdProf(eddm.getPartId() + "|" + eddm.getTypPartId() + "|" + eddm.getPartNiv());
					if (eddm.getInsee() != null && !eddm.getInsee().isEmpty() ) {
						docMongo.setAssuInsee(eddm.getInsee());
					}
					if (eddm.getSassu() != null && !eddm.getSassu().isEmpty()) {
						docMongo.setNumAdherent(eddm.getSassu());
					}
				} else if (typeEntite == null) {
					docMongo.setStatus(GlobalVariable.STATUS_NOAFFECT);
					document.setStatus(GlobalVariable.STATUS_NOAFFECT);
				}
				// integre l'eddocId au json
				docMongo.setEddocId(eddm.getDocId());
				docMongo.setErreurEddm(null);
			} catch (Exception e) {
				// on va ici, si l'eddm a peter, ou le getTypeDocument n'est pas valide
				// GlobalVariable.ATTR_SHOW_RULE dit que l'on vient du courtage.
				if (null != docMongo.getTypeEntiteRattachement()) {
					docMongo.setStatus(GlobalVariable.STATUS_A_TRAITER);
					document.setStatus(GlobalVariable.STATUS_A_TRAITER);
					docMongo.setErreurEddm(e.getMessage());
					docMongo.setTypeEntiteRattachement(TypeEntite.INCONNU);
				} else {
					// cas courtage ou meme ged entrante
					docMongo.setStatus(GlobalVariable.STATUS_NOAFFECT);
					document.setStatus(GlobalVariable.STATUS_NOAFFECT);
				}

				try {

					//  create eddoc_id manually !
					// ID_STAR = sequence sur 12 chiffres
					// TS_STAR = timestamp à 14 chiffres -  dont les deux premiers chiffres sont remplacés par 99
					Eddm eddm = null;
					if (docMongo.getShowRule() != null) {
						// atention au cas de l'extranet qui veut inserer des docs dans SUDE sans affectation
						// CEtte mthode ne réalise pas d'appel EDDM. C'est un racourci.
						eddm = eddmManager.createEmpty();
					} else {
						// Lors d'une réaffectation de doc, si celui ci n'a pas d'EDDM, ca rique de peter.
						eddm = eddmManager.createEmpty(document, environnement);
					}
					GedeIdHelper.setIdstarTstar(document, eddm.getDocId());

					// integre l'eddocId au json
					docMongo.setEddocId(eddm.getDocId());
				} catch (Exception e1) {
					throw new CimutDocumentException("Impossible de cree l'EDDM sans affectation", e1);
				}
			}
		}
		

		if (StringUtils.isNotBlank(docMongo.getCodePostal())) {
			String region = Region.getRegion(docMongo.getCodePostal());
			String departement = Departement.getDepartement(docMongo.getCodePostal());
			if (!region.isEmpty()) {
				docMongo.setRegion(region);
			}
			if (!departement.isEmpty()) {
				docMongo.setDepartement(departement);
			}
		}
		// reintegre le json en string
		document.getJson().setData(DocumentHelper.stringify(docMongo));
		// persiste en base
		document = documentService.create(environnement, document, true, false);
		return document;
	}
	

	/**
	 * Recupere un identifiant unique
	 * 
	 * @param doc
	 * @return
	 * @throws CimutFileException
	 * @throws CimutConfException
	 */
	@Deprecated
	// Go to DocumentHelper.generateFinalName() 
	public String getNewUniqueFileName(Document doc) throws CimutFileException, CimutConfException {

		int counter = 0;
		String id = doc.getId();
		String extension = id.substring(id.lastIndexOf('.'));
		String baseName = id.substring(0, id.lastIndexOf('.'));
		Pattern multiplePattern = Pattern.compile("^(.*)__(\\d+)$");
		Matcher m = multiplePattern.matcher(baseName);
		if (m.matches()) {
			baseName = m.group(1);
			counter = Integer.parseInt(m.group(2));
		}

		id = baseName + "__" + counter + extension;
		Document document = documentService.get(id);

		while (document != null) {
			counter++;
			id = baseName + "__" + counter + extension;
			document = documentService.get(id);
		}

		Logger.getLogger(DocumentHelper.class).info("getNewUniqueFileName : " + doc.getId() + " => " + id);

		return id;
	}

	/**
	 * Ajoute/update un document
	 * 
	 * @param xml
	 *            File
	 * @param doc
	 *            File
	 * @param environnement
	 *            Nom de l'environnement dans lequel ajouter/mettre à jour le document
	 * @return
	 */
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public IntegrationReport addDocFromPoolerDirectory(File xml, File doc, String environnement) {

		logger.info("add(" + xml.getName() + "," + doc.getName() + ")");
		Document previous = null;
		Document document = null;
		IntegrationReport report = new IntegrationReport();
		report.setDocumentName(doc.getName());
		report.setDate(new Date());
		try {

			// on lit le flux depuis le fichier
			// on convertis en document
			document = DocumentHelper.toDocument(xml, doc.getName(), environnement, null, null, null);

			// ajout d'un libellé en fonction du cmroc
			String typeDocument = document.getTypeDocument();
			// le libellé du document est le nom du fichier par défaut
			// si le cmroc est renseigné dans la property on surcharge le libellé par le type du document
			String libelleDoc = isCmrocInLibelleDocByTypedoc(document.getCmroc()) && !StringUtils.isBlank(typeDocument) ? typeDocument : doc.getName();
			document.setLibelle(libelleDoc);

			if (StringUtils.isBlank(typeDocument)) {
				document.setTypeDocument(Type.CODE_COURRIER_DEMATERIALISE);
			}
			document.setOrigine("PoolerDirectory");
			document.setService("Ged");
			document.setSite("G");

			// on verifie si ce n'est pas une mise à jour plutot qu'un insert !
			previous = documentService.get(document.getId());
			// ajoute le tout dans la base de donnee et dans EDDM

			if (previous == null) {

				// on valide le document
				DocumentHelper.validate(document);
				document = add(document, environnement);
				// gestion de l'historique/tracabilité
				insert(document.getId(), "Intgration initiale du document", GlobalVariable.IMPORT_USER);
				// deplace le fichier document dans son plan de classement
				FileManager.add(doc, document);

				DocumentMongo json = DocumentHelper.getDocMongoFromJson(document);
				String ref_courtier = json.getAttribute("REF_COURTIER");
				String mail_courtier = json.getAttribute("MAIL_COURTIER");
				// A voir si on ajoute plut d'information dans ce rapport
				report.setInformations("CMROC=" + json.getCmroc() + ", " + "TYPE_COURRIER=" + json.getTypeEntiteRattachement().getText());

				if (StringUtils.isNotBlank(ref_courtier) && StringUtils.isNotBlank(mail_courtier)) {

					Emetteur emit = new Emetteur(json.getCmroc(), json.getCmroc(), "", "nepasrepondre@cimut.fr", "ORGANISME");
					emit.setModeDistribution("INTERNE");

					Map<String, String> map = new HashMap<String, String>();

					List<Destinataire> destList = new ArrayList<Destinataire>();
					Destinataire dest = new Destinataire(mail_courtier, map, "", ref_courtier, "COURTIER");
					destList.add(dest);

					Job job = new Job();
					job.setDestinataires(destList);
					job.setEmetteur(emit);

					DocumentMongo myJson = DocumentHelper.getDocMongoFromJson(document);
					String dateEmission = DateHelper.formatyyyymmddToddmmyyyyWithSlashes(myJson.getAttribute("DATE_EMISSION"));

					String messageText = GlobalVariable.getCourtageMailMessage();
					String messageHtml = GlobalVariable.getCourtageMailHTMLMessage();

					messageText = messageText.replaceAll("DATE_EMISSION", dateEmission);
					messageHtml = messageHtml.replaceAll("DATE_EMISSION", dateEmission);

					Mail mail = new Mail(GlobalVariable.getCourtageMailSujetMessage(), messageText, messageHtml, null);
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
							throw new CimutDocumentException(
									"Fail to send email, code statut : " + status + " email:" + mail_courtier + " refCourtier:" + ref_courtier);
						}
					} catch (Exception e) {
						throw new CimutDocumentException(e);
					}
				}

			} else {

				document.setId(this.getNewUniqueFileName(document));
				File dest = new File(GlobalVariable.getIntegrationPath(environnement).concat("/in"), document.getId());
				doc.renameTo(dest);
				doc = dest;
				document = replace(previous, document, GlobalVariable.IMPORT_USER, environnement);

				// gestion de l'historique/tracabilité
				insert(document.getId(), "Nouvelle intégration réalisé", GlobalVariable.IMPORT_USER);
				// deplace le fichier document dans son plan de classement
				FileManager.add(doc, document);
				// balance le fichier dans le rep del du moteur d'indexation
				MdbManager.delete(previous, environnement);
			}

			// balance le fichier dans le rep add du moteur d'indexation
			MdbManager.add(document, environnement);
			// at last, we clean the xml file
			FileHelper.deleteXmlFile(xml);
		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			String message = "impossible de traite le document :" + xml.getName() + "\n\n";
			message += e.getMessage();
			try {

				// on replace les documents precedemment deplacer dans le rep d'erreur
				// Rollback filesystem
				FileHelper.moveDocToError(xml, environnement);
				FileHelper.moveDocToError(doc, environnement);
				if (document != null) {
					FileHelper.moveDocFromDestToError(document, environnement);
					MdbManager.deleteInputFile("add", document, environnement);

					//String typeDoc = 
					//if (document.getTypedocument() == null || )

					//json.getTypeDocument().

					TypeEntite typeEntite = null;

					try {
						DocumentMongo docMongo = null;
						docMongo = DocumentHelper.getDocMongoFromJson(document);
						typeEntite = docMongo.getTypeEntiteRattachement();

					} catch (CimutDocumentException e2) {
						logger.error(e2);
						message += "\n\nErreur sur la deserialisation du bean en json : \n" + e2.getMessage() + "\n\n";
					}

					// rool back sur le metier EDDM !
					if (previous == null && document.getIdstar() != 0
							&& EnumSet.of(TypeEntite.PARTENAIRE, TypeEntite.ENTREPRISE, TypeEntite.PERSONNE).contains(typeEntite)) {
						try {
							EddmManager eddmManager = metier.getEddmManager();
							DocumentConverter converter = new DocumentConverter();
							Eddm eddm = converter.toEddm(document, false);
							eddmManager.remove(eddm.getDocId(), eddm.getCmroc(), environnement);
						} catch (Exception e1) {
							logger.error(e1);
							message += "\n\nErreur sur le rollback metier EDDM : \n" + e1.getMessage() + "\n\n";
						}
					}
				}

			} catch (CimutFileException e1) {
				message += "\n\nErreur sur le rollback fichier : \n" + e1.getMessage() + "\n\n";
			} catch (CimutConfException e1) {
				message += "\n\nErreur sur le rollback fichier : \n" + e1.getMessage() + "\n\n";
			} finally {
				try {
					// on ecrit le fichier id.log d'erreur associé é cette transaction dans repertoire d'erreur
					FileHelper.logFile(message, GlobalVariable.getErrorPath(environnement) + xml.getName().replaceAll(".xml$", ".log"));
				} catch (Exception e1) {
					logger.fatal(e1);
				}
			}
			// on rollback le sql
			throw new RuntimeException(message, e);
		}
		report.setOK(true);
		return report;
	}
	
	/**
	 * Ajoute/update un document
	 * 
	 * @param xml
	 *            File
	 * @param doc
	 *            File
	 * @param environnement
	 *            Nom de l'environnement dans lequel ajouter/mettre à jour le document
	 * @return
	 */
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public List<IntegrationReport> addDocsFromPoolerDirectory(File xml, List<Map<String, Object>> listDocsAssociatedToXml, String environnement) {

		List<IntegrationReport> listReport = new ArrayList<IntegrationReport>();
		// documentMongo qui contiendra la liste des documents créer pour pouvoir generer une demande sude contenant plusieurs documents
		DocumentMongo docMongoMultiple = null;
		//Map qui enregistre la liste des documents generes ou mis a jour en cas de rollback
		Map<Document, Boolean> documentsGeneres = new HashMap<Document, Boolean>();
		try {
			for(Map<String, Object> entry : listDocsAssociatedToXml) {
				File doc = (File) entry.get(GlobalVariable.ATTR_DOCUMENT);
				String type = (String) entry.get(GlobalVariable.ATTR_TYPE_DOCUMENT);
				String idExterne = (String) entry.get(GlobalVariable.ATTR_ID_EXT_DOC);
				String libelleDoc = (String) entry.get(GlobalVariable.ATTR_LIBELLE_DOC);
				IntegrationReport report = new IntegrationReport();
				report.setDocumentName(doc.getName());
				report.setDate(new Date());
				logger.info("add(" + xml.getName() + "," + doc.getName() + ")");
				Document previous = null;
				Document document = null;

				// on lit le flux depuis le fichier
				// on convertis en document
				document = DocumentHelper.toDocument(xml, doc.getName(), environnement, type, idExterne, libelleDoc);

				// ajout d'un libellé en fonction du cmroc
				String typeDocument = document.getTypeDocument();
				// le libellé du document est le nom du fichier par défaut
				// si le cmroc est renseigné dans la property on surcharge le libellé par le type du document
				if (libelleDoc == null || libelleDoc == "") {
					libelleDoc = isCmrocInLibelleDocByTypedoc(document.getCmroc()) && !StringUtils.isBlank(typeDocument) ? typeDocument : doc.getName();
				}
				document.setLibelle(libelleDoc);

				if (StringUtils.isBlank(typeDocument)) {
					document.setTypeDocument(Type.CODE_COURRIER_DEMATERIALISE);
				}
				document.setOrigine("PoolerDirectory");
				document.setService("Ged");
				document.setSite("G");

				// on verifie si ce n'est pas une mise à jour plutot qu'un insert !
				previous = documentService.get(document.getId());
				// ajoute le tout dans la base de donnee et dans EDDM

				if (previous == null) {

					// on valide le document
					DocumentHelper.validate(document);
					document = add(document, environnement);

					//à la prémière itération on creer le documentMongo "generale", on ajoute l'eddocId du premier document créer à la liste 
					if (docMongoMultiple == null ) {
						docMongoMultiple = DocumentHelper.getDocMongoFromJson(document);
						docMongoMultiple.getEddocIds().add(document.getEddocId());
						// suppression de l'EddocId pour un seul document
						docMongoMultiple.setEddocId(null); 
					} else {
						//aux iteration suivantes on ajoute l'eddoc id du document créer au document Mongo
						docMongoMultiple.getEddocIds().add(document.getEddocId());
					}
					// gestion de l'historique/tracabilité
					insert(document.getId(), "Intgration initiale du document", GlobalVariable.IMPORT_USER);
					// deplace le fichier document dans son plan de classement
					FileManager.add(doc, document);
					//on enregistre le document dans cette liste en cas de rollback
					documentsGeneres.put(document, false);

				} else {

					document.setId(this.getNewUniqueFileName(document));
					File dest = new File(GlobalVariable.getIntegrationPath(environnement).concat("/in"), document.getId());
					doc.renameTo(dest);
					doc = dest;
					document = replace(previous, document, GlobalVariable.IMPORT_USER, environnement);

					//à la prémière itération on creer le documentMongo "generale", on ajoute l'eddocId du premier document créer à la liste 
					if (docMongoMultiple == null ) {
						docMongoMultiple = DocumentHelper.getDocMongoFromJson(document);
						docMongoMultiple.getEddocIds().add(document.getEddocId());
						// suppression de l'EddocId pour un seul document
						docMongoMultiple.setEddocId(null); 
					} else {
						//aux iteration suivantes on ajoute l'eddoc id du document créer au document Mongo
						docMongoMultiple.getEddocIds().add(document.getEddocId());
					}

					// gestion de l'historique/tracabilité
					insert(document.getId(), "Nouvelle intégration réalisé", GlobalVariable.IMPORT_USER);
					// deplace le fichier document dans son plan de classement
					FileManager.add(doc, document);
					documentsGeneres.put(document, true);
					// balance le fichier dans le rep del du moteur d'indexation
					MdbManager.delete(previous, environnement);
				}

				// A voir si on ajoute plut d'information dans ce rapport
				report.setInformations("CMROC=" + docMongoMultiple.getCmroc() + ", " + "TYPE_COURRIER=" + docMongoMultiple.getTypeEntiteRattachement().getText());
				// balance le fichier dans le rep add du moteur d'indexation
				report.setOK(true);
				listReport.add(report);

			}
			
			// génération du fichier json dans le répertoire indexation pour créer l'entrée dans la base Mongo et permettre la céation de la demande sude
			MdbManager.addFromDocMongo(docMongoMultiple, environnement);

			String ref_courtier = docMongoMultiple.getAttribute("REF_COURTIER");
			String mail_courtier = docMongoMultiple.getAttribute("MAIL_COURTIER");

			if (StringUtils.isNotBlank(ref_courtier) && StringUtils.isNotBlank(mail_courtier)) {

				Emetteur emit = new Emetteur(docMongoMultiple.getCmroc(), docMongoMultiple.getCmroc(), "", "nepasrepondre@cimut.fr", "ORGANISME");
				emit.setModeDistribution("INTERNE");

				Map<String, String> map = new HashMap<String, String>();

				List<Destinataire> destList = new ArrayList<Destinataire>();
				Destinataire dest = new Destinataire(mail_courtier, map, "", ref_courtier, "COURTIER");
				destList.add(dest);

				Job job = new Job();
				job.setDestinataires(destList);
				job.setEmetteur(emit);

				String dateEmission = DateHelper.formatyyyymmddToddmmyyyyWithSlashes(docMongoMultiple.getAttribute("DATE_EMISSION"));

				String messageText = GlobalVariable.getCourtageMailMessage();
				String messageHtml = GlobalVariable.getCourtageMailHTMLMessage();

				messageText = messageText.replaceAll("DATE_EMISSION", dateEmission);
				messageHtml = messageHtml.replaceAll("DATE_EMISSION", dateEmission);

				Mail mail = new Mail(GlobalVariable.getCourtageMailSujetMessage(), messageText, messageHtml, null);
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
						throw new CimutDocumentException(
								"Fail to send email, code statut : " + status + " email:" + mail_courtier + " refCourtier:" + ref_courtier);
					}
				} catch (Exception e) {
					throw new CimutDocumentException(e);
				}
			}
			
			// at last, we clean the xml file
			FileHelper.deleteXmlFile(xml);

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

			String message = "impossible de traite le document :" + xml.getName() + "\n\n";
			message += e.getMessage();
			try {

				// on replace les documents precedemment deplacer dans le rep d'erreur
				// Rollback filesystem
				FileHelper.moveDocToError(xml, environnement);
				for(Map<String, Object> entry : listDocsAssociatedToXml) {
					File doc = (File) entry.get(GlobalVariable.ATTR_DOCUMENT);
					FileHelper.moveDocToError(doc, environnement);
				}

				for(Map.Entry<Document, Boolean> entry : documentsGeneres.entrySet()) {
					Document document = entry.getKey();
					FileHelper.moveDocFromDestToError(document, environnement);
					MdbManager.deleteInputFile("add", document, environnement);

					TypeEntite typeEntite = null;

					try {
						DocumentMongo docMongo = null;
						docMongo = DocumentHelper.getDocMongoFromJson(document);
						typeEntite = docMongo.getTypeEntiteRattachement();

					} catch (CimutDocumentException e2) {
						logger.error(e2);
						message += "\n\nErreur sur la deserialisation du bean en json : \n" + e2.getMessage() + "\n\n";
					}

					// rool back sur le metier EDDM !
					if (!entry.getValue() && document.getIdstar() != 0
							&& EnumSet.of(TypeEntite.PARTENAIRE, TypeEntite.ENTREPRISE, TypeEntite.PERSONNE).contains(typeEntite)) {
						try {
							EddmManager eddmManager = metier.getEddmManager();
							DocumentConverter converter = new DocumentConverter();
							Eddm eddm = converter.toEddm(document, false);
							eddmManager.remove(eddm.getDocId(), eddm.getCmroc(), environnement);
						} catch (Exception e1) {
							logger.error(e1);
							message += "\n\nErreur sur le rollback metier EDDM : \n" + e1.getMessage() + "\n\n";
						}
					}

				}


			} catch (CimutFileException e1) {
				message += "\n\nErreur sur le rollback fichier : \n" + e1.getMessage() + "\n\n";
			} catch (CimutConfException e1) {
				message += "\n\nErreur sur le rollback fichier : \n" + e1.getMessage() + "\n\n";
			} finally {
				try {
					// on ecrit le fichier id.log d'erreur associé é cette transaction dans repertoire d'erreur
					FileHelper.logFile(message, GlobalVariable.getErrorPath(environnement) + xml.getName().replaceAll(".xml$", ".log"));
				} catch (Exception e1) {
					logger.fatal(e1);
				}
			}
			// on rollback le sql
			throw new RuntimeException(message, e);
		}
		return listReport;
	}

	/* (non-Javadoc)
	 * @see fr.cimut.ged.entrant.service.ManagerRemote#get(java.lang.String)
	 */

	public Document get(String id) {
		return documentService.get(id);
	}

	public Document updateWithoutTransac(Document document)
			throws CimutDocumentException, CimutFileException, CimutConfException, GedeCommonException {
		return documentService.update(document);
	}

	public Document replace(Document oldDoc, Document newDoc, String login, String environnement) throws CimutDocumentException {

		logger.info("replace(" + oldDoc.getId() + "=> " + newDoc.getId() + ")");

		try {

			// on recupere l'id original du nouveau document (cense etre unique cf: module transcodage)
			String newId = newDoc.getId();

			// on merge les deux. ne prennant en compte que les nouveaux attributs donne,
			// reprennant les anciennes valeur si non renseigné
			newDoc = DocumentHelper.mergeBean(oldDoc, newDoc);

			DocumentHelper.validate(newDoc);

			DocumentMongo jsonOld = DocumentHelper.getDocMongoFromJson(oldDoc);
			DocumentMongo jsonNew = DocumentHelper.getDocMongoFromJson(newDoc);

			logger.info("#########################");
			logger.info("oldId : " + oldDoc.getId());
			logger.info("newId : " + newId);
			logger.info("#########################\n\n");

			// faut mettre a jour le nouvel id partout la ou il le faut ...
			newDoc.setId(newId);
			newDoc.getJson().setId(newId);
			newDoc.getJson().setOrganisme(jsonNew.getCmroc());
			jsonNew.setId(newId);

			//####################################################################################
			// detect if affectation has Changed
			//####################################################################################
			boolean hasProprietaireChanged = DocumentHelper.hasOwnerChanged(jsonOld, jsonNew);
			//####################################################################################

			//####################################################################################
			// detect if affectation has Changed
			//####################################################################################

			// Do what is necessary if association has changed
			if (hasProprietaireChanged) {

				logger.info("Owner has changed !");
				EddmManager eddmManager = metier.getEddmManager();
				if (jsonNew.getEddocId() != null && jsonNew.getEddocId().matches("^\\d+_\\d+$")) {
					String eddocIdToDel = jsonNew.getEddocId();
					logger.info("REMOVING EDDM : " + eddocIdToDel);
					try {
						eddmManager.remove(eddocIdToDel, jsonNew.getCmroc(), environnement);
					} catch (Exception e) {
						logger.error("Impossible de supprimer l'entree eddoc : " + eddocIdToDel + " => " + e.getMessage());
						// on fait rien ...
						//jsonNew.setStatus(GlobalVariable.STATUS_ERROR);
						//jsonNew.setErreurEddm(e.getMessage());
					} finally {
						jsonNew.setEddocId(null);
						newDoc.setIdstar(0);
						newDoc.setTsstar(0);
						jsonNew.setErreurEddm(null);
					}
				}

				// on integre notre nouveau documents dans EDDM
				try {
					logger.debug("ADD EDDM ");
					Eddm eddm = eddmManager.create(newDoc, metier, environnement);

					TypeEntite typeEntite = eddm.getTypeEntite();
					if (typeEntite == TypeEntite.PERSONNE) {
						jsonNew.setAssuInsee(eddm.getInsee());
						jsonNew.setNumAdherent(eddm.getSassu());
					} else if (typeEntite == TypeEntite.ENTREPRISE) {
						jsonNew.setIdEntreprise(eddm.getIdentreprise());
					}

					jsonNew.setEddocId(eddm.getDocId());
					jsonNew.setErreurEddm(null);

					if (jsonNew.getStatus().equals(GlobalVariable.STATUS_ERROR)) {
						jsonNew.setStatus(GlobalVariable.STATUS_A_TRAITER);
						newDoc.setStatus(GlobalVariable.STATUS_A_TRAITER);
					}

				} catch (Exception e) {
					logger.error("ADD EDDM erreur : " + e.getMessage());
					jsonNew.setStatus(GlobalVariable.STATUS_ERROR);
					newDoc.setStatus(GlobalVariable.STATUS_ERROR);
					jsonNew.setErreurEddm(e.getMessage());
					try {

						Eddm eddm = eddmManager.createEmpty();
						GedeIdHelper.setIdstarTstar(newDoc, eddm.getDocId());

						// integre l'eddocId au json
						jsonNew.setEddocId(eddm.getDocId());

					} catch (Exception e1) {
						logger.error(e1);
					}
				}

				// gestion de la DA
				if (jsonNew.getSudeId() != null && jsonNew.getSudeId().matches("^\\d+$")) {

					// si on a un changement de proprio, on ne vehicule pas la Da pour qu'elle puisse etre retraiter par le system plus tard
					logger.info("");
					logger.info("#########################");
					logger.info("Has an DA_ID  " + jsonNew.getSudeId());
					logger.info("#########################");

					try {
						SudeManager daManager = metier.getSudeManager();
						daManager.remove(jsonNew.getSudeId(), jsonNew.getCmroc(), environnement);
						jsonNew.setSudeId(null);
						jsonNew.setErreurDa(null);
					} catch (Exception e) {
						// en faite, y a des cas ou les SUDE on été virée ...
						jsonNew.setSudeId(null);
						jsonNew.setErreurDa(null);
						logger.error("Remove DA : " + e.getMessage());
						//jsonNew.setStatus(GlobalVariable.STATUS_ERROR);
						//newDoc.setStatus(GlobalVariable.STATUS_ERROR);
						//jsonNew.setErreurDa(e.getMessage());
					}
				} else if (jsonNew.getErreurDa() != null) {
					logger.info("#########################");
					logger.info("Has a DA ERREUR  " + jsonNew.getErreurDa());
					logger.info("#########################");
					jsonNew.setErreurDa(null);
					// cas d'une erreur uniquement lie à une DA. on remet le status "A traiter"
					if (jsonNew.getErreurEddm() == null && jsonNew.getStatus().equals(GlobalVariable.STATUS_ERROR)) {
						jsonNew.setStatus(GlobalVariable.STATUS_A_TRAITER);
						newDoc.setStatus(GlobalVariable.STATUS_A_TRAITER);
					}
				}

			} else if (!newDoc.getMimeType().equals(oldDoc.getMimeType())) {

				logger.info("Mime Type has changed !");

				// extension has changed. update EDDM for this
				if (jsonNew.getEddocId() != null && jsonNew.getEddocId().matches("^\\d+_\\d+$")) {
					try {
						logger.info("UPDATE EDDM : " + jsonNew.getEddocId());
						EddmManager eddmManager = metier.getEddmManager();
						eddmManager.update(newDoc, environnement);
						//jsonNew.setErreurEddm(null);

					} catch (Exception e) {
						logger.error("Update EDDM : " + e.getMessage());
						jsonNew.setStatus(GlobalVariable.STATUS_ERROR);
						newDoc.setStatus(GlobalVariable.STATUS_ERROR);
						jsonNew.setErreurEddm(e.getMessage());
					}
				}
			}

			if (!oldDoc.getId().equals(newDoc.getId())) {

				// pour pouvoir consulter le document precedent, il nous faut un lien
				// on genere un nouveau tsstar, en gardant la sequence du premier, on doit donc etre bon pour l'unicité
				if (oldDoc.getIdstar() == 0 && newDoc.getIdstar() == 0) {
					oldDoc.setIdstar(GedeIdHelper.generateIdStar());
				} else if (newDoc.getIdstar() != 0) {
					oldDoc.setIdstar(newDoc.getIdstar());
				}

				oldDoc.setTsstar(GedeIdHelper.generateTsstar());
				String eddocIdOld = "[eddocId:" + GedeIdHelper.getEddocIdFromIds(oldDoc.getIdstar(), oldDoc.getTsstar()) + "]";
				String fileNameOld = "[fileName:" + oldDoc.getId() + "]";
				insert(oldDoc.getId(), "Remplacement du fichier " + fileNameOld + eddocIdOld, login);

				// ne realise pas la suppression du json associer ...
				oldDoc.setJson(null);
				logger.info("Mise a jour Ancien Doc");
				documentService.update(oldDoc);

				// on persist le nouveau
				logger.info("Persistence du nouveau bean ");

				// on remet notre jsonNew dans notre bean oracle manager
				newDoc.getJson().setData(DocumentHelper.stringify(jsonNew));
				newDoc = documentService.create(environnement, newDoc, true, false);

				logger.info("Deplacement de l'historique de old => new ");
				// on deplace l'historique du premier dans le nouveau
				int result = daoHisto.updateHistoriqueId(oldDoc.getId(), newDoc.getId());
				logger.info("updateHistoriqueId => " + result);

				logger.info("Suppression old json");
				daoJson.deleteJsonById(oldDoc.getId());

			} else {
				logger.info("Mise a jour Doc");
				newDoc.getJson().setData(DocumentHelper.stringify(jsonNew));
				documentService.update(newDoc);
			}

			String modifications = DocumentHelper.diff(jsonOld, jsonNew);
			if (!modifications.isEmpty()) {
				logger.info("Ajout Historique " + newDoc.getId() + " " + modifications);
				insert(newDoc.getId(), modifications, login);
			}
			logger.info("Done !");
			return newDoc;

		} catch (Exception e) {
			throw new CimutDocumentException(e);
		}
	}

	public Historique insert(String id, String modifications, String login) {
		Historique historique = new Historique();
		historique.setDateMaj(new Date());
		historique.setModification(modifications);
		historique.setUserMaj(login);
		historique.setDocId(id);
		return daoHisto.insert(historique);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public List<Historique> getHistoriques(String id) {
		return daoHisto.getHistoriques(id);
	}

	/**
	 * Recupere un document depuis son eddocId
	 * 
	 * @param eddocId
	 * @return
	 */
	public Document getByEddocId(String eddocId) {
		logger.info("finding : " + eddocId);
		return documentService.getByEddocId(eddocId);
	}

	/**
	 * Ajout dans le SI de starweb, l'email en paramètre
	 * 
	 * @param message
	 *            (l'email)
	 * @param inboxFolderName
	 *            (Nom du répertoire de traitement du mail)
	 * @param listRules
	 *            (liste des règles)
	 * @param compteMailManager
	 *            (Informations sur le compte mail)
	 * @param environnement
	 *            (Nom de l'environnement dans lequel ajouter l'email)
	 * @throws CimutMailBlackListedException
	 */
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	//@TransactionTimeout(value = 60, unit = TimeUnit.MINUTES)
	public void addEmail(Message message, String inboxFolderName, List<Rule> listRules, CompteMailManager compteMailManager, String environnement)
			throws Exception {

		List<Document> listDoc = new ArrayList<Document>();
		List<Document> documents = new ArrayList<Document>();

		String cmroc = compteMailManager.getCompteMail().getCmroc();
		String sudeId = null;

		// on recupere les infos du mail

		// en recuper l'expediteur et on verifie si present dans la blackListe
		String sender = null;
		boolean isBlackListed = false;
		sender = MailerHelper.getFromTo(message);
		if (sender == null || sender.isEmpty()) {
			throw new Exception("impossible d'extraire l'email de l'envoyeur");
		}
		isBlackListed = blackListManager.get(environnement, cmroc, sender);

		if (isBlackListed) {
			throw new CimutMailBlackListedException("Mail present dans la blackliste : " + sender);
		}

		try {

			Map<String, String> contents = MailerHelper.getMessage(message);

			String subject = MailerHelper.getSubject(message);

			// on recupere les infos de l'entité a rattaché avec l'appel p360
			P360Manager managerP360 = new P360Manager();
			DocumentMongo docMongo = managerP360.getAffectation(sender, cmroc, metier, environnement);

			// on instancie notre bean document (editique)
			Document document = new Document();
			document = DocumentHelper.setDefaultValue(document, environnement);
			document.setDtcreate(new Date());
			document.setTypeDocument(Type.CODE_PIECE_JOINTE_SUDE);
			document.setCmroc(docMongo.getTutelle());

			// on instancie notre bean json (mongoDB + oracle)
			// evidement, le destinataire est bien le compte de la boite sur laquelle on est ...
			docMongo.getAttributes().put(GlobalVariable.ATTR_DESTINATAIRE, compteMailManager.getCompteMail().getEmail());
			docMongo.getAttributes().put(GlobalVariable.ATTR_EXPEDITEUR, sender);
			docMongo.getAttributes().put(GlobalVariable.ATTR_SUBJECT, subject);
			docMongo.getAttributes().put(GlobalVariable.ATTR_MAIL_FOLDER, inboxFolderName);
			if (compteMailManager.hasAccuseReception()) {
				docMongo.getAttributes().put(GlobalVariable.ACCUSE_RECEPTION, "1");
			}

			String strippedContent = MailerHelper.getStrippedContent(contents.get("content"), sender, subject);
			docMongo.setCommentaire(strippedContent);
			docMongo.setDtIntegration(new DateTime());
			docMongo.setDtCreate(MailerHelper.getDate(message));
			docMongo.setTypeDocument(document.getTypeDocument());
			document.setStatus(GlobalVariable.STATUS_A_TRAITER);

			// on rattache notre json avec notre bean
			document.setJson(new Json());
			document.getJson().setData(DocumentHelper.stringify(docMongo));

			logger.info(inboxFolderName + " debut recuperation de piece jointe... ");
			long timeBefore = new Date().getTime();
			documents = MailerHelper.getPiecesJointes(message, document.getCmroc());
			long timeAfter = new Date().getTime();
			long timeElapsed = timeAfter - timeBefore;
			logger.info(inboxFolderName + "  recuperation de piece jointe ok en " + timeElapsed + "ms");

			boolean isMailContentEmpty = contents.get("content") == null || contents.get("content").trim().isEmpty();
			if (isMailContentEmpty && documents.isEmpty()) {
				// je prefere stopper la, soit un mail vide (aucun interet a le traité, ca arrive oui !)
				// soit un probleme de parsing de mail (multipart non gerer correctement et donc a analyser)
				throw new CimutMetierException("Le mail est vide ou problème de parsing de contenu => pas d'intégration");
			}
			document.setId(DocumentHelper.generateSudeNewId(".html"));

			//Si l'option contenuPJ est à true, on ajoute le corps du mail dans une piece jointe
			if (GlobalVariable.isMailContenuPjEnabled(cmroc) && !isMailContentEmpty) {
				Document document1 = new Document();
				// recupere le nom du fichier origina (surtout pour l'extension)
				// libelle dans EDDM (le nom du fichier)
				document1.setLibelle("email");

				// set les attr necesaire a l'obtetion du plan de classement
				document1.setDtcreate(new Date());
				// recuperation du plan de classement
				document1.setCmroc(cmroc);
				String path = DocumentHelper.getPlanDeClassement(document1);
				// set le nouveau nom du fichier
				document1.setId(document.getId());

				path += "/" + document1.getId();
				// sauvegarde notre fichier dans le plan de classement de la ged

				String charset = "";

				// gestion du charset/file encoding /!\ file utf-8 mais contenu html iso-latin1 (faut bien faire attention ici et respecter tout ceci)
				Writer out = null;
				try {

					if (contents.containsKey("charset") && !contents.get("charset").isEmpty()) {
						out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), contents.get("charset")));
						charset = contents.get("charset");
					} else {
						// ok , je prend le charset du systeme par defaut, au pire, les accents seront pas bon
						out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), System.getProperty("file.encoding")));
						charset = System.getProperty("file.encoding");
					}
					out.write(contents.get("content"));
				} catch (Exception e) {
					throw new CimutMetierException("Erreur lors de l'ecriture du mail dans le fichier", e);
				} finally {
					if (out != null) {
						out.close();
					}
				}

				// gestion du type mime
				String typeMime = "";
				if (contents.containsKey("mime") && contents.get("mime") != null && !contents.get("mime").isEmpty()) {
					typeMime = contents.get("mime");
				} else {
					typeMime = FileHelper.getTypeMime(document1.getId());
				}

				document1.setMimeType(typeMime + "; charset=" + charset);
				// on ajout notre document a la liste
				document1.setJson(new Json());
				documents.add(document1);
			}

			// on boucle sur les pieces jointes
			for (Document newDoc : documents) {

				// on reset notre ID
				// je veux finalement que la piece jointe concernant le corp du mail soit ici !
				// pour des reprises de contenu de mail c'est plus pratique avec le DA ID
				if (document.getId() == null || document.getId().isEmpty()) {
					document.setId(newDoc.getId());
				}
				newDoc.setTypeDocument(document.getTypeDocument());

				// pour le libelle EDDM (meme que chose que pour la SUDE)
				docMongo.addAttribute("EDDM_LIBELLE", newDoc.getLibelle());
				newDoc.setLibelle("Ged Entrante");
				docMongo.setStatus(document.getStatus());
				// AFFECATION DES DOCUMENTS VIA EDDM
				// besoin de ca pour l'appel EDDM
				newDoc.setJson(document.getJson());
				newDoc.getJson().setData(DocumentHelper.stringify(docMongo));

				// on realise enfin l'appel
				if (docMongo.getTypeEntiteRattachement() == null) {
					metier.getEddmManager().createEmpty(newDoc, environnement);
				} else {
					metier.getEddmManager().create(newDoc, metier, environnement);
				}

				// on reaffect
				newDoc.setJson(new Json());// en evitant la ref au json ;)
				newDoc.getJson().setId(newDoc.getId());
				newDoc.getJson().setOrganisme(newDoc.getCmroc());
				newDoc.getJson().setData(DocumentHelper.stringify(docMongo));// la c'est une string, donc c'est ok ! pas de ref
				// on ajoute a la liste an mergant (method fait main, attention, c'est central pour CDDE)
				listDoc.add(DocumentHelper.merge(document, newDoc));
			}

			//########################
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> obj = objectMapper.readValue(document.getJson().getData(), new TypeReference<HashMap<String, Object>>() {
			});

			// trouve notre regle
			Rule ruleToApply = findRule(obj, listRules);

			//########################
			if (ruleToApply != null) {

				// on set le sujet de la DA avec le sujet du mail
				// attention a bien sanitizer ca, sinon plantage du parser niveau cobol et timeout
				if (ruleToApply.getService() != null) {
					ruleToApply.getService().setSujet(docMongo.getAttribute(GlobalVariable.ATTR_SUBJECT));
				}

				logger.debug("RULE NAME : " + ruleToApply.getName());
				logger.debug("SERVICE NAME : " + ruleToApply.getService().getName());

				// creation de la sude
				Sude sude = metier.getSudeManager().create(ruleToApply.getService(), docMongo, listDoc, environnement);

				if (compteMailManager.hasAccuseReception()) {
					docMongo.getAttributes().remove(GlobalVariable.ACCUSE_RECEPTION);
				}

				// recuperation sude_id
				sudeId = sude.getId();
				docMongo.setSudeId(sudeId);

				logger.info("DA ID : " + sudeId);

				if (listDoc != null && !listDoc.isEmpty()) {
					// on periste ces document dans l'editique
					for (Document doc : listDoc) {

						// recupere le json associé a doc
						DocumentMongo json_ = DocumentHelper.getDocMongoFromJson(doc);

						// reaffect au json les elements necessaire (eddocId, Daid)
						json_.setSudeId(sudeId);

						json_.setEddocId(GedeIdHelper.getEddocIdFromIds(doc.getIdstar(), doc.getTsstar()));

						// on remet le json dans son bean oracle apres ces ajouts
						doc.getJson().setData(DocumentHelper.stringify(json_));
						if (doc.getMimeType() == null) {
							doc.setMimeType(FileHelper.getTypeMime(doc.getId()));
						}

						// on persite en base !
						documentService.create(environnement, doc, true, false);
						docMongo.getEddocIds().add(json_.getEddocId());
					}

					// integration dans mongoDB avec l'id du premier doc
					docMongo.setId(listDoc.get(0).getId());

				} else {
					// on genere un ID bidon ...
					// TODO ne sera jamais purger ...
					document.setId(DocumentHelper.generateSudeNewId(""));
					docMongo.setId(document.getId());
				}
				document.getJson().setData(DocumentHelper.stringify(docMongo));
				MdbManager.add(document, environnement);

				// ici, on envoie l'accuse de reception si configurer 

				// 
				// erf, probleme ici ...
				// maintenant on precise dans SUDE si l'accusé de reception a été envoyer. vu que je réalise 
				// l'appel sude avant d'envoyer l'email (normal niveau transactionnel) si je plante à l'envoie ...
				// faut que je mette à jour la SUDE en elevant ce champs accusé de reception !

				// TODO : ne jete pas d'exception ici (mais faudrait finalement mettre a jour la SUDE pour enlever cette accusé de reception !)
				compteMailManager.sendAccuseReception(sender, sudeId);

			} else {
				// TODO : trouver la regle par default !!!!
				logger.error("Aucune regle trouvee, impossible de creer la DA !!");
				throw new CimutMetierException("Aucune regle trouvee, impossible de creer la DA !!");
			}
		} catch (Exception e) {

			// on rollback le tout ...
			if (sudeId != null && sudeId.matches("^\\d+$")) {
				try {
					metier.getSudeManager().remove(sudeId, cmroc, environnement);
				} catch (Exception e1) {
					logger.fatal("Rollback on files failed,impossible d effacer la SUDE suivante  : " + sudeId);
				}
			}

			// loop over created EDDM
			for (Document doc : listDoc) {
				try {
					String eddocId = GedeIdHelper.getEddocIdFromIds(doc.getIdstar(), doc.getTsstar());
					logger.info("deleting edm  " + eddocId);
					metier.getEddmManager().remove(eddocId, doc.getCmroc(), environnement);
				} catch (Exception e2) {
					logger.fatal("Rollback on eddm failed ", e2);
				}
			}
			// loop ever created/copied files
			for (Document doc : documents) {
				try {
					File fileToDelete = DocumentHelper.getFile(doc);
					logger.info("deleting file " + fileToDelete.getAbsolutePath());
					if (!fileToDelete.delete()) {
						logger.fatal("Rollback on files failed,impossible d effacer le fichier suivant  : " + doc.getId());
					}
				} catch (Exception e2) {
					logger.fatal("Rollback on files failed ", e2);
				}
			}
			throw e;

		}

	}

	/**
	 * Recupere la regle depuis la liste de regle qui correspond a notre object On ne gere que la recherche par
	 * ATTR_DESTINATAIRE.
	 * 
	 * @param obj
	 * @param listRules
	 * @return
	 */
	private Rule findRule(Map<String, Object> obj, List<Rule> listRules) {

		Rule ruleToApply = null;

		for (Rule rule : listRules) {

			boolean found = false;
			for (RuleCriteres ruleCriteres : rule.getCriteres()) {
				Object item = obj.get(ruleCriteres.getId());

				logger.debug("critere id : " + ruleCriteres.getId());

				if (item instanceof String) {
					logger.debug((String) item + " in ");
					for (String crite : ruleCriteres.getParameters()) {
						logger.debug("\t" + crite);
					}

					if (GlobalVariable.ATTR_DESTINATAIRE.equals(ruleCriteres.getId())) {

						List<String> listParams = ruleCriteres.getParameters();
						String emails = "";
						for (String string : listParams) {
							emails += string + ", ";
						}
						String searchFor = (String) item;
						if (searchFor.indexOf(",") > -1) {
							String[] destinataires = searchFor.split(",");
							for (String string : destinataires) {
								if (string != null && emails.toUpperCase().indexOf(string.trim().toUpperCase()) > -1) {
									found = true;
								}
							}
						} else if (emails.toUpperCase().indexOf(searchFor.toUpperCase()) > -1) {
							found = true;
						}
					} else if (!ruleCriteres.getParameters().contains(item)) {
						found = false;
						break;
						// cas ou ils ont envoyer ca a plusieur destinataire
					} else {
						found = true;
					}
				} else {
					found = false;
					break;
				}
			}
			if (found) {
				ruleToApply = rule;
				break;
			}
		}
		return ruleToApply;
	}

	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public void deleteDocDB(List<String> files) throws CimutConfException {
		if (files != null && !files.isEmpty()) {
			for (String id : files) {
				documentService.delete(id);
			}
		}
	}

	/**
	 * 
	 * @param document
	 * @param typeDossier
	 * @param identifiant
	 * @param demandeur
	 * @param environnement
	 * @param isNotif
	 * @param destinataire
	 * @return
	 * @throws CimutConfException
	 * @throws GedeCommonException
	 */
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public String addCommmunication(Document document, String typeDossier, String identifiant, String demandeur, String environnement,
			boolean isNotif, String destinataire) throws CimutConfException, GedeCommonException {

		String generatedEddmId = null;
		EddmManager eddmManager = metier.getEddmManager();

		try {
			// TODO SPA supprimer cette méthode cf integUnitaire
			DocumentHelper.addInfoRattachement(document, TypeEntite.fromString(typeDossier), identifiant, "E");

			// insert starweb 
			Eddm eddm = eddmManager.create(document, metier, environnement, EddmManager.SENDING, demandeur, isNotif, destinataire,
					document.getOrigine());
			generatedEddmId = eddm.getDocId();

			//le json ne servait que pour la mécanique de rattachement au dossier. 
			//pas besoin de le garder pour le référencement des communications
			document.setJson(null);
			// insert edt
			documentService.create(environnement, document, true, false);

		} catch (Exception e) {
			try {
				eddmManager.remove(generatedEddmId, document.getCmroc(), environnement);
			} catch (CimutMetierException e1) {
				logger.error("Rollback on eddm failed ", e1);
			}
			throw new GedeCommonException("Problème de sauvegarde de la communication : ", e);
		}
		return generatedEddmId;
	}

	public String addSude(Document document, String environnement)
			throws CimutDocumentException, CimutMetierException, CimutConfException, GedeCommonException {

		EddmManager eddmManager = metier.getEddmManager();
		Eddm eddm = null;
		if (document.getDocMongo().getTypeEntiteRattachement() != null) {
			eddm = eddmManager.create(document, metier, environnement);
		} else {
			eddm = eddmManager.createEmpty(document, environnement);
		}

		try {
			documentService.create(environnement, document, true, false);
		} catch (Exception e) {
			try {
				metier.getEddmManager().remove(eddm.getDocId(), document.getCmroc(), environnement);
			} catch (Exception e1) {
				logger.error(e1);
			}
			throw new CimutDocumentException(e);
		}
		return eddm.getDocId();
	}

	private boolean isCmrocInLibelleDocByTypedoc(String cmroc) {
		List<String> cmrocsByLibelle = GlobalVariable.getListCmrocDematDocumentNameByTypedoc();
		return cmrocsByLibelle.contains(cmroc);
	}

}
