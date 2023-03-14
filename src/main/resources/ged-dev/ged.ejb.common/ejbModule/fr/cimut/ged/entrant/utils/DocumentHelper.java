package fr.cimut.ged.entrant.utils;

import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.ADHERENT_NUM;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.DETAIL_INTEGRATION;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.ENTREPRISE_CLASS_ETAB;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.ENTREPRISE_NUM_INTERNE;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.GARANTIE_CODE_GARANTIE;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.LIBELLE;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.NOM_DOCUMENT;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.PACK_CODE_PACK;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.PACK_CODE_PRODUIT;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.PARTENAIRE_NIVEAU;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.PARTENAIRE_NUM_INTERNE;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.PARTENAIRE_TYPE_PART;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.ROWLASTINDEX;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.SECTION_CLASSE_ETAB;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.SECTION_CODE_SECTION;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.SECTION_NUM_INTERNE;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.STATUT_INTEGRATION;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.SUDE_IDENTIFIANT;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.TYPE_DOCUMENT;
import static fr.cimut.ged.entrant.beans.RapportIntegrationHeader.TYPE_RATTACHEMENT;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

import javax.ejb.EJB;

import fr.cimut.ged.entrant.dao.TypeDao;
import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.dto.TypeFilters;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.cimut.ged.entrant.beans.DocumentUploadMassifWrapper;
import fr.cimut.ged.entrant.beans.RapportIntegrationHeader;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.UploadFileRequest;
import fr.cimut.ged.entrant.beans.UploadMassifResult;
import fr.cimut.ged.entrant.beans.UploadStatut;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.db.User;
import fr.cimut.ged.entrant.beans.mongo.Departement;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.Region;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutXmlException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.service.TypeService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DocumentHelper {

	private final static SimpleDateFormat spfDoy = new SimpleDateFormat("DDD");
	private final static SimpleDateFormat spfMonth = new SimpleDateFormat("MM");
	private final static SimpleDateFormat spfEntry = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat starwebDateFormat = new SimpleDateFormat("ddMMyyyy");
	private final static SimpleDateFormat spfYear = new SimpleDateFormat("yyyy");

	private static SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd_HHmmss");
	private static Long SEQUENCE = 0L;

	private static final Logger LOGGER = Logger.getLogger(DocumentHelper.class);

	//SecureRandom.getInstanceStrong() quand on sera en java 8
	

	private DocumentHelper() {

	}

	/**
	 * !! méthode à proscire. il faut seter tous les champ au mieux et non pas fourer GEDe partout !! Déprécié pour
	 * mettre en avant cette mauvaise pratique. GO to setMinDefaultValue + .setLibelle() .setOrigine() .setService()
	 * .setSite()
	 * 
	 * @param document
	 * @param environnement
	 * @return
	 * @throws CimutConfException
	 * @throws CimutFileException
	 */
	@Deprecated
	public static Document setDefaultValue(Document document, String environnement) {
		document.setLibelle("Ged Entrante");
		document.setOrigine("Ged Entrante");
		document.setNbpage(1);
		document.setTypepapier("A4BLAN");
		document.setIsFonddepage("non");
		document.setIsArchivage("non");
		document.setService("Ged");
		document.setNbexdc(1);
		// TODO le site doit être seté en fonction du type de document
		document.setSite("G");
		document.setIdstar(0);
		document.setTsstar(0);
		document.setLbchfp("Ged Entrante");
		document.setLbnmff("Ged Entrante");
		document.setSidstar(environnement);
		return document;
	}

	/**
	 * + .setLibelle() .setOrigine() .setService() .setSite() pour le site , si vous ne savez pas mettez 'G'
	 * 
	 * @param document
	 * @param environnement
	 * @return
	 */
	public static Document setMinDefaultValue(Document document, String environnement) {
		document.setNbpage(1);
		document.setTypepapier("absent");
		document.setIsFonddepage("non");
		document.setIsArchivage("non");
		document.setNbexdc(1);
		document.setIdstar(0);
		document.setTsstar(0);
		document.setLbchfp("Ged Entrante");
		document.setLbnmff("Ged Entrante");
		document.setSidstar(environnement);
		return document;
	}

	public static Document setDefaultMulticanalValues(Document document, String environnement) {
		document.setDtcreate(new Date());
		document.setNbpage(1);
		document.setIsFonddepage("non");
		document.setIsArchivage("non");
		document.setNbexdc(1);
		document.setSite("G");
		document.setProfilEditique(1);
		document.setIdstar(0);
		document.setTsstar(0);
		document.setLbchfp("gede");
		document.setSidstar(environnement);
		return document;
	}

	/**
	 * Supprime tout les caracteres indesirable.
	 * 
	 * @param input
	 * @return
	 */
	public static String sanitize(String input) {
		if (null != input) {
			input = StringUtils.stripAccents(input);
			input = input.replaceAll("\t", "");
			input = input.replaceAll("\n", "");
			input = input.replaceAll("[^A-Za-z0-9\\.\\s_\\-]", "");
		}
		return input;
	}

	/**
	 * recupere le fichier pour le document fourni
	 * 
	 * @param document
	 * @return
	 * @throws CimutFileException
	 * @throws CimutConfException
	 */
	public static File getFile(Document document) throws CimutFileException, CimutConfException {
		File file = new File(getPlanDeClassement(document), document.getId());
		if (!file.exists()) {
			LOGGER.warn("Fichier introuvable dans la nouvelle arborescence (" + file.getAbsolutePath()+ "). On va essayer dans l'ancienne.");
			file = new File(getOldPlanDeClassement(document), document.getId());
			if (!file.exists()) {
				throw new CimutFileException("Impossible d'acceder au fichier suivant : " + file.getAbsolutePath());
			}
		}
		return file;
	}

	/**
	 * recupere le fichier correspondant au document fourni afin de l'écrire.
	 * 
	 * @param document
	 * @return
	 * @throws CimutFileException
	 * @throws CimutConfException
	 * @throws GedeCommonException
	 */
	public static File getFileToSave(Document document) throws CimutFileException, CimutConfException, GedeCommonException {
		File file2save = new File(getPlanDeClassement(document), document.getId());
		if (file2save.exists()) {
			throw new GedeCommonException("Le fichier " + file2save.getAbsolutePath() + " à sauvegarder existe déjà dans le plan de classement.");
		}
		return file2save;
	}





	/**
	 * Get the bean DocumentMongo from the bean document
	 * 
	 * @param document
	 * @return
	 * @throws CimutDocumentException
	 */
	public static DocumentMongo getDocMongoFromJson(Document document) throws CimutDocumentException {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(document.getJson().getData(), DocumentMongo.class);
		} catch (Exception e) {
			throw new CimutDocumentException(e.getMessage(), e);
		}
	}

	/**
	 * Stringify the bean
	 * 
	 * @param document
	 * @return
	 * @throws CimutDocumentException
	 */
	public static String stringify(DocumentMongo document) throws CimutDocumentException {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.writeValueAsString(document);
		} catch (Exception e) {
			throw new CimutDocumentException(e.getMessage(), e);
		}
	}

	/**
	 * recupere le chemin dans lequel les documents doivent etre placés
	 * 
	 * @param document
	 * @return String Path
	 * @throws CimutConfException
	 * @throws CimutDocumentException
	 */
	@Deprecated
	public static String getOldPlanDeClassement(Document document) throws CimutFileException, CimutConfException {

		// init
		StringBuilder path = new StringBuilder();
		path.append(GlobalVariable.getDestinationPath());

		// build path (cmroc)
		path.append(OffuscatorHelper.offuscateFolder(document.getCmroc()) + File.separator);
		Date dtCreate = document.getDtcreate();

		// build path (date)
		String year = OffuscatorHelper.offuscateFolder(spfYear.format(dtCreate));
		path.append(year + File.separator);
		String dayOfYear = OffuscatorHelper.offuscateFolder(spfDoy.format(dtCreate));
		path.append(dayOfYear + File.separator);

		File directory = new File(path.toString());
		if (!directory.exists() && !directory.mkdirs()) {
			throw new CimutFileException("ne peux pas cree le repertoire du plan de classement suivant : " + directory.getAbsolutePath());
		}
		return path.toString();
	}

	/**
	 * recupere le nouveau chemin dans lequel les documents doivent etre placés
	 * Évolution septembre 2020 (DIGIT-561) - on change le chemin relatif vers ANNEE{AAAA}/CMROC/SITE(ORG,STAR)/MOIS{MM}
	 * 
	 * @param document
	 * @return String Path
	 * @throws CimutConfException
	 * @throws CimutDocumentException
	 */
	public static String getPlanDeClassement(Document document) throws CimutFileException, CimutConfException {

		// init
		StringBuilder path = new StringBuilder();
		path.append(GlobalVariable.getDestinationPath());

		Date dtCreate = document.getDtcreate();
		// build path (année)
		String year = OffuscatorHelper.offuscateFolder(spfYear.format(dtCreate));
		path.append(year + File.separator);
		
		// build path (cmroc)
		path.append(OffuscatorHelper.offuscateFolder(document.getCmroc()) + File.separator);

		// build path (site) --> gede
		path.append("gede").append(File.separator);

		// build path (mois)
		String month = OffuscatorHelper.offuscateFolder(spfMonth.format(dtCreate));
		path.append(month + File.separator);

		File directory = new File(path.toString());
		if (!directory.exists() && !directory.mkdirs()) {
			throw new CimutFileException("ne peux pas cree le repertoire du plan de classement suivant : " + directory.getAbsolutePath());
		}
		return path.toString();
	}

	/**
	 * Validate the minimum requirement for the document
	 * 
	 * @param document
	 * @throws CimutDocumentException
	 * @throws GedeCommonException
	 * @throws CimutConfException
	 * @throws CimutFileException
	 */
	public static void validate(Document document) throws CimutDocumentException {
		// controle les champs obligatoire pour l'insertion en base de donnee

		if (document.getId() == null || document.getId().isEmpty()) {
			throw new CimutDocumentException("Aucun identifiant fournit");
		}
		if (document.getCmroc() == null || document.getCmroc().isEmpty()) {
			throw new CimutDocumentException("Aucune tutelle fournit");
		}

		// needed for archivage
		if (document.getTypeDocument() == null || document.getTypeDocument().isEmpty()) {
			throw new CimutDocumentException("Le type de document n'est pas fournit");
		}

		DocumentMongo docMongo = DocumentHelper.getDocMongoFromJson(document);

		if (docMongo.getId() == null || docMongo.getId().isEmpty()) {
			throw new CimutDocumentException(GlobalVariable.ATTR_ID + " n'est pas fournit");
		}
		if (docMongo.getTutelle() == null || !docMongo.getTutelle().matches("^\\d{4}$")) {
			throw new CimutDocumentException(GlobalVariable.ATTR_TUTELLE + " n'est pas au bon format ou vide : " + docMongo.getTutelle());
		}
		if (docMongo.getCmroc() == null || !docMongo.getCmroc().matches("^\\d{4}$")) {
			throw new CimutDocumentException(GlobalVariable.ATTR_ID_ORGANISME + " n'est pas au bon format ou vide " + docMongo.getCmroc());
		}
		if (docMongo.getTypeDocument() == null || docMongo.getTypeDocument().isEmpty()) {
			throw new CimutDocumentException(GlobalVariable.ATTR_TYPE_DOCUMENT + " n'est pas fournit");
		} else {
			if (!docMongo.getTypeDocument().matches("^.{1,60}$")) {
				throw new CimutDocumentException(GlobalVariable.ATTR_TYPE_DOCUMENT + " n'est pas valid " + docMongo.getTypeDocument());
			}
		}
	}

	private static boolean checkFileAlreadyExists(Document document) throws CimutFileException, CimutConfException, GedeCommonException {
		return getFileToSave(document).exists();
	}

	/**
	 * Reconstruit un bean document depuis un HashMap provenant d'un xml d'entree du transcodage
	 * 
	 * @param xml
	 *            XML d'entrée du transcodage
	 * @param id
	 *            Nom du document
	 * @param environnement
	 *            Nom de l'environnement
	 * @return Document
	 * @throws CimutDocumentException
	 * @throws CimutXmlException
	 * @throws CimutConfException
	 * @throws IOException
	 */
	public static Document toDocument(File xml, String id, String environnement, String type, String idExt, String libelleDoc)
			throws CimutDocumentException, CimutXmlException, IOException, CimutConfException {

		Map<String, Map<String, String>> mappedDocument = FileHelper.loadXmlFile(xml);

		Date dateIntegration = new Date();

		boolean isAnUpdate = false;

		if (mappedDocument.containsKey(GlobalVariable.ATTR_DATABASE)
				&& mappedDocument.get(GlobalVariable.ATTR_DATABASE).containsKey(GlobalVariable.ATTR_ID)) {
			mappedDocument.get(GlobalVariable.ATTR_DATABASE).put(GlobalVariable.ATTR_ID,
					mappedDocument.get(GlobalVariable.ATTR_DATABASE).get(GlobalVariable.ATTR_ID));
		} else {
			mappedDocument.get(GlobalVariable.ATTR_DATABASE).put(GlobalVariable.ATTR_ID, id);
		}
		mappedDocument.get(GlobalVariable.ATTR_JSON).put(GlobalVariable.ATTR_ID, id);

		// FOR JSON
		ObjectMapper mapper = getObjectMapper();
		ObjectNode jsonColumn = mapper.createObjectNode();
		mapper.setSerializationInclusion(Include.ALWAYS);

		// OUTPUT
		Document document = new Document();

		// date d'integration
		document.setDtcreate(dateIntegration);
		mappedDocument.get(GlobalVariable.ATTR_JSON).put(GlobalVariable.ATTR_DTINTEGRATION, spfEntry.format(dateIntegration));

		// contruit le Document

		for (Entry<String, Map<String, String>> mapped : mappedDocument.entrySet()) {

			String key = mapped.getKey();
			Map<String, String> databaseHm = mapped.getValue();

			if (key.equalsIgnoreCase(GlobalVariable.ATTR_DATABASE)) {
				for (Map.Entry<String, String> entry : databaseHm.entrySet()) {
					String keyDbHm = entry.getKey();
					String value = entry.getValue();
					if (keyDbHm.equalsIgnoreCase(GlobalVariable.ATTR_ID)) {
						// TODO : Vérifier ce qui est poussé dans mongo
						document.setId(value);
					} else if (keyDbHm.equalsIgnoreCase(GlobalVariable.ATTR_TUTELLE)) {
						document.setCmroc(value);
					}  else if (keyDbHm.equalsIgnoreCase(GlobalVariable.ATTR_PRIORITE)) {
						//document.setprofilEditique(Integer.parseInt(value));
						// TODO/FIXME => set the appriopriate value for confidentiality
						document.setProfilEditique(0);
					}
				}
			} else if (key.equalsIgnoreCase(GlobalVariable.ATTR_JSON)) {
				// BUILD THE JSON
				for (String keyDbHm : databaseHm.keySet()) {
					String value = databaseHm.get(keyDbHm);
					if (keyDbHm.equalsIgnoreCase(GlobalVariable.ATTR_TUTELLE)) {
						jsonColumn.put(GlobalVariable.ATTR_ID_ORGANISME, OrganismeHelper.getOrganisme(value));
						jsonColumn.put(GlobalVariable.ATTR_TUTELLE, value);
					} else if (keyDbHm.equalsIgnoreCase(GlobalVariable.ATTR_CODE_POSTAL)) {
						String region = Region.getRegion(value);
						String departement = Departement.getDepartement(value);
						if (!region.isEmpty()) {
							jsonColumn.put(GlobalVariable.ATTR_REGION, region);
						}
						if (!departement.isEmpty()) {
							jsonColumn.put(GlobalVariable.ATTR_DEPARTEMENT, departement);
						}
						jsonColumn.put(keyDbHm, value);
					} else if (GlobalVariable.DATES.contains(keyDbHm)) {
						ObjectNode date = mapper.createObjectNode();
						try {
							value = DateHelper.convert(value);
							date.put("$date", value);
							jsonColumn.set(keyDbHm, date);
						} catch (ParseException e) {
							throw new CimutDocumentException("Erreur lors de la convertion de la date en JSON " + document.getId() + " " + value, e);
						}
					} else {
						// aspbtp n'ont pas mis le nom prenom dans le bloc adresse 1 :`/
						if (keyDbHm.equalsIgnoreCase("ADRESSE1")) {
							keyDbHm = "ADRESSE2";
						} else if (keyDbHm.equalsIgnoreCase("ADRESSE2")) {
							keyDbHm = "ADRESSE3";
						} else if (keyDbHm.equalsIgnoreCase("ADRESSE3")) {
							keyDbHm = "ADRESSE4";
						} else if (keyDbHm.equalsIgnoreCase("ADRESSE4")) {
							keyDbHm = "ADRESSE5";
						} else if (keyDbHm.equalsIgnoreCase("ADRESSE5")) {
							keyDbHm = "ADRESSE6";
						} else if (keyDbHm.equalsIgnoreCase("ADRESSE6")) {
							keyDbHm = "ADRESSE7";
						}

						jsonColumn.put(keyDbHm, value);
					}
				}
				StringBuilder adresse1 = new StringBuilder();
				// on ne traite que les adresses pour les personnes.
				if (databaseHm.containsKey(GlobalVariable.ATTR_PRENOM) || databaseHm.containsKey(GlobalVariable.ATTR_NOM_DE_FAMILLE)) {
					if (databaseHm.containsKey(GlobalVariable.ATTR_NOM_DE_FAMILLE)) {
						adresse1.append(databaseHm.get(GlobalVariable.ATTR_NOM_DE_FAMILLE));
					}
					if (databaseHm.containsKey(GlobalVariable.ATTR_PRENOM)) {
						if (adresse1.length() > 0) {
							adresse1.append(" ");
						}
						adresse1.append(databaseHm.get(GlobalVariable.ATTR_PRENOM));
					}
					jsonColumn.put("ADRESSE1", adresse1.toString());
				}
			} else if (key.equalsIgnoreCase(GlobalVariable.ATTR_ATTRIBUTS)) {
				// ADD THE ATTRIBUTES TO THE JSON
				ObjectNode attributes = mapper.createObjectNode();
				for (String keyDbHm : databaseHm.keySet()) {
					String value = databaseHm.get(keyDbHm);
					attributes.put(keyDbHm, value);
				}
				jsonColumn.set(GlobalVariable.ATTR_ATTRIBUTS, attributes);
			}
		}
		if(type == null || type.equals("")) {
			document.setTypeDocument(GlobalVariable.DEFAULT_ATTR_TYPE_DOCUMENT);
		} else {
			document.setTypeDocument(type);
		}
		if(idExt != null || idExt != "") {
			document.setIdentifiantExterne(idExt);
		}
		if (!isAnUpdate) {
			if (!jsonColumn.has(GlobalVariable.ATTR_STATUS) || jsonColumn.get(GlobalVariable.ATTR_STATUS).asText().isEmpty()) {
				jsonColumn.put(GlobalVariable.ATTR_STATUS, GlobalVariable.STATUS_A_TRAITER);
			}
			// besoin pour la purge
			document.setStatus(jsonColumn.get(GlobalVariable.ATTR_STATUS).asText());
		}

		// STRINGIFY JSON
		String jsonStringified = null;
		try {
			jsonStringified = mapper.writeValueAsString(jsonColumn);
		} catch (JsonProcessingException e) {
			throw new CimutDocumentException("Erreur lors de la serialisation en JSON " + document.getId(), e);
		}

		// AFFECT JSON
		Json json = new Json();
		json.setId(document.getId());
		json.setData(jsonStringified);
		json.setOrganisme(OrganismeHelper.getOrganisme(document.getCmroc()));
		document.setJson(json);
		document = setMinDefaultValue(document, environnement);
		try {
			document.setMimeType(FileHelper.getTypeMime(id));
		} catch (CimutFileException e) {
			throw new CimutDocumentException(e);
		}

		return document;
	}

	public static boolean checkRight(Document document, User user) throws CimutConfException {
		if (!OrganismeHelper.getTutelles(user.getCmroc()).contains(document.getCmroc())) {
			return false;
		}
		if (user.getLevelConfidentiel() < document.getProfilEditique()) {
			return false;
		}
		return true;
	}

	/**
	 * Merge two Document bean. Modify only values which are defined in the new bean, Keep the old value if not
	 * 
	 * @param oldDoc
	 * @param newDoc
	 * @return
	 * @throws CimutDocumentException
	 */
	public static Document merge(Document oldDoc, Document newDoc) throws CimutDocumentException {
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(newDoc.getClass());
			// Iterate over all the attributes
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {

				if (descriptor.getWriteMethod() != null) {

					Method method = descriptor.getReadMethod();
					if (method == null) {
						continue;
					}
					Object targetValue = method.invoke(oldDoc);
					Object destinationValue = method.invoke(newDoc);

					if (targetValue == null || descriptor.getPropertyType().equals(Json.class)) {
						// faudrait pas non plus synchro le Json ...
						continue;
					} else if (destinationValue == null) {
						descriptor.getWriteMethod().invoke(newDoc, targetValue);
					} else if (descriptor.getPropertyType().equals(long.class)) {
						Long targetValueLong = (Long) targetValue;
						Long destinationValueLong = (Long) destinationValue;
						if (destinationValueLong == 0 && targetValueLong != 0) {
							descriptor.getWriteMethod().invoke(newDoc, targetValue);
						}
					} else {
						// on met a jour le TypeMime, vu que l'extension a été modifié
						if (descriptor.getName().equals("id") && !targetValue.equals(destinationValue)) {
							String newExt = FileHelper.getExtension((String) destinationValue);
							String oldExt = FileHelper.getExtension((String) targetValue);
							if (!newExt.equals(oldExt)) {
								newDoc.setMimeType(FileHelper.getTypeMime((String) destinationValue));
							}
						}
					}
				}
			}
			LOGGER.info("merge OK");
			return newDoc;
		} catch (Exception e) {
			throw new CimutDocumentException(e);
		}
	}

	public static Document mergeBean(Document oldDoc, Document newDoc) throws CimutDocumentException {

		LOGGER.debug("mergeBean(" + oldDoc.getId() + "" + newDoc.getId() + ")");

		try {

			newDoc = merge(oldDoc, newDoc);

			DocumentMongo target = DocumentHelper.getDocMongoFromJson(oldDoc);
			DocumentMongo destination = DocumentHelper.getDocMongoFromJson(newDoc);

			BeanInfo beanInfo = Introspector.getBeanInfo(destination.getClass());

			// Iterate over all the attributes
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				// Only copy writable attributes
				if (descriptor.getWriteMethod() != null) {
					Object targetValue = descriptor.getReadMethod().invoke(target);
					Object destinationValue = descriptor.getReadMethod().invoke(destination);

					if (targetValue == null) {
						continue;
					} else if (destinationValue == null) {
						if (descriptor.getName().equals("tutelle")) {
							destination.setTutelle((String) targetValue);
							destination.setCmroc(OrganismeHelper.getOrganisme(destination.getTutelle()));
							newDoc.setCmroc(destination.getTutelle());
						} else {
							descriptor.getWriteMethod().invoke(destination, targetValue);
						}
					} else {
						// on traite le Map<String,String>
						if (descriptor.getPropertyType().equals(Map.class)) {
							Map<String, String> oldMap = (Map<String, String>) targetValue;
							Map<String, String> newMap = (Map<String, String>) destinationValue;

							// on ajoute les ancienne au nouvelle
							for (Map.Entry<String, String> entry : oldMap.entrySet()) {
								String key = entry.getKey();
								if (!newMap.containsKey(key)) {
									newMap.put(key, entry.getValue());
								}
							}

							// on retire les champs vide si pas present precedemment
							Iterator<Entry<String, String>> entries = newMap.entrySet().iterator();
							while (entries.hasNext()) {
								Entry<String, String> thisEntry = entries.next();
								String key = thisEntry.getKey();
								String val = thisEntry.getValue();
								if (oldMap.containsKey(key) && val.isEmpty()) {
									entries.remove();
								}
							}
							descriptor.getWriteMethod().invoke(destination, newMap);
						}
						// on traite les strings
						else if (descriptor.getPropertyType().equals(String.class)) {

							if (!targetValue.equals(destinationValue)) {

								String finalValue = (String) destinationValue;
								// Attention ici, si la tutelle a changé, faut mettre à jour l'organisme
								if (descriptor.getName().equals("tutelle")) {
									destination.setCmroc(OrganismeHelper.getOrganisme(finalValue));
									newDoc.setCmroc(finalValue);
								}
								// le code postal a changer, on recalcule region et departement
								else if (descriptor.getName().equals("codePostal")) {
									// de meme, si le code postal a changer ... on recalcule la region et le department
									String region = Region.getRegion(finalValue);
									String departement = Departement.getDepartement(finalValue);
									destination.setRegion(region);
									destination.setDepartement(departement);
								} else if (descriptor.getName().equals("typeDossier")) {
									if (finalValue != null && finalValue.length() > 60) {
										finalValue = finalValue.substring(0, 60);
									}
									newDoc.setTypeDocument(finalValue);
								}
							}
						}
					}
				}
			}

			if (destination.getStatus() != null) {
				newDoc.setStatus(destination.getStatus());

				// on efface l'erreur DA si l'utilisateur passe du status "En erreur" a autre chose.
				// on va retraiter la DA dans ce cas.
				if (target.getStatus() != null && target.getStatus().equals(GlobalVariable.STATUS_ERROR)
						&& !destination.getStatus().equals(GlobalVariable.STATUS_ERROR)) {
					if (destination.getErreurDa() != null) {
						destination.setErreurDa(null);
					}
				}

			}
			if (destination.getTypeEntiteRattachement() != null) {
				if (destination.getTypeEntiteRattachement() != TypeEntite.PERSONNE) {
					destination.setNumAdherent(null);
					destination.setAssuInsee(null);
					destination.setNom(null);
					destination.setPrenom(null);
					Map<String, String> attributes = destination.getAttributes();
					// clean up addresse as we need it only in case of assuré
					for (int i = 1; i < 8; i++) {
						if (attributes.containsKey("ADRESSE" + i)) {
							attributes.remove("ADRESSE" + i);
						}
					}

					if (attributes.containsKey("DATE_NAISSANCE_PATIENT")) {
						attributes.remove("DATE_NAISSANCE_PATIENT");
					}
					destination.setAttributes(attributes);
				}
				if (destination.getTypeEntiteRattachement() != TypeEntite.PARTENAIRE) {
					destination.setIdProf(null);
				}
				if (destination.getTypeEntiteRattachement() != TypeEntite.ENTREPRISE) {
					destination.setIdEntreprise(null);
					destination.setNomEntreprise(null);
					destination.setNumEntreprise(null);
				}
			}

			LOGGER.debug("mergeBean Almost finished");
			newDoc.getJson().setData(DocumentHelper.stringify(destination));
			newDoc.getJson().setId(newDoc.getId());
			newDoc.getJson().setOrganisme(newDoc.getCmroc());
			return newDoc;
		} catch (Exception e) {
			throw new CimutDocumentException(e);
		}
	}

	public static String diff(DocumentMongo target, DocumentMongo destination) throws CimutDocumentException {
		try {

			if (target.getId() == null) {
				throw new CimutDocumentException("target.getId() est null");
			} else if (destination.getId() == null) {
				throw new CimutDocumentException("destination.getId() est null");
			}

			BeanInfo beanInfo = Introspector.getBeanInfo(destination.getClass());

			StringBuilder output = new StringBuilder();

			// Iterate over all the attributes
			for (PropertyDescriptor descriptor : beanInfo.getPropertyDescriptors()) {
				// Only copy writable attributes
				if (descriptor.getWriteMethod() != null) {

					// get Values
					Object targetValue = descriptor.getReadMethod().invoke(target);
					Object destinationValue = descriptor.getReadMethod().invoke(destination);

					// nothing
					if (targetValue == null && destinationValue == null) {
						continue;
					}

					// Add
					else if (targetValue == null && destinationValue != null) {
						if (descriptor.getPropertyType().equals(String.class) && ((String) destinationValue).isEmpty()) {
							continue;
						}
						output.append("Ajout " + descriptor.getName() + " : " + destinationValue + "\n");
					}
					// remove
					else if (destinationValue == null && targetValue != null) {
						output.append("Suppression " + descriptor.getName() + " : " + targetValue + "\n");
					}
					// modified ?
					else {
						// loop over the map
						if (descriptor.getPropertyType().equals(Map.class)) {

							Map<String, String> oldMap = (Map<String, String>) targetValue;
							Map<String, String> newMap = (Map<String, String>) destinationValue;

							for (Map.Entry<String, String> entry : oldMap.entrySet()) {
								String key = entry.getKey();
								String value = entry.getValue();
								// delete
								if (!newMap.containsKey(key) || (newMap.get(key).isEmpty() && !value.isEmpty())) {
									output.append("Suppression " + key + " : " + value + "\n");
								}
								// modified
								else if (!value.equals(newMap.get(key))) {
									output.append("Modif " + key + " : " + value + " => " + newMap.get(key) + "\n");
								}
							}
							//add
							for (Map.Entry<String, String> entry : newMap.entrySet()) {
								String key = entry.getKey();
								String value = entry.getValue();
								if (!oldMap.containsKey(key) && !value.isEmpty()) {
									output.append("Ajout " + key + " : " + value + "\n");
								}
							}
						} else if (descriptor.getPropertyType().equals(String.class)) {
							if (!targetValue.equals(destinationValue)) {
								String oldValue = (String) targetValue;
								String newValue = (String) destinationValue;
								if (oldValue.isEmpty() && newValue.isEmpty()) {
									continue;
								} else if (oldValue.isEmpty() && !newValue.isEmpty()) {
									output.append("Ajout " + descriptor.getName() + " : " + targetValue + " => " + newValue + "\n");
								} else if (!oldValue.isEmpty() && newValue.isEmpty()) {
									output.append("Suppression " + descriptor.getName() + " : " + targetValue + "\n");
								} else if (!oldValue.equals(newValue)) {
									output.append("Modif " + descriptor.getName() + " : " + targetValue + " => " + destinationValue + "\n");
								}
							}
						} else if (descriptor.getPropertyType().equals(DateTime.class)) {
							if (!targetValue.equals(destinationValue)) {
								output.append("Modif " + descriptor.getName() + " : " + targetValue + " => " + destinationValue + "\n");
							}
						}
					}
				}
			}
			return output.toString();
		} catch (Exception e) {
			throw new CimutDocumentException(e);
		}

	}

	public static boolean hasOwnerChanged(DocumentMongo oldJson, DocumentMongo newJson) {
		boolean hasProprietaireChanged = false;

		TypeEntite newTypeDoc = newJson.getTypeEntiteRattachement();
		TypeEntite oldTypeDoc = oldJson.getTypeEntiteRattachement();

		if (newTypeDoc == null && oldTypeDoc == null) {
			hasProprietaireChanged = false;
		} else if (newTypeDoc != null && oldTypeDoc == null) {
			hasProprietaireChanged = true;
		} else if (newTypeDoc == null && oldTypeDoc != null) {
			hasProprietaireChanged = true;
		} else if (oldTypeDoc.equals(newTypeDoc)) {
			if (oldJson.getTypeEntiteRattachement() == TypeEntite.PERSONNE) {
				if (oldJson.getNumAdherent() == null) {
					if (newJson.getNumAdherent() != null) {
						hasProprietaireChanged = true;
					}
				} else if (!oldJson.getNumAdherent().equals(newJson.getNumAdherent())) {
					hasProprietaireChanged = true;
				}
				if (oldJson.getAssuInsee() == null) {
					if (newJson.getAssuInsee() != null) {
						hasProprietaireChanged = true;
					}
				} else if (!oldJson.getAssuInsee().equals(newJson.getAssuInsee())) {
					hasProprietaireChanged = true;
				}
			} else if (oldJson.getTypeEntiteRattachement() == TypeEntite.PARTENAIRE) {
				if (oldJson.getIdProf() == null) {
					if (newJson.getIdProf() != null) {
						hasProprietaireChanged = true;
					}
				} else if (!oldJson.getIdProf().equals(newJson.getIdProf())) {
					hasProprietaireChanged = true;
				}
			} else if (oldJson.getTypeEntiteRattachement() == TypeEntite.ENTREPRISE) {
				if (oldJson.getIdEntreprise() == null) {
					if (newJson.getIdEntreprise() != null) {
						hasProprietaireChanged = true;
					}
				} else if (!oldJson.getIdEntreprise().equals(newJson.getIdEntreprise())) {
					hasProprietaireChanged = true;
				}
			}
		} else {
			hasProprietaireChanged = true;
		}
		return hasProprietaireChanged;
	}

	/**
	 * @param typeDoc
	 * @param extension
	 *            (.pdf for example)
	 * @return typeDoc_date_heure_sequence.extension
	 */
	public static String generateFinalName(String typeDoc, String extension) {
		return typeDoc + "_" + formater.format(new Date()) + "_" + (SEQUENCE++) + extension;
	}

	/**
	 * go to generateFinalName !
	 */
	@Deprecated
	public static String generateSudeNewId(String extension) {
		return "SUDE_" + formater.format(new Date()) + "_" + (SEQUENCE++) + extension;
	}

	public static String saveFile(Document document, File fileSource) throws CimutDocumentException {
		File fileDest;
		try {
			fileDest = DocumentHelper.getFileToSave(document);
			FileUtils.copyFile(fileSource, fileDest);
		} catch (Exception e) {
			throw new CimutDocumentException("Erreur lors de la sauvegarde du document", e);
		}
		return fileDest.getAbsolutePath();
	}

	/**
	 * Supprime le fichier physique du file system
	 * 
	 * @param document
	 * @throws CimutConfException
	 * @throws CimutFileException
	 */
	public static void deleteFile(Document document) throws CimutFileException, CimutConfException {
		File file = getFile(document);
		if (!file.delete()) {
			throw new CimutFileException("Impossible de supprimer le fichier : " + file.getAbsolutePath());
		} else {
			Logger.getLogger(DocumentHelper.class)
					.info("Fichier " + file.getAbsolutePath() + "supprimé. callstack :" + Thread.currentThread().getStackTrace());
		}
	}

	/**
	 * valorise le json utilisé par eddmanager pour le rattachement...
	 * 
	 * @param document
	 * @param typeEntite
	 * @param identifiant
	 * @param sens
	 *            : "E" émission , "R" réception
	 * @throws CimutConfException
	 * @throws CimutDocumentException
	 * @throws GedeCommonException
	 */
	public static void addInfoRattachement(Document document, TypeEntite typeEntite, String identifiant, String sens)
			throws GedeCommonException, CimutConfException, CimutDocumentException {

		DocumentMongo docMongo = new DocumentMongo();

		switch (typeEntite) {
		case PERSONNE:
			if (identifiant.matches("^[0-9a-zA-Z]{1,12}$")) {
				docMongo.setNumAdherent(identifiant);
			} else if (identifiant.matches("^\\d{13}$")) {
				docMongo.setAssuInsee(identifiant);
			} else {
				throw new GedeCommonException("L'identifiant de la personne n'est pas au bon format : (1 à 12 characteres alpha numerique) si "
						+ GlobalVariable.ATTR_NUM_ADHERENT + ", (13 chiffres) si " + GlobalVariable.ATTR_ASSU_INSEE);
			}
			break;
		case ENTREPRISE:
			docMongo.setIdEntreprise(identifiant);
			break;
		case SECTION:
			docMongo.setIdEntreprise(identifiant);
			break;
		case PARTENAIRE:
			docMongo.setIdProf(identifiant);
			break;
		case PACK:
			docMongo.setCodePack(identifiant);
			//			docMongo.setCodeProduit();
			docMongo.addAttribute("SHOW_RULE", GlobalVariable.SHOW_RULE_TABLEAU_RMBT);
			break;
		default:
			throw new GedeCommonException("Type d'entité non supportée");
		}

		docMongo.setCmroc(OrganismeHelper.getOrganisme(document.getCmroc()));
		docMongo.setTutelle(document.getCmroc());
		docMongo.setTypeEntiteRattachement(typeEntite);
		docMongo.setTypeDocument(document.getTypeDocument());

		// ils veulent la nom du fichier original comme libelle dans EDDM
		docMongo.addAttribute("EDDM_LIBELLE", document.getLibelle());

		docMongo.addAttribute("EDDM_SENS", sens);

		document.setJson(new fr.cimut.ged.entrant.beans.db.Json());
		document.getJson().setOrganisme(OrganismeHelper.getOrganisme(document.getCmroc()));
		document.getJson().setData(DocumentHelper.stringify(docMongo));
		document.getJson().setId(document.getId());
	}

	/**
	 * Dezipe un fichier zip reçu brut vers le repertoire de destination demandée.
	 * 
	 * @param bytes
	 * @param destinationRepository
	 * @return zipExtractPath, le repertoire de destination de l'extraction
	 * @throws IOException
	 * @throws GedeCommonException 
	 * @throws ZipException
	 */
	public static String saveAndExtractZipFromData(byte[] bytes, String destinationRepository) throws IOException, GedeCommonException {
		// Enregistrement du ZIP
		File zipDir = new File(destinationRepository);
		if (!zipDir.exists()) {
			zipDir.mkdir();
		}
		UUID uuid = UUID.randomUUID();
		String fileName = zipDir.getCanonicalPath() + File.separator + uuid + "_uploaded.zip";
		File tempZipFile = new File(fileName);
		FileUtils.writeByteArrayToFile(tempZipFile, bytes);
		// Le repertoire de dezipage est crée à la volée
		String zipExtractPath = zipDir.getCanonicalPath() + File.separator + uuid + "_extract";
		// Dezipage
		ZipFile zipFile;
		try {
			zipFile = new ZipFile(fileName);
			// Contrôle sur le nombre de fichier présent dans le zip
			long zipMaxFileCount = GlobalVariable.getZipMaxFileCount();
			// On retire le fichier d'index du nombre total de fichier
			if (zipFile.getFileHeaders().size() - 1 > zipMaxFileCount) {
				// Suppression du zip et lancement de l'exception
				FileUtils.forceDelete(tempZipFile);
				throw new GedeCommonException("Le fichier Zip transmis contient plus de " + zipMaxFileCount + " fichiers à intégrer");
			}
			zipFile.extractAll(zipExtractPath);
			// Suppression une fois le dezipage effectué
			FileUtils.forceDelete(tempZipFile);
		} catch (ZipException e) {
			LOGGER.error("Problème lors du dezippage pour l'upload en masse IDOC", e);
			throw new GedeCommonException("Le fichier Zip transmis est corrompu ou n'est pas un fichier Zip valide");
		}
		return zipExtractPath;
	}

	public static UploadMassifResult extractDocumentsFromExtractedZip(String extractFolderPath, String env, String cmroc,
			TypeDao typeDao, boolean checkIndexFileLineNumber, boolean forceDeleteOnFail)
			throws IOException, GedeCommonException {
		UploadMassifResult uploadMassifResult = new UploadMassifResult();
		File zipDir = new File(extractFolderPath);
		if (!zipDir.exists()) {
			throw new IOException("Repertoire de dézipage non existant");
		}
		// On récupère le XLSX
		File indexFile = null;
		List<File> potentialIndexFileList = new ArrayList<File>();
		for (File file : zipDir.listFiles()) {
			if (file.isFile()) {
				if (file.getName().matches(".*\\.xlsx")) {
					potentialIndexFileList.add(file);
				}
			}
		}
		// Un ou des fichier xlsx ?
		if (potentialIndexFileList.size() > 0) {
			if (potentialIndexFileList.size() == 1) {
				// Un seul fichier, c'est forcément celui d'index
				indexFile = potentialIndexFileList.get(0);
			} else {
				// Si on a plusieurs fichier xlsx, on filtre en cherchant celui qui contient le mot 'index' dans son nom
				for (File file : potentialIndexFileList) {
					if (file.getName().matches(".*index.*\\.xlsx")) {
						indexFile = file;
						break;
					}
				}
				// Si aucun ne correspond, on lance une exception
				if (null == indexFile) {
					throw new GedeCommonException("Aucun fichier d'index au format xlsx et contenant le mot 'index' trouvé dans le fichier Zip transmis");
				}
			}
			// On réutilise le fichier d'index pour générer le rapport d'intégration. On garde donc sous la mains la référence vers le fichier
			uploadMassifResult.setRapportIntegration(indexFile);
		} else {
			// Pas de fichier d'index : on lance une exception
			throw new GedeCommonException("Aucun fichier d'index au format xlsx trouvé dans le fichier Zip transmis");
		}

		uploadMassifResult.init();
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(indexFile);
			Sheet documentsSheet = workbook.getSheetAt(0);
			// Contrôle de la longueur du fichier d'index
			long zipMaxIndexLines = GlobalVariable.getZipMaxIndexLines();
			if (checkIndexFileLineNumber && documentsSheet.getLastRowNum() > zipMaxIndexLines) {
				// Index partant de 0, et comme on ne compte pas la première ligne de header, on est au bon nombre de ligne
				throw new GedeCommonException("Le fichier Zip transmis à un fichier d'index qui contient plus de " + zipMaxIndexLines + " lignes à intégrer");
			}
			Iterator<Row> rowIterator = documentsSheet.iterator();
			// On contrôle la ligne d'entête puis on passe aux données avec le prochain next()
			List<String> headerErrors = DocumentHelper.checkHeaderRowUpload(rowIterator.next());
			if (!headerErrors.isEmpty()) {
				throw new GedeCommonException("Problème dans l'entête des colonnes. Le fichier d'index est invalide : " + headerErrors);
			}
			// On itère sur les lignes
			while (rowIterator.hasNext()) {
				UploadStatut uploadStatut = new UploadStatut();
				Row currentRow = rowIterator.next();
				boolean isEmpty = checkRowEmptyness(currentRow, ROWLASTINDEX);
				// Cas row entiérement vide, on est en fin de tableur
				if (isEmpty) {
					LOGGER.info("Ligne ne contenant pas de données dans le fichier xlsx. Préparation des documents complète.");
					break;
				}
				String documentName = null;
				List<String> rowValidityCheck = checkRowValidity(currentRow, ROWLASTINDEX);
				documentName = getCellStringValue(currentRow, NOM_DOCUMENT);
				if (rowValidityCheck.size() == 0 && documentName != null) {
					LOGGER.info("Traitement du document " + documentName);
					Document document = new Document();
					DocumentHelper.setMinDefaultValue(document, env);
					document.setCmroc(cmroc);
					document.setDtcreate(new Date());
					// On initialise le document avec les données dans le fichier d'index
					try {
						DocumentHelper.fullfillDocumentFromRow(document, currentRow, typeDao, cmroc, env);
						// Récupération du fichier sur le disque
						File documentFile = new File(zipDir + File.separator + documentName);
						if (!documentFile.exists()) {
							uploadStatut.setOk(false);
							uploadStatut.setErrorMessage("Le document " + documentName + " est introuvable dans le fichier zip envoyé");
						} else {
							TypeEntite typeEntite = TypeEntite.fromString(getCellStringValue(currentRow, TYPE_RATTACHEMENT));
							// Contexte SUDE : aucun contrôle sur type et taille
							// Sinon, on contrôle la taille et le type de fichier : pdf, max 150ko/page par défaut si pas de valeur en conf
							if (!typeEntite.equals(TypeEntite.SUDE)) {
								if (!FileHelper.getExtension(documentFile.getName()).equals(".pdf")) {
									uploadStatut.setOk(false);
									uploadStatut.setErrorMessage("Le document " + documentName + " n'est pas un fichier pdf");
								} else {
									PDDocument doc = null;
									try {
										doc = PDDocument.load(documentFile);
									} catch (Exception e) {
										throw new GedeCommonException("Problème de lecture du fichier " + documentName);
									}
									document.setNbpage(doc.getNumberOfPages());
									if (document.getNbpage() == 0) {
										document.setNbpage(1);
									}
									doc.close();
									int tailleMax = GlobalVariable.getTailleMaxFichierIdoc();
									if ((documentFile.length() / document.getNbpage()) > tailleMax) {
										uploadStatut.setOk(false);
										uploadStatut.setErrorMessage("Le document " + documentName + " à une taille supérieure à " + tailleMax / 1024 + "ko par page");
									} else {
										// On utilise un wrapper pour conserver le numéro de row, utile pour le rapport d"intégration
										DocumentUploadMassifWrapper documentUploadMassifWrapper = new DocumentUploadMassifWrapper();
										documentUploadMassifWrapper.setDocument(document);
										documentUploadMassifWrapper.setRowNum(currentRow.getRowNum());
										uploadMassifResult.getDocumentToUploadMap().put(documentUploadMassifWrapper, documentFile);
										uploadStatut.setOk(true);
									}
								}
							} else {
								// TODO Refactoriser ces 5 lignes avec les 5 mêmes plus haut
								// On utilise un wrapper pour conserver le numéro de row, utile pour le rapport d"intégration
								DocumentUploadMassifWrapper documentUploadMassifWrapper = new DocumentUploadMassifWrapper();
								documentUploadMassifWrapper.setDocument(document);
								documentUploadMassifWrapper.setRowNum(currentRow.getRowNum());
								uploadMassifResult.getDocumentToUploadMap().put(documentUploadMassifWrapper, documentFile);
								uploadStatut.setOk(true);
							}
						}
					} catch (GedeCommonException e) {
						LOGGER.error("Problème lors de la prépartation d'un document pour l'upload massif.", e);
						uploadStatut.setOk(false);
						uploadStatut.setErrorMessage(e.getMessage());
					} catch (CimutFileException e) {
						LOGGER.error("Problème lors de la prépartation d'un document pour l'upload massif.", e);
						uploadStatut.setOk(false);
						uploadStatut.setErrorMessage(e.getMessage());
					}
				} else {
					if (documentName != null) {
						LOGGER.warn("Problème dans le traitement du document " + documentName + " : " + rowValidityCheck.toString());
					} else {
						LOGGER.warn("Problème dans le traitement d'un document : " + rowValidityCheck.toString());
					}
					uploadStatut.setOk(false);
					uploadStatut.setErrorMessage(rowValidityCheck.toString());
				}
				uploadMassifResult.getUploadStatutMap().put(currentRow.getRowNum(), uploadStatut);
			}
		} catch (Exception e) {
			// On essai supprime le repertoire zip temporaire. En cas d'exception, l'information n'est pas remonté dans le DocumentService
			try {
				if (zipDir != null) {
					// fermeture du workbook avant suppression du fichier xlsx
					if (workbook != null) {
						workbook.close();
						workbook = null;
					}
					if (forceDeleteOnFail) {
						FileUtils.forceDelete(zipDir);
					}
				}
			} catch (IOException e2) {
				LOGGER.warn("Problème lors de la suppression du réportoire d'extraction temporaire du fichier zip " + zipDir);
			}
			if (e instanceof GedeCommonException) {
				// Si c'est une exception qu'on connait on la laisse se propager telle quelle
				throw (GedeCommonException) e;
			}
			LOGGER.error("Problème technique lors de la lecture du fichier d'index " + indexFile.getName(), e);
			throw new GedeCommonException("Problème technique lors de la lecture du fichier d'index " + indexFile.getName(), e);
		} finally {
			// Fermeture du fichier XLSX à la fin du traitement dans tous les cas
			if (workbook != null) {
				workbook.close();
			}
			
		}
		return uploadMassifResult;
	}

	/**
	 * Mapping entre les données d'un classeur XLSX et un Document pour l'intégration en GEDe WARN : le contrôle su
	 * l'entête est à faire avant l'appel à cette méthode
	 * 
	 * @param typeDao
	 * 
	 * @param document,
	 *            le document à charger
	 * @param currentRow,
	 *            le row courrant du XLSX
	 * @return un message d'erreur ou null si tout est OK
	 * @throws GedeException
	 */
	public static void fullfillDocumentFromRow(Document document, Row currentRow, TypeDao typeDao, String cmroc, String env) throws GedeException {
		TypeEntite typeEntite = TypeEntite.fromString(getCellStringValue(currentRow, TYPE_RATTACHEMENT));
		document.getDocMongo().setTypeEntiteRattachement(typeEntite);

		String[] infos = new String[3];
		switch (typeEntite) {
		case PERSONNE:
			String identifiant = getCellStringValue(currentRow, ADHERENT_NUM);
			if (StringUtils.isBlank(identifiant)) {
				throw new GedeCommonException("Numéro d'adhérent obligatoire pour l'entité Personne");
			} else if (identifiant.matches("^[0-9a-zA-Z]{1,12}$")) {
				document.getDocMongo().setNumAdherent(identifiant);
			} else if (identifiant.matches("^\\d{13}$")) {
				document.getDocMongo().setAssuInsee(identifiant);
			} else {
				throw new GedeCommonException("L'identifiant de la personne n'est pas au bon format : (1 à 12 characteres alpha numerique) si "
						+ GlobalVariable.ATTR_NUM_ADHERENT + ", (13 chiffres) si " + GlobalVariable.ATTR_ASSU_INSEE);
			}
			break;
		case ENTREPRISE:
			infos[0] = getCellStringValue(currentRow, ENTREPRISE_NUM_INTERNE);
			infos[1] = getCellStringValue(currentRow, ENTREPRISE_CLASS_ETAB);
			if (StringUtils.isBlank(infos[0])) {
				throw new GedeCommonException("Numéro interne obligatoire pour l'entité Entreprise");
			}
			document.getDocMongo().setIdEntreprise(StringUtils.join(infos, "|").replaceAll("\\|*$", ""));
			break;
		case SECTION:
			infos[0] = getCellStringValue(currentRow, SECTION_NUM_INTERNE);
			infos[1] = getCellStringValue(currentRow, SECTION_CLASSE_ETAB);
			infos[2] = getCellStringValue(currentRow, SECTION_CODE_SECTION);
			if (StringUtils.isBlank(infos[0]) || StringUtils.isBlank(infos[2])) {
				throw new GedeCommonException("Numéro interne et code section obligatoires pour l'entité Section");
			}
			document.getDocMongo().setIdEntreprise(StringUtils.join(infos, "|").replaceAll("\\|*$", ""));
			break;
		case PARTENAIRE:
			infos[0] = getCellStringValue(currentRow, PARTENAIRE_NUM_INTERNE);
			infos[1] = getCellStringValue(currentRow, PARTENAIRE_TYPE_PART);
			infos[2] = getCellStringValue(currentRow, PARTENAIRE_NIVEAU);
			if (StringUtils.isBlank(infos[0]) || StringUtils.isBlank(infos[1]) || StringUtils.isBlank(infos[2])) {
				throw new GedeCommonException("Numéro interne, type partenaire et niveau obligatoires pour l'entité Partenaire");
			}
			document.getDocMongo().setIdProf(StringUtils.join(infos, "|").replaceAll("\\|*$", ""));
			break;
		case PACK:
			String packCodeProduit = StringUtils.trim(getCellStringValue(currentRow, PACK_CODE_PRODUIT));
			String packCodePack = StringUtils.trim(getCellStringValue(currentRow, PACK_CODE_PACK));
			if (StringUtils.isBlank(packCodeProduit) || StringUtils.isBlank(packCodePack)) {
				throw new GedeCommonException("Code produit et code pack obligatoires pour l'entité Pack");
			}
			// Contrôle de l'existence du pack
			if (!checkPackExistViaStarwebDao(cmroc, env, packCodeProduit, packCodePack)) {
				throw new GedeCommonException("Le pack " + packCodePack + " - " + packCodeProduit + " n'existe pas");
			}
			document.getDocMongo().setCodeProduit(packCodeProduit);
			document.getDocMongo().setCodePack(packCodePack);
			// XXX Appliquer cette règle seulement pour les Tableau de garantie ?
			document.getDocMongo().setShowRule(GlobalVariable.SHOW_RULE_TABLEAU_RMBT);
			break;
		case GARANTIE:
			String codeGarantie = StringUtils.trim(getCellStringValue(currentRow, GARANTIE_CODE_GARANTIE));
			if (StringUtils.isBlank(codeGarantie)) {
				throw new GedeCommonException("Code garantie obligatoire pour l'entité Garantie");
			}
			// Contrôle de l'existence de la garantie
			if (!checkGarantieExistViaStarwebDao(cmroc, env, codeGarantie)) {
				throw new GedeCommonException("La garantie " + codeGarantie + " n'existe pas");
			}
			document.getDocMongo().setCodeGarantie(codeGarantie);
			// XXX Appliquer cette règle seulement pour les Tableau de garantie ?
			document.getDocMongo().setShowRule(GlobalVariable.SHOW_RULE_TABLEAU_RMBT);
			break;
		case SUDE:
			String sudeId = getCellStringValue(currentRow, SUDE_IDENTIFIANT);
			if (StringUtils.isBlank(sudeId)) {
				throw new GedeCommonException("Identifiant SUDE obligatoire pour l'entité Sude");
			}
			document.getDocMongo().setSudeId(sudeId);
			break;
		default:
			throw new GedeCommonException("Type d'entité non gérée");
		}

		String fileName = getCellStringValue(currentRow, NOM_DOCUMENT);
		document.setMimeType(FileHelper.getTypeMime(fileName));

		String typeDoc = getCellStringValue(currentRow, TYPE_DOCUMENT);
		document.setTypeDocument(typeDoc);
		document.getDocMongo().setTypeDocument(typeDoc);
		document.setId(DocumentHelper.generateFinalName(typeDoc, FileHelper.getExtension(fileName)));

		String libelle = getCellStringValue(currentRow, LIBELLE);
		if (StringUtils.isBlank(libelle)) {
			libelle = typeDao.getLibelleByCode(typeDoc);
		}
		document.setLibelle(getTruncatedString(libelle, "", 50)); // Le libellé doit faire maximum 50 caractères

		document.setSite("G");// O
		document.setOrigine("Intégration IDOC");

		document.setService("IDOC");

		Date dateDebut = null;
		Date dateFin = null;
		String dateDebutString = getCellStringValue(currentRow, RapportIntegrationHeader.DATE_DEBUT_VALIDITE);
		if (StringUtils.isNotBlank(dateDebutString)) {
			try {
				dateDebut = starwebDateFormat.parse(dateDebutString);
				document.setDtDebutValidite(dateDebut);
			} catch(Exception e) {
				LOGGER.error("Problème de lecture de la date de début de validité pour une intégration IDOC massive : " + dateDebutString, e);
				throw new GedeCommonException("Format de la date de début de validité invalide. Format attendu : ddMMyyyy.");
			}
		}
		String dateFinString = getCellStringValue(currentRow, RapportIntegrationHeader.DATE_FIN_VALIDITE);
		if (StringUtils.isNotBlank(dateFinString)) {
			try {
				dateFin = starwebDateFormat.parse(dateFinString);
				document.setDtFinValidite(dateFin);
			} catch(Exception e) {
				LOGGER.error("Problème de lecture de la date de fin de validité pour une intégration IDOC massive : " + dateFinString, e);
				throw new GedeCommonException("Format de la date de fin de validité invalide. Format attendu : ddMMyyyy.");
			}
		}
		if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) {
			throw new GedeCommonException("Période de validité incohérente : date de fin inférieure à la date de début");
		}
	}

	private static String getCellStringValue(Row currentRow, RapportIntegrationHeader field) {
		return getCellStringValue(currentRow.getCell(field.getIndex()));
	}

	private static String getCellStringValue(Cell cell) {
		String value = null;
		if (cell != null) {
			if (cell.getCellTypeEnum().equals(CellType.STRING)) {
				value = cell.getStringCellValue();
			} else if (cell.getCellTypeEnum().equals(CellType.NUMERIC)) {
				value = String.valueOf((int) cell.getNumericCellValue());
			}
		}
		return value;
	}

	/**
	 * Vérifie si un row est valide ou non
	 * 
	 * @param row
	 * @param cellMaxCheck,
	 *            indique la largeur de cellule à contrôler. Attention, on part de 0, donc pour une largeur de 10, il
	 *            faut entrer 9 (check de 0 à 9). Le dernier élément est aussi contrôlé.
	 * @return une liste de message d'erreur si il y a un champ vide, une liste vide sinon
	 */
	public static List<String> checkRowValidity(Row row, int cellMaxCheck) {
		List<String> resultList = new ArrayList<String>();
		for (int i = 0; i <= cellMaxCheck; i++) {
			if (StringUtils.isEmpty(getCellStringValue(row.getCell(i))) && RapportIntegrationHeader.getByCellIndex(i).isMandatory()) {
				resultList.add(RapportIntegrationHeader.getByCellIndex(i).getLibelle() + " obligatoire non renseigné");
			}
		}
		return resultList;
	}

	/**
	 * Vérifie si un row est totalement vide ou non
	 * 
	 * @param row
	 * @param cellMaxCheck,
	 *            indique la largeur de cellule à contrôler. Attention, on part de 0, donc pour une largeur de 10, il
	 *            faut entrer 9 (check de 0 à 9). Le dernier élément est aussi contrôlé.
	 * @return true si le row est vide, false sinon
	 */
	public static boolean checkRowEmptyness(Row row, int cellMaxCheck) {
		boolean isEmpty = true;
		for (int i = 0; i <= cellMaxCheck; i++) {
			if (StringUtils.isNotEmpty(getCellStringValue(row.getCell(i)))) {
				isEmpty = false;
			}
		}
		return isEmpty;
	}

	public static List<String> checkHeaderRowUpload(Row headerRow) {
		List<String> columnErrors = new ArrayList<String>();
		for (RapportIntegrationHeader value : RapportIntegrationHeader.values()) {
			if(!checkCellStringValue(headerRow.getCell(value.getIndex()), value.getLibelle())) {
				columnErrors.add("Colonne '" + value.getLibelle() + "' non trouvée en position #" + (value.getIndex() + 1));
			}
		}
		return columnErrors;
	}

	private static boolean checkCellStringValue(Cell cell, String attendedValue) {
		String tempCell = getCellStringValue(cell);
		if (tempCell == null || !tempCell.equals(attendedValue)) {
			return false;
		}
		return true;
	}

	public static UploadMassifResult getDocumentsFromZipByteArray(byte[] bytes, String env, String cmroc, TypeDao typeDao)
			throws IOException, ZipException, GedeCommonException {
		// Contrôle de la taille du fichier zip
		long zipMaxSize = GlobalVariable.getZipMaxSize();
		if (bytes.length > zipMaxSize) {
			// On repasse en Mo pour le message d'erreur
			zipMaxSize = zipMaxSize/ 1024 / 1024;
			zipMaxSize = (zipMaxSize == 0) ? 1 : zipMaxSize;
			throw new GedeCommonException("Le fichier Zip transmis à une taille supérieur à " + zipMaxSize + " Mo");
		}
		String zipExtractPath = saveAndExtractZipFromData(bytes, File.separator + "tmp");
		return extractDocumentsFromExtractedZip(zipExtractPath, env, cmroc, typeDao, true, true);
	}

	public static ByteArrayOutputStream prepareRapportIntegration(File rapportIntegationInitial, UploadMassifResult uploadMassifResult)
			throws GedeCommonException, IOException {
		Workbook workbook = null;
		try {
			workbook = new XSSFWorkbook(rapportIntegationInitial);
			// Préparation des styles
			Font boldFont = workbook.createFont();
			boldFont.setBold(true);
			CellStyle backgroundGreenStyle = workbook.createCellStyle();
			CellStyle backgroundRedStyle = workbook.createCellStyle();
			backgroundGreenStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
			backgroundGreenStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			backgroundGreenStyle.setFont(boldFont);
			backgroundRedStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
			backgroundRedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			backgroundRedStyle.setFont(boldFont);
			Sheet documentsSheet = workbook.getSheetAt(0);
			// Insertion du statut et des éventuelles(s) erreur(s)
			for (Entry<Integer, UploadStatut> entry : uploadMassifResult.getUploadStatutMap().entrySet()) {
				Row row = documentsSheet.getRow(entry.getKey());
				Cell currentCell = row.getCell(STATUT_INTEGRATION.getIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (entry.getValue().isOk()) {
					currentCell.setCellValue("OK");
					currentCell.setCellStyle(backgroundGreenStyle);
				} else {
					currentCell.setCellValue("KO");
					currentCell.setCellStyle(backgroundRedStyle);
					row.getCell(DETAIL_INTEGRATION.getIndex(), Row.MissingCellPolicy.CREATE_NULL_AS_BLANK).setCellValue(entry.getValue().getErrorMessage());
				}
			}
			// Écriture dans le rapport suite aux ajouts de status
			ByteArrayOutputStream rapportOutputStream = new ByteArrayOutputStream();
			workbook.write(rapportOutputStream);
			rapportOutputStream.flush();
			return rapportOutputStream;
		} catch (Exception e) {
			LOGGER.error("Problème lors de la génération du rapport d'intégration pour l'upload massif", e);
			throw new GedeCommonException("Problème lors de la génération du rapport d'intégration pour l'upload massif");
		} finally {
			workbook.close();
		}
	}

	public static String getIdentifantEntite(Document document) throws GedeCommonException {
		TypeEntite typeEntite = document.getDocMongo().getTypeEntiteRattachement();
		String identifiant = null;
		switch (typeEntite) {
		case PERSONNE:
			identifiant = document.getDocMongo().getNumAdherent();
			if (StringUtils.isBlank(identifiant)) {
				identifiant = document.getDocMongo().getAssuInsee();
			}
			break;
		case ENTREPRISE:
			identifiant = document.getDocMongo().getIdEntreprise();
			break;
		case SECTION:
			identifiant = document.getDocMongo().getIdEntreprise();
			break;
		case PARTENAIRE:
			identifiant = document.getDocMongo().getIdProf();
			break;
		case PACK:
			identifiant = document.getDocMongo().getCodeProduit() + "|" + document.getDocMongo().getCodePack();
			break;
		case GARANTIE:
			identifiant = document.getDocMongo().getCodeProduit() + "|" + document.getDocMongo().getCodeGarantie();
			break;
		case SUDE:
			identifiant = document.getDocMongo().getSudeId();
			break;
		default:
			throw new GedeCommonException("Type d'entité non gérée");
		}
		if (StringUtils.isBlank(identifiant)) {
			throw new GedeCommonException("Aucun identifiant trouvé pour ce document. id " + document.getId());
		}
		return identifiant;
	}

	public static DocumentMongo completeDocMongo(Document document) throws GedeCommonException, CimutConfException {
		DocumentMongo docMongo = document.getDocMongo();

		if (docMongo.getTypeEntiteRattachement() == null) {
			throw new GedeCommonException("Le type d'entité doit être préalablement définie.");
		}

		if (StringUtils.isBlank(docMongo.getId())) {
			docMongo.setId(document.getId());
		}

		if (StringUtils.isBlank(docMongo.getTypeDocument())) {
			docMongo.setTypeDocument(document.getTypeDocument());
		}

		if (StringUtils.isBlank(docMongo.getCmroc())) {
			docMongo.setCmroc(OrganismeHelper.getOrganisme(document.getCmroc()));
		}

		if (StringUtils.isBlank(docMongo.getTutelle())) {
			docMongo.setTutelle(document.getCmroc());
		}

		if (StringUtils.isBlank(docMongo.getEddocId())) {
			docMongo.setEddocId(GedeIdHelper.getEddocId(document));
		}

		if (docMongo.getDtCreate() == null) {
			docMongo.setDtCreate(new DateTime());
		}

		if (docMongo.getDtIntegration() == null) {
			docMongo.setDtIntegration(new DateTime());
		}

		return docMongo;
	}

	public static void fillJsonFromDocMongo(Document document) throws CimutDocumentException {
		Json json = new Json();
		json.setId(document.getId());
		json.setOrganisme(document.getCmroc());
		json.setData(DocumentHelper.stringify(document.getDocMongo()));
		document.setJson(json);
	}

	public static Document generateRapportIntegrationDocument(String env, String cmroc, long integrationsTotale, long integrationsOK,
			long integrationsKO, String user, String zipName)
			throws CimutDocumentException {
		Document document = new Document();
		DocumentHelper.setMinDefaultValue(document, env);
		document.setCmroc(cmroc);
		document.setDtcreate(new Date());
		document.setLibelle("Rapport intégration IDOC");
		document.setSite("G");
		document.setOrigine("Intégration IDOC");
		document.setService("IDOC");
		// On set un id avec un uuid aléatoire
		document.setId("rapport_" + UUID.randomUUID() + ".xlsx");
		document.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		document.setTypeDocument(TypeEntite.RAPPORT_INTEGRATION_IDOC.toString());
		DocumentMongo docMongo = document.getDocMongo();
		docMongo.setTypeEntiteRattachement(TypeEntite.RAPPORT_INTEGRATION_IDOC);
		docMongo.setIntegrationsTotale(integrationsTotale);
		docMongo.setIntegrationsOK(integrationsOK);
		docMongo.setIntegrationsKO(integrationsKO);
		docMongo.setUser(user);
		docMongo.setZipName(zipName);
		fillJsonFromDocMongo(document);
		return document;
	}

	/**
	 * Préparation du document pour une intégration unitaire
	 * @param env
	 * @param cmroc
	 * @param user
	 * @return
	 * @throws CimutFileException 
	 */
	public static Document prepareDocIntegUnitaire(String env, String cmroc, String user, UploadFileRequest integRequest,
			String filename, String typeDoc) throws GedeCommonException, CimutFileException {
		Document document = new Document();
		DocumentHelper.setMinDefaultValue(document, env);
		document.setCmroc(cmroc);
		document.setDtcreate(new Date());
		// On set les données en fonction de la requête reçu depuis StarwebWS
		TypeEntite typeEntite  = null;
		if (integRequest.getTypeDest() != null) {
			String typeDest = integRequest.getTypeDest();
			if (typeDest.equals("INSEE")) {
				typeEntite = TypeEntite.PERSONNE;
			} else if (typeDest.equals("GARANTIE")) {
				typeEntite = TypeEntite.GARANTIE;
			} else if (typeDest.equals("ENTREPRISE")) {
				typeEntite = TypeEntite.ENTREPRISE;
			} else if (typeDest.equals("SECTION")) {
				typeEntite = TypeEntite.SECTION;
			} else if (typeDest.equals("PACK")) {
				typeEntite = TypeEntite.PACK;
			} else {
				throw new GedeCommonException("Le type destinataire " + typeDest + " ne correspond à aucune entité connue");
			}
		} else {
			throw new GedeCommonException("Aucun type destinataire présent dans la requête d'intégration unitaire");
		}
		document.getDocMongo().setTypeEntiteRattachement(typeEntite);

		String[] infos = new String[3];
		switch (typeEntite) {
		case PERSONNE:
			String insee = integRequest.getDestInsee();
			if (StringUtils.isBlank(insee)) {
				throw new GedeCommonException("Numéro INSEE obligatoire pour l'entité Personne");
			} else if (insee.matches("^\\d{13}$")) {
				document.getDocMongo().setAssuInsee(insee);
				document.getDocMongo().setRang(integRequest.getRang());
			} else {
				throw new GedeCommonException("L'INSEE de la personne n'est pas au bon format");
			}
			break;
		case ENTREPRISE:
			infos[0] = integRequest.getStruNum();
			infos[1] = integRequest.getStruClasse();
			if (StringUtils.isBlank(infos[0])) {
				throw new GedeCommonException("Numéro interne obligatoire pour l'entité Entreprise");
			}
			document.getDocMongo().setIdEntreprise(StringUtils.join(infos, "|").replaceAll("\\|*$", ""));
			break;
		case SECTION:
			infos[0] = integRequest.getStruNum();
			infos[1] = integRequest.getStruClasse();
			infos[2] = integRequest.getStruSec();
			if (StringUtils.isBlank(infos[0]) || StringUtils.isBlank(infos[2])) {
				throw new GedeCommonException("Numéro interne et code section obligatoires pour l'entité Section");
			}
			document.getDocMongo().setIdEntreprise(StringUtils.join(infos, "|").replaceAll("\\|*$", ""));
			break;
		case PARTENAIRE:
			infos[0] = integRequest.getPartId();
			infos[1] = integRequest.getPartType();
			infos[2] = integRequest.getPartNiv();
			if (StringUtils.isBlank(infos[0]) || StringUtils.isBlank(infos[1]) || StringUtils.isBlank(infos[2])) {
				throw new GedeCommonException("Numéro interne, type partenaire et niveau obligatoires pour l'entité Partenaire");
			}
			document.getDocMongo().setIdProf(StringUtils.join(infos, "|").replaceAll("\\|*$", ""));
			break;
		case PACK:
			String packCodeProduit = integRequest.getProduitComCode();
			String packCodePack = integRequest.getPackCode();
			if (StringUtils.isBlank(packCodeProduit) || StringUtils.isBlank(packCodePack)) {
				throw new GedeCommonException("Code produit et code pack obligatoires pour l'entité Pack");
			}
			document.getDocMongo().setCodeProduit(packCodeProduit);
			document.getDocMongo().setCodePack(packCodePack);
			document.getDocMongo().setShowRule(GlobalVariable.SHOW_RULE_TABLEAU_RMBT);
			break;
		case GARANTIE:
			String codeGarantie = integRequest.getGarantieCode();
			if (StringUtils.isBlank(codeGarantie)) {
				throw new GedeCommonException("Code garantie obligatoire pour l'entité Garantie");
			}
			document.getDocMongo().setCodeGarantie(codeGarantie);
			document.getDocMongo().setShowRule(GlobalVariable.SHOW_RULE_TABLEAU_RMBT);
			break;
		default:
			throw new GedeCommonException("Type d'entité non gérée");
		}

		document.setMimeType(FileHelper.getTypeMime(filename));

		document.setTypeDocument(typeDoc);
		document.getDocMongo().setTypeDocument(typeDoc);
		document.setId(DocumentHelper.generateFinalName(typeDoc, FileHelper.getExtension(filename)));

		String baseLabel;
		if (StringUtils.isBlank(integRequest.getLabel())) {
			baseLabel = filename; // when no custom label provided, use filename instead
		} else {
			baseLabel = integRequest.getLabel();
		}
		document.setLibelle(DocumentHelper.getTruncatedString(baseLabel, "", 50)); // Le libellé doit faire maximum 50 caractères

		document.setSite("G");// O
		document.setOrigine("Intégration IDOC");

		document.setService("IDOC");

		Date dateDebut = null;
		Date dateFin = null;
		if (StringUtils.isNotBlank(integRequest.getDtDebutValidite())) {
			try {
				dateDebut = spfEntry.parse(integRequest.getDtDebutValidite());
				document.setDtDebutValidite(dateDebut);
			} catch(Exception e) {
				LOGGER.error("Problème de lecture de la date de début de validité pour une intégration IDOC unitaire : " + integRequest.getDtDebutValidite(), e);
				throw new GedeCommonException("Format de la date de début de validité invalide. Format attendu : ddMMyyyy.");
			}
		}
		if (StringUtils.isNotBlank(integRequest.getDtFinValidite())) {
			try {
				dateFin = spfEntry.parse(integRequest.getDtFinValidite());
				document.setDtFinValidite(dateFin);
			} catch(Exception e) {
				LOGGER.error("Problème de lecture de la date de fin de validité pour une intégration IDOC unitaire : " + integRequest.getDtDebutValidite(), e);
				throw new GedeCommonException("Format de la date de fin de validité invalide. Format attendu : ddMMyyyy.");
			}
		}
		if (dateDebut != null && dateFin != null && dateDebut.after(dateFin)) {
			throw new GedeCommonException("Période de validité incohérente : date de fin inférieure à la date de début");
		}
		return document;
	}

	/** Fournit un {@link ObjectMapper} instancié et paramétré */
	public static ObjectMapper getObjectMapper() {
		return new ObjectMapper().configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
	}

	public static boolean checkGarantieExistViaStarwebDao(String cmroc, String envir, String codeGarantie) throws GedeException {
		Map<String, String> headerParam = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		boolean result = false;
		try {
			Object garantieDto = RestClientUtils.executeGetRequest(GlobalVariable.getStarwebDaoWsUrl() + "/garantie/" + codeGarantie, headerParam,
					Object.class);
			if (garantieDto != null) {
				result = true;
			}
		} catch (IOException e) {
			throw new GedeCommonException("Erreur lors de l'appel à starwebDao pour le contrôle de l'existence d'une garantie", e);
		}
		return result;
	}

	public static boolean checkPackExistViaStarwebDao(String cmroc, String envir, String codeProduitCommercial, String codePack) throws GedeException {
		Map<String, String> headerParam = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		boolean result = false;
		try {
			Object packDto = RestClientUtils.executeGetRequest(GlobalVariable.getStarwebDaoWsUrl() + "/pack/"
					+ codeProduitCommercial + "/" + codePack, headerParam, Object.class);
			if (packDto != null) {
				result = true;
			}
		} catch (IOException e) {
			throw new GedeCommonException("Erreur lors de l'appel à starwebDao pour le contrôle de l'existence d'un pack", e);
		}
		return result;
	}

	/**
	 * Convertit un liste de document editique en Map<idstar_tsstar, eddoc> pour facilité la comparaison avec un liste de documents starweb
	 * @param _eddocs
	 * @return
	 */
	public static Map<String, Document> convertListEddocToMap (List<Document> _eddocs) {
		Map<String, Document> eddocs = new HashMap<String, Document>();
		for (Document doc : _eddocs) {
			eddocs.put(doc.getEddocId(), doc);
		}
		return eddocs;
	}

	public static void checkValid(Document docDto) throws CimutDocumentException, CimutFileException, CimutConfException, GedeCommonException {
		// a brancher une fois que les sudefilemanger et autres sont passer sur l'intégration générique
		//		DocumentHelper.validate(docDto);
	}

	// TODO
	public static void checkValid(DocumentMongo docMongo) {
		// check date et autre
	}

	// https://stackoverflow.com/questions/119328/how-do-i-truncate-a-java-string-to-fit-in-a-given-number-of-bytes-once-utf-8-en
	public static String truncateWhenUTF8(String s, int maxBytes) {
		int b = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			// ranges from http://en.wikipedia.org/wiki/UTF-8
			int skip = 0;
			int more;
			if (c <= 0x007f) {
				more = 1;
			}
			else if (c <= 0x07FF) {
				more = 2;
			} else if (c <= 0xd7ff) {
				more = 3;
			} else if (c <= 0xDFFF) {
				// surrogate area, consume next char as well
				more = 4;
				skip = 1;
			} else {
				more = 3;
			}

			if (b + more > maxBytes) {
				return s.substring(0, i);
			}
			b += more;
			i += skip;
		}
		return s;
	}

	public static String getTruncatedString(String value, String defaultValue, int numBytes) {
		if (StringUtils.isBlank(defaultValue)) {
			defaultValue = "";
		}

		String result;
		if (StringUtils.isBlank(value)) {
			result = defaultValue; // when no custom label provided, use filename instead
		} else {
			result = value;
		}

		if (result.getBytes().length <= numBytes) {
			return result;
		}

		return truncateWhenUTF8(result, numBytes);
	}
}
