/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.interrogation.servlet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.service.DocumentService;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.FileHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class BinaryFileManager extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private Logger logger = Logger.getLogger(this.getClass());

	@EJB
	Metier metier;

	@EJB
	Manager manager;

	@EJB
	fr.cimut.ged.entrant.service.DocumentMongoService mongo;

	@EJB
	DocumentService documentService;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BinaryFileManager() {
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
		// max 10 Mb
		uploadHandler.setSizeMax(1024 * 1024 * 10);

		String envirInput = null;
		String environnement = null;

		String fileName = null;
		String binaryString = null;
		String cmroc = null;
		String identifiant = null;
		TypeEntite typeEntite = null;
		String typeDocument = null;
		String identifiantExterne = null;

		File output = null;
		FileOutputStream outputStream = null;
		String newGeneratedId = null;
		DocumentMongo docMongo = null;
		String eddocId = null;

		Map<String, String> extraParamFromRequest = new HashMap<String, String>();

		try {
			// Récupération et vérification de la validité de l'environnement
			envirInput = request.getParameter("env");
			environnement = EnvironnementHelper.determinerEnvironnement(envirInput);

			List<FileItem> items = uploadHandler.parseRequest(request);
			for (FileItem item : items) {
				String fieldName = item.getFieldName();
				if (null == fieldName) {
					continue;
				}

				if ("cmroc".equals(fieldName) || "ID_ORGANISME".equals(fieldName)) {
					cmroc = item.getString();
					continue;
				}
				if ("fileName".equals(fieldName)) {
					fileName = item.getString();
					extraParamFromRequest.put(fieldName, fileName);
					if (fileName.lastIndexOf("/") > 0) {
						fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
					} else if (fileName.lastIndexOf("\\") > 0) {
						fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
					}
					if (fileName.length() > 255) {
						String ext = FileHelper.getExtension(fileName);
						fileName = fileName.substring(0, 255 - (ext.length())) + ext;
					}
					continue;
				}
				if ("identifiant".equals(fieldName)) {
					identifiant = item.getString();
					continue;
				}
				if ("typeDossier".equals(fieldName)) {
					typeEntite = TypeEntite.fromString(item.getString());
					continue;
				}
				if ("typeDocument".equals(fieldName)) {
					typeDocument = item.getString();
					continue;
				}
				if ("externalId".equals(fieldName)) {
					identifiantExterne = item.getString();
					continue;
				}
				if ("binaryString".equals(fieldName)) {
					binaryString = item.getString();
					continue;
				}

				// on laisse l'appelant ajouter les parametres qu'ils souhaite pour l'indexation mongo
				extraParamFromRequest.put(fieldName, item.getString());
			}

			if (null != cmroc && null != binaryString && null != environnement) {

				// on commence par décoder le base 64
				byte[] decodeBase64 = Base64.decodeBase64(binaryString);

				Date dateCreation = new Date();

				// init d'une extension par défaut
				String extension = ".pdf";
				try {
					extension = FileHelper.getExtension(fileName);
				} catch (Exception e) {
					logger.warn("pas d'extension au fichier " + fileName + " attribution d'un extension par défaut : " + extension);
				}
				// TODO respecter le schéma de nommage TypeDocument_AAAAMMJJ_HHMMSS_RANDOM.ext
				// => imposer un type de document en parametre ou alors les client (alto par
				// exemple) ne devraient pas appeler ce endpoint
				newGeneratedId = DocumentHelper.generateFinalName("BINARY", extension);

				// init info oracle document
				Document document = DocumentHelper.setMinDefaultValue(new Document(), environnement);

				document.setLibelle(DocumentHelper.getTruncatedString(fileName, "Ged Entrante", 50));
				document.setOrigine("Ged Entrante");
				document.setService("Ged");
				document.setSite("G");
				
				document.setId(newGeneratedId);
				document.setCmroc(cmroc);
				document.setDtcreate(dateCreation);
				document.setMimeType(FileHelper.getTypeMime(fileName));
				document.setIdentifiantExterne(identifiantExterne);

				// init info mongo
				docMongo = new DocumentMongo();

				// init des valeurs par défaut si non défini
				// dans ce cas on est dans un upload pour SUDE
				// on ne realise l'appel mongo si pas une sude !

				// Dans le cas ou l'on valorise le show rule, c'est que l'on veut un referencement mongo, mais pas de creation de demande SUDE avec.
				// Ces document ne sont pas visible non plus depuis les IHM CDDE.
				if (typeDocument == null || !extraParamFromRequest.containsKey(GlobalVariable.ATTR_SHOW_RULE)) {
					if(typeDocument == null) {
						typeDocument = Type.CODE_PIECE_JOINTE_SUDE;
					}
					document.setStatus("A traité");
					docMongo.setStatus(document.getStatus());
				}

				document.setTypeDocument(typeDocument);

				docMongo.setCmroc(cmroc);
				docMongo.setTutelle(cmroc);
				docMongo.setDtCreate(new DateTime(dateCreation));

				// renommage à faire - ce n'est pas une erreur 
				// typeDocument <> typeDossier, on a fait ca au debut avec sebastien
				docMongo.setTypeDocument(typeDocument);
				docMongo.setTypeEntiteRattachement(typeEntite);
				docMongo.addAttribute("EDDM_LIBELLE", fileName);

				// ajout des infos supplémentaires de la requête pour indexation
				// ex: referenceDevis + fileName + show_rule
				for (Entry<String, String> entry : extraParamFromRequest.entrySet()) {
					if (entry.getValue() != null && !entry.getValue().isEmpty()) {
						docMongo.addAttribute(entry.getKey(), entry.getValue());
					}
				}

				docMongo.setId(newGeneratedId);
				docMongo.setCmroc(cmroc);

				// reaffect l'idenfiant selon le type de dossier
				if (null != identifiant) {
					if (typeEntite == TypeEntite.PERSONNE) {
						docMongo.setAssuInsee(identifiant);
					} else if (typeEntite == TypeEntite.PARTENAIRE) {
						docMongo.setIdProf(identifiant);
					} else if (typeEntite == TypeEntite.ENTREPRISE) {
						docMongo.setIdEntreprise(identifiant);
					}
				}

				// init info oracle json
				Json dbJson = new fr.cimut.ged.entrant.beans.db.Json();
				dbJson.setData(DocumentHelper.stringify(docMongo));
				dbJson.setId(newGeneratedId);
				dbJson.setOrganisme(cmroc);
				document.setJson(dbJson);

				// mise à jour oracle via le manager - c'est lui qui se charge de setter la reference eddoc			
				manager.add(document, environnement);

				// on récupère la reference eddoc_id
				eddocId = document.getEddocId();

				// on ecrit le binaire décodé dans son plan de classement
				output = new File(DocumentHelper.getPlanDeClassement(document), newGeneratedId);
				outputStream = new FileOutputStream(output);
				outputStream.write(decodeBase64);
				outputStream.close();

				// on insert dans mongoDB, mais on veut pas cree de DA.
				if (extraParamFromRequest.containsKey(GlobalVariable.ATTR_SHOW_RULE)) {
					mongo.insertMongo(environnement, cmroc, DocumentHelper.getDocMongoFromJson(document));
				}
				Logger.getLogger(EddmManager.class).info("Upload OK : " + newGeneratedId + " , " + cmroc);
			}

			// on envoie le nouvel eddoc-id 
			if (newGeneratedId != null) {
				PrintWriter writer = response.getWriter();
				try {

					response.setStatus(200);
					response.setHeader("Content-Type", "text/html;charset=iso-8859-1");
					response.setHeader("Cache-Control", "no-cache");
					writer.write(eddocId);
					writer.flush();
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				} finally {
					writer.close();
				}
			} else {
				if (environnement == null) {
					logger.fatal("Environnement invalide (non géré) : " + envirInput);
					response.sendError(500, "Environnement invalide (non géré) : " + envirInput);
				} else {
					logger.error("Aucun identifiant valide (cmroc " + cmroc + ")");
					response.sendError(500, "Aucun identifiant valid");
				}
			}

		} catch (Exception e) {

			logger.error(e.getMessage(), e); // on rollback ce qui faut (note à moi même : mongoDB est en dernier ;))
			if (null != outputStream) {
				outputStream.close();
			}

			// clean up file
			if (output != null) {
				if (!output.delete()) {
					logger.error("Rollback : Impossible de supprimer le fichier : " + output.getAbsolutePath() + " (" + cmroc + ")");
				}
			}

			// clean up EDDM
			if (eddocId != null) {
				try {
					metier.getEddmManager().remove(eddocId, cmroc, environnement);
				} catch (Exception e1) {
					logger.error("Rollback : Impossible de supprimer l'entree EDDM : " + eddocId + " (" + cmroc + ")", e1);
				}
			}

			response.sendError(500, e.getMessage());
		}
	}

}
