/**
 * @author spare
 */
package fr.cimut.ged.entrant.interrogation.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.FileHelper;

public class MulticanalFileManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(this.getClass());
	private static final String CMROC = "cmroc";
	private static final String TYPE_DOSSIER = "typeDossier";
	private static final String ID_DOSSIER = "idDossier";
	private static final String ENV = "env";
	private static final Object DEMANDEUR = "demandeur";
	private static final Object ISNOTIF = "isNotification";
	private static final Object DESTINATAIRE = "destinataire";

	@EJB
	Metier metier;

	@EJB
	Manager manager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public MulticanalFileManager() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 
	 * @param env
	 * @param cmroc
	 * @param demandeur
	 * @param typeDossier
	 * @param idDossier
	 * @param typeDocument
	 * @param libelle
	 * @param origine
	 * @param canal
	 * @param destinataire
	 * @param service
	 * @param mimeType
	 * @param isNotification
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// passé les 100Ko on enregistre plus en memoire mais sur le filesystem
		// attention à la monté en charge pour la conso mémoire.
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(1024 * 100);

		ServletFileUpload uploadHandler = new ServletFileUpload(factory);

		// Sets the maximum allowed size of a complete request : max 10 Mb
		uploadHandler.setSizeMax((long) 1024 * 1024 * 10);

		FileItem fileItem = null;
		Document document = null;
		String idEddm = null;
		File fileToSave = null;

		Map<String, String> inputsMap = new HashMap<String, String>();

		// On parse la requette
		try {
			if (!ServletFileUpload.isMultipartContent(request)) {
				logger.error("Request is not multipart, please 'multipart/form-data' enctype for your form.");
				throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
			}

			List<FileItem> items = uploadHandler.parseRequest(request);
			int nbFile = 0;
			for (FileItem item : items) {
				if (item.isFormField()) {
					processFormField(item, inputsMap);
				} else {
					fileItem = item;
					inputsMap.put("extension", FileHelper.getExtension(item.getName()));
					nbFile++;
				}
			}

			if (nbFile != 1) {
				throw new CimutDocumentException("Merci de fournir un et un seul fichier a interger");
			}

			checkInputs(inputsMap);

		} catch (Exception e) {
			logger.error("Problème lors du parsing de la requete", e);
			sendInternalSR(response, e.getMessage());
			return;
		}

		// on stocke le fichier et on le référence
		try {

			document = buildDocument(inputsMap);

			fileToSave = DocumentHelper.getFileToSave(document);
			fileItem.write(fileToSave);
			logger.debug("sauvegarde doc to : " + fileToSave.getAbsolutePath());

			idEddm = manager.addCommmunication(document, inputsMap.get(TYPE_DOSSIER), inputsMap.get(ID_DOSSIER), inputsMap.get(DEMANDEUR),
					inputsMap.get(ENV), Boolean.parseBoolean(inputsMap.get(ISNOTIF)), inputsMap.get(DESTINATAIRE));

		} catch (Exception e) {
			// GYC
			// la gestion du transactionnel pour les fichiers se realise ici ...
			// cause : probleme de cast sur les fileItems dans l'ejb ...
			logger.error("Problème lors du stockage/referencement", e);
			if (fileToSave != null && fileToSave.exists() && !fileToSave.delete()) {
				logger.error("Impossible de supprimer le fichier suivante : " + fileToSave.getAbsolutePath());
			}
			sendInternalSR(response, e.getMessage());
			return;
		}

		// on répond
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			response.setStatus(200);
			response.setHeader("Content-Type", "text/html; charset=utf-8");
			response.setHeader("Cache-Control", "no-cache");

			writer.write(idEddm);
			writer.flush();
		} catch (Exception e) {
			logger.error("Problème lors de la reponse au client", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void sendInternalSR(HttpServletResponse response, String libError) {
		try {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, libError);
		} catch (IOException e) {
			logger.fatal("Probleme lors de l'affichage du message de reponse", e);
		}
	}

	private void processFormField(FileItem item, Map<String, String> dataMap) {
		dataMap.put(item.getFieldName(), item.getString());
	}

	private void checkInputs(Map<String, String> inputsMap) throws CimutDocumentException, CimutConfException {

		// vérification de la validité de l'environnement
		String environnement = EnvironnementHelper.determinerEnvironnement(inputsMap.get(ENV));
		if (environnement == null) {
			throw new CimutDocumentException("Environnement invalide (non géré) : " + inputsMap.get(ENV));
		}
		inputsMap.put(ENV, environnement);

		// vérification du cmroc
		if (!inputsMap.get(CMROC).matches("\\d{4}")) {
			throw new CimutDocumentException("cmroc invalid : " + inputsMap.get(CMROC));
		}

		if (!inputsMap.get(TYPE_DOSSIER).matches("^(Personne|Partenaire|Entreprise)$")) {
			throw new CimutDocumentException("typeDossier invalid : " + inputsMap.get(TYPE_DOSSIER));
		}

		if (!inputsMap.get(ID_DOSSIER).matches("^[0-9A-Za-z\\|]+$")) {
			throw new CimutDocumentException("idDossier invalid : " + inputsMap.get(ID_DOSSIER));
		}

		List<String> inputsList = Arrays.asList("demandeur", "typeDocument", "libelle", "origine", "canal", "destinataire", "service", "mimeType",
				"isNotification");

		for (String input : inputsList) {
			if (StringUtils.isBlank(inputsMap.get(input))) {
				throw new CimutDocumentException(input + " manquant");
			}
		}

	}

	private Document buildDocument(Map<String, String> inputsMap) {
		Document document = new Document();
		document = DocumentHelper.setDefaultMulticanalValues(document, inputsMap.get(ENV));
		document.setCmroc(inputsMap.get(CMROC));
		document.setTypeDocument(inputsMap.get("typeDocument"));
		document.setDtcreate(new Date());
		document.setLibelle(inputsMap.get("libelle"));
		document.setOrigine(inputsMap.get("origine"));
		document.setTypepapier(inputsMap.get("canal"));
		document.setService(inputsMap.get("service"));
		document.setId(DocumentHelper.generateFinalName(document.getTypeDocument(), inputsMap.get("extension")));
		document.setMimeType(inputsMap.get("mimeType"));
		document.setLbnmff("Ged Entrante");
		return document;
	}
}
