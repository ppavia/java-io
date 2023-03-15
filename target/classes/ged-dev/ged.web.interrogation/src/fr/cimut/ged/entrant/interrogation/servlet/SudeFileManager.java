/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.interrogation.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.FileHelper;
import fr.cimut.ged.entrant.utils.OrganismeHelper;

public class SudeFileManager extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final Logger logger = Logger.getLogger(this.getClass());
	@EJB
	private Manager manager;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SudeFileManager() {
		super();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		boolean allowed = false;
		String credentials = "";
		final String authorization = request.getHeader("Authorization");
		if (authorization != null && authorization.startsWith("Basic")) {
			// Authorization: Basic base64credentials
			String base64Credentials = authorization.substring("Basic".length()).trim();
			credentials = new String(Base64.decodeBase64(base64Credentials.getBytes()), Charset.forName("UTF-8"));
			// credentials = username:password
			final String[] values = credentials.split(":", 2);
			if (values.length == 2) {
				if (values[0].equals("giveMeFive") && values[1].equals("giveMeNiNe")) {
					allowed = true;
				}
			}
		}

		if (!allowed) {
			logger.error("Erreur d'authentification " + credentials);
			response.sendError(401, "Authentification requise");
			return;
		}

		if (!ServletFileUpload.isMultipartContent(request)) {
			logger.error("Request is not multipart, please 'multipart/form-data' enctype for your form.");
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}
		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
		// max 20 Mb
		uploadHandler.setSizeMax(1024 * 1024 * 20);

		String envirInput = null;
		String environnement = null;

		String login = null;
		TypeEntite typeEntite = null;
		String cmroc = null;
		String originalFileName = "";
		String sens = "R";
		String identifiantRattachement = null;
		FileItem file = null;

		try {
			// Récupération et vérification de la validité de l'environnement
			envirInput = request.getParameter("env");
			environnement = EnvironnementHelper.determinerEnvironnement(envirInput);

			List<FileItem> items = uploadHandler.parseRequest(request);
			for (FileItem item : items) {

				if (item.getFieldName().equals("login")) {
					login = item.getString();
					continue;
				}

				if (item.getFieldName().equals("cmroc")) {
					cmroc = item.getString();
					continue;
				}

				if (item.getFieldName().equals("sens")) {
					sens = item.getString();
					continue;
				}

				if (item.getFieldName().equals("typeDossier")) {
					typeEntite = TypeEntite.fromString(item.getString());
					continue;
				}

				if (item.getFieldName().equals("filename")) {
					originalFileName = item.getString();
					// strip le chemin du fichier (EDDM libelle)
					if (originalFileName.lastIndexOf("/") > 0) {
						originalFileName = originalFileName.substring(originalFileName.lastIndexOf("/") + 1);
					} else if (originalFileName.lastIndexOf("\\") > 0) {
						originalFileName = originalFileName.substring(originalFileName.lastIndexOf("\\") + 1);
					}
					if (originalFileName.length() > 255) {
						String ext = FileHelper.getExtension(originalFileName);
						originalFileName = originalFileName.substring(0, 255 - (ext.length())) + ext;
					}
					continue;
				}

				if (item.getFieldName().equals("identifiant")) {
					identifiantRattachement = item.getString();
					continue;
				}

				if (!item.isFormField() && !item.getName().isEmpty()) {
					file = item;
				}
			}

			String eddocId = null;
			String newGeneratedId = null;

			if (file != null && login != null && cmroc != null && identifiantRattachement != null && typeEntite != null && environnement != null) {

				if (!cmroc.matches("\\d{4}")) {
					throw new CimutDocumentException("cmroc invalid : " + cmroc);
				}
				if (!login.matches("^[A-Za-z0-9\\s_\\-\\.]+$")) {
					throw new CimutDocumentException("login invalid : " + login);
				}

				if (!EnumSet.of(TypeEntite.PERSONNE, TypeEntite.PARTENAIRE, TypeEntite.ENTREPRISE).contains(typeEntite)) {
					if (EnumSet.of(TypeEntite.COURTIER, TypeEntite.RESEAUCOURTIERS, TypeEntite.INCONNU, TypeEntite.INTERNE).contains(typeEntite)) {
						typeEntite = null;
					}
				} else {
					if (!identifiantRattachement.matches("^[0-9A-Za-z\\s\\|]+$")) {
						throw new CimutDocumentException("identifiantRattachement invalid : " + identifiantRattachement);
					}
				}
				String extension = FileHelper.getExtension(file.getName());

				newGeneratedId = DocumentHelper.generateSudeNewId(extension);
				//
				logger.debug("newGeneratedId " + newGeneratedId);
				logger.debug("build document");

				Document document = DocumentHelper.setDefaultValue(new Document(), environnement);
				document.setId(newGeneratedId);
				document.setCmroc(cmroc);
				document.setDtcreate(new Date());
				document.setStatus("A traité");
				document.setMimeType(FileHelper.getTypeMime(file.getName()));
				document.setTypeDocument(Type.CODE_PIECE_JOINTE_SUDE);

				logger.debug("build json");

				DocumentMongo docMongo = document.getDocMongo();
				docMongo.setCmroc(OrganismeHelper.getOrganisme(cmroc));
				docMongo.setTutelle(cmroc);
				docMongo.setTypeEntiteRattachement(typeEntite);
				docMongo.setTypeDocument(document.getTypeDocument());

				docMongo.addAttribute("EDDM_LIBELLE", originalFileName);
				if (sens != null && "E".equals(sens)) {
					docMongo.addAttribute("EDDM_SENS", "E");
				}
				if (docMongo.getTypeEntiteRattachement() == TypeEntite.PERSONNE) {
					docMongo.setNumAdherent(identifiantRattachement);
				} else if (docMongo.getTypeEntiteRattachement() == TypeEntite.PARTENAIRE) {
					docMongo.setIdProf(identifiantRattachement);
				} else if (docMongo.getTypeEntiteRattachement() == TypeEntite.ENTREPRISE) {
					docMongo.setIdEntreprise(identifiantRattachement);
				}

				document.setJson(new fr.cimut.ged.entrant.beans.db.Json());
				document.getJson().setId(newGeneratedId);
				document.getJson().setOrganisme(OrganismeHelper.getOrganisme(cmroc));
				document.getJson().setData(DocumentHelper.stringify(docMongo));

				logger.info("ask EDDM " + OrganismeHelper.getOrganisme(cmroc) + " " + cmroc);

				logger.debug("SAVE FILE");
				File output = new File(DocumentHelper.getPlanDeClassement(document), newGeneratedId);
				file.write(output);
				try {
					eddocId = manager.addSude(document, environnement);
				} catch (Exception e) {
					if (!output.delete()) {
						logger.error("Impossible de supprimer le fichier " + output.getAbsolutePath());
					}
					logger.error("commentaire manquant", e);
					throw new Exception(e);
				}
				logger.info("Upload OK");
			}

			// on envoie le nouvelle eddoc-id 
			if (eddocId != null) {
				PrintWriter writer = response.getWriter();
				try {
					response.setStatus(200);
					response.setHeader("Content-Type", "text/html;charset=iso-8859-1");
					response.setHeader("Cache-Control", "no-cache");
					writer.write(eddocId);
					writer.flush();
				} catch (Exception e) {
					logger.error("commentaire manquant", e);
				} finally {
					writer.close();
				}
			} else {
				if (environnement == null) {
					logger.fatal("Environnement invalide (non géré) : " + envirInput);
					response.sendError(500, "Environnement invalide (non géré) : " + envirInput);
				} else {
					logger.error("Aucun identifiant valid");
					response.sendError(500, "Aucun identifiant valid");
				}
			}

		} catch (FileUploadException e) {
			logger.error("commentaire manquant", e);
			response.sendError(500, e.getMessage());
		} catch (CimutDocumentException e) {
			logger.error("commentaire manquant", e);
			response.sendError(400, e.getMessage());
		} catch (Exception e) {
			logger.fatal("commentaire manquant", e);
			response.sendError(500, e.getMessage());
		}
	}
}
