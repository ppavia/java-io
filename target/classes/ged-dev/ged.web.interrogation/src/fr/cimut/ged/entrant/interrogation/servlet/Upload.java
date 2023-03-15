/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.interrogation.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
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

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;

/**
 * Servlet implementation class GedeUpload NOTE : servlet à priori non utilisée à migrer vers le futur endpoint
 * générique qui réalisera un meilleur typage
 */
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@EJB
	Manager manager;

	@EJB
	fr.cimut.ged.entrant.service.DocumentMongoService mongo;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Upload() {
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
			Logger.getLogger(Upload.class).error("Erreur d'authentification " + credentials);
			response.sendError(401, "Authentification requise");
			return;
		}

		if (!ServletFileUpload.isMultipartContent(request)) {
			Logger.getLogger(Upload.class).error("Request is not multipart, please 'multipart/form-data' enctype for your form.");
			throw new IllegalArgumentException("Request is not multipart, please 'multipart/form-data' enctype for your form.");
		}
		ServletFileUpload uploadHandler = new ServletFileUpload(new DiskFileItemFactory());
		// max 10 Mb
		uploadHandler.setSizeMax(1024 * 1024 * 10);

		String envirInput = null;
		String environnement = null;

		String login = null;
		String extension = null;
		FileItem item_ = null;
		Document oldDoc = null;

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

				if (item.getFieldName().equals("extension")) {
					extension = item.getString();
					continue;
				}

				if (!item.isFormField() && !item.getName().isEmpty()) {
					oldDoc = manager.get(item.getName());
					if (oldDoc != null) {
						item_ = item;
					} else {
						throw new Exception("Occurence du document non trouvé : " + item.getName());
					}
				}
			}

			String newId = null;
			String eddocId = null;

			if (item_ != null && login != null && environnement != null) {
				Document newDoc = new Document();
				newDoc.setId(oldDoc.getId());
				newDoc.setJson(oldDoc.getJson());
				newDoc = DocumentHelper.mergeBean(oldDoc, newDoc);
				newId = manager.getNewUniqueFileName(newDoc);

				if (!newId.endsWith(extension)) {
					newId = newId.substring(0, newId.lastIndexOf(".")) + extension;
				}
				newDoc.setId(newId);

				// mise a jour oracle
				newDoc = manager.replace(oldDoc, newDoc, login, environnement);
				fr.cimut.ged.entrant.beans.mongo.DocumentMongo newJson = DocumentHelper.getDocMongoFromJson(newDoc);
				String cmroc = newJson.getCmroc();
				eddocId = newJson.getEddocId();

				// mise a jour mongo
				mongo.insertMongo(environnement, cmroc, newJson);
				mongo.removeMongo(environnement, cmroc, oldDoc.getId());
				Logger.getLogger(EddmManager.class).info("Upload OK");

				File output = new File(DocumentHelper.getPlanDeClassement(newDoc), newId);
				item_.write(output);

			}

			// on envoie le nouvelle id 
			if (newId != null) {
				PrintWriter writer = response.getWriter();
				try {
					response.setStatus(200);
					response.setHeader("Content-Type", "text/html;charset=iso-8859-1");
					response.setHeader("Cache-Control", "no-cache");
					writer.write(newId + "|" + eddocId);
					writer.flush();
				} catch (Exception e) {
					Logger.getLogger(Upload.class).error("commentaire manquant", e);
				} finally {
					writer.close();
				}
			} else {
				if (environnement == null) {
					Logger.getLogger(Upload.class).fatal("Environnement invalide (non géré) : " + envirInput);
					response.sendError(500, "Environnement invalide (non géré) : " + envirInput);
				} else {
					Logger.getLogger(Upload.class).error("Aucun identifiant valid");
					response.sendError(500, "Aucun identifiant valid");
				}
			}

		} catch (FileUploadException e) {
			Logger.getLogger(Upload.class).error("commentaire manquant", e);
			response.sendError(500, e.getMessage());

		} catch (Exception e) {
			Logger.getLogger(Upload.class).error("commentaire manquant", e);
			response.sendError(500, e.getMessage());
		}
	}

}