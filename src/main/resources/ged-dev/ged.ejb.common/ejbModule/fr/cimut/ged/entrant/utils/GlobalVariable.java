package fr.cimut.ged.entrant.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.util.GlobalProperties;

public class GlobalVariable {

	public static final Logger logger = Logger.getLogger(GlobalVariable.class);

	/** Préfixe accolé au nom de l'environnement pour obtenir le nom de la base de données */
	public static final String NOM_BASE_PREFIXE = "GEDE_";

	public static final String ATTR_DATABASE = "DATABASE";
	public static final String ATTR_JSON = "JSON";
	public static final String ATTR_ATTRIBUTS = "ATTRIBUTS";

	public static final String STATUS_ERROR = "En erreur";
	public static final String STATUS_TRAITE = "Traité";
	public static final String STATUS_A_TRAITER = "A traiter";
	public static final String STATUS_NOAFFECT = "Non affecté";

	public static final String ATTR_ASSU_INSEE = "NIR_ASSURE";
	public static final String ATTR_ASSU_RANG = "NIR_RANG";
	public static final String ATTR_GESTIONNAIRE = "GESTIONNAIRE";
	public static final String ATTR_DTINTEGRATION = "DATE_INTEGRATION";
	public static final String ATTR_NOM_ENTREPRISE = "NOM_ENTREPRISE";
	public static final String ATTR_NUM_ENTREPRISE = "NUMERO_ENTREPRISE";
	public static final String ATTR_DEPARTEMENT = "DEPARTEMENT";
	public static final String ATTR_REGION = "REGION";
	public static final String ATTR_PRIORITE = "PRIORITE";
	public static final String ATTR_DESTINATAIRE = "MAIL_DESTINATAIRE";
	public static final String ATTR_EXPEDITEUR = "MAIL_EXPEDITEUR";
	public static final String ATTR_SUBJECT = "MAIL_SUBJECT";
	public static final String ATTR_MAIL_FOLDER = "MAIL_DOSSIER";
	public static final String ATTR_ERROR_EDDM = "ERROR_EDDM";
	public static final String ATTR_ERROR_DA = "ERROR_DA";

	public static final String ATTR_NOM_DE_FAMILLE = "NOM_DE_FAMILLE";
	public static final String ATTR_COMMENTAIRES = "COMMENTAIRE";
	public static final String ATTR_PRENOM = "PRENOM";

	public static final String IMPORT_USER = "_import_ged";

	public static final String ATTR_PAGESIZE = "PAGESIZE";
	public static final String ATTR_PAGENUM = "PAGENUM";
	public static final String ATTR_SORTING = "SORTING";
	public static final int DEFAULT_PAGESIZE = 150;
	public static final int DEFAULT_PAGENUM = 0;
	public static final String DEFAULT_SORTING = "desc";
	public static final String KEY_CHIFFRAGE = "chiffrage";
	public static final String DOCUMENT_TAG_PATTERN = "DOCUMENT[0-9]{1,2}";
	public static final String TYPE_DOCUMENT_TAG_PATTERN = "TYPE_DOCUMENT[0-9]{1,2}";

	//attributs d une recherche

	public static final String ATTR_DATE_CREATION_D = "DATE_CREATION_D";
	public static final String ATTR_DATE_CREATION_F = "DATE_CREATION_F";

	//attibuts stats
	public static final String ATTR_STATS_TYPE = "STATS_TYPE";
	public static final String ATTR_STATS_FILTER = "STATS_FILTER";
	public static final String ATTR_STATS_FILTER_VAL = "STATS_FILTER_VAL";
	public static final String VAR_TYPE_COUNT = "statsTypeCount";
	public static final String VAR_ALL_STATUS = "allStatus";

	public static final String ATTR_TYPE_COURRIER = "TYPE_COURRIER";
	public static final String ATTR_ORGANISME = "ID_ORGANISME";
	public static final String ATTR_ID_DOC = "ID_DOC";
	public static final String ATTR_DATE_CREATION = "DATE_CREATION";
	public static final String ATTR_NUM_ADHERENT = "NUMERO_ADHERENT";
	public static final String ATTR_ID_ORGANISME = "ID_ORGANISME";
	public static final String ATTR_ID_ENTREPRISE = "ID_ENTREPRISE";
	public static final String ATTR_ID_SYSENTREPRISE = "ID_SYSENTREPRISE";
	public static final String ATTR_CLASSE_ENTRPRISE = "CLASSE_ENTREPRISE";
	public static final String ATTR_TYPE_DOSSIER = "TYPE_DOSSIER";
	public static final String ATTR_TYPE_DOCUMENT = "TYPE_DOCUMENT";
	public static final String ATTR_ID_EXT_DOC = "IDENTIFIANT_EXTERNE";
	public static final String ATTR_REFERENCE_COURRIER = "REFERENCE_COURRIER";
	public static final String ATTR_LIBELLE_DOC = "LIBELLE_DOCUMENT";
	public static final String DEFAULT_ATTR_TYPE_DOCUMENT = "Pièce jointe SUDE";
	public static final String ATTR_DOCUMENT = "DOCUMENT";
	public static final String ATTR_DTCREATE = "DATE_CREATION";
	public static final String ATTR_TYPE_ENTITE_RATTACHEMENT = "TYPE_ENTITE_RATTACHEMENT";
	public static final String ATTR_TYPEDOC = "TYPE_COURRIER";
	public static final String ATTR_MAILED = "MAILED";
	public static final String ATTR_DIRECTION = "DIRECTION";
	public static final String ATTR_DA_ID = "ID_DA";
	public static final String ATTR_EDDOC_ID = "ID_EDDOC";
	public static final String ATTR_EDDOC_IDS = "ID_EDDOCS";
	public static final String ATTR_VILLE = "VILLE";
	public static final String ATTR_STATUS = "STATUS";
	public static final String ATTR_REGIME = "REGIME";
	public static final String ATTR_SITE_SCAN = "SITE_SCAN";
	public static final String ATTR_CODE_POSTAL = "CODE_POSTAL";
	public static final String ATTR_SITESCAN = "";
	public static final String ATTR_TUTELLE = "TUTELLE";
	public static final String ATTR_CANAL = "CANAL";
	public static final String ATTR_ID_PROF = "ID_PROF";
	public static final String ATTR_MEDIA = "MEDIA";
	public static final String ATTR_ID = "ID_DOC";
	public static final String ATTR_SHOW_RULE = "SHOW_RULE";
	public static final String TYPE_ENTREPRISE = "Entreprise";
	public static final String TYPE_SECTION = "Section";
	public static final String TYPE_PARTENAIRE = "Partenaire";
	public static final String TYPE_PERSONNE = "Personne";
	public static final String TYPE_PACK = "Pack";
	public static final String TYPE_GARANTIE = "Garantie";
	public static final String TYPE_SUDE = "Sude";
	public static final String TYPE_CONTRAT = "Contrat";
	public static final String TYPE_INCONNU = "Inconnu";
	public static final String TYPE_COURTIER = "Courtier";
	public static final String TYPE_RESEAUCOURTIERS = "ReseauCourtiers";
	public static final String TYPE_INTERNE = "Interne";
	public static final String TYPE_RAPPORT_INTEGRATION_IDOC = "RapportIntegrationIdoc";
	public static final String DATE_NAISSANCE_PATIENT = "DATE_NAISSANCE_PATIENT";
	public static final String SHOW_RULE_TABLEAU_RMBT = "ecart_umc_107_tmpSolution";

	public static final List<String> QUERY_SEARCH_ATTR = Arrays.asList(ATTR_STATUS, ATTR_TYPE_COURRIER, ATTR_TYPE_DOSSIER, ATTR_TUTELLE, ATTR_ID_DOC);

	public static final List<String> LISTE_CRITERES = Arrays.asList(ATTR_TYPEDOC, ATTR_TYPE_DOSSIER, ATTR_DIRECTION, ATTR_TUTELLE, ATTR_VILLE,
			ATTR_STATUS, ATTR_SITE_SCAN, ATTR_CODE_POSTAL, ATTR_DESTINATAIRE, ATTR_EXPEDITEUR, ATTR_MAIL_FOLDER);

	public static final String RULE_ID = "ruleId";
	public static final String RULE_DATE = "ruleDate";

	public static final List<String> OBLIGATOIRE_DB = Arrays.asList(ATTR_TYPE_DOSSIER, ATTR_ID, ATTR_TUTELLE);
	public static final List<String> OPTIONAL_DB = Arrays.asList(ATTR_PRIORITE);
	public static final List<String> DATES = Arrays.asList(ATTR_DTINTEGRATION, ATTR_DTCREATE);
	public static final List<String> HASH_KEYS = Arrays.asList(ATTR_JSON, ATTR_DATABASE, ATTR_ATTRIBUTS);
	public static final List<String> OBLIGATOIRE_JSON = Arrays.asList(ATTR_TYPEDOC, ATTR_ID, ATTR_ID_ORGANISME, ATTR_TYPE_DOSSIER);
	public static final List<String> OBLIGATOIRE_PERSONNE = Arrays.asList(ATTR_NUM_ADHERENT);
	public static final List<String> OBLIGATOIRE_PARTENAIRE = Arrays.asList(ATTR_ID_PROF);
	public static final List<String> OBLIGATOIRE_ENTREPRISE = Arrays.asList(ATTR_ID_ENTREPRISE);
	public static final int MAX_SUDE_DOCUMENT_PER_NOTE = 20;
	public static final String CANAL_MEL = "MEL";
	public static final String CANAL_LER = "LER";
	public static final String ACCUSE_RECEPTION = "ACCUSE_RECEPTION";
	public static final String ATTR_IDENTIFIANT_PACK = "IDENTIFIANT_PACK";
	public static final String ATTR_CODE_GARANTIE = "CODE_GARANTIE";
	public static final String ATTR_CODE_PRODUIT = "CODE_PRODUIT";
	public static final String ATTR_UPLOAD_USER = "UPLOAD_USER";
	public static final String ATTR_INTEGRATIONS_TOT = "INTEGRATIONS_TOT";
	public static final String ATTR_INTEGRATIONS_OK = "INTEGRATIONS_OK";
	public static final String ATTR_INTEGRATIONS_KO = "INTEGRATIONS_KO";
	public static final String ATTR_USER_INTEGRATION = "USER_INTEGRATION";
	public static final String ATTR_ZIP_NAME = "ZIP_NAME";

	private static final String keyVariableMaster = "fr.cimut.ged.entrante.master.scheduled.task";

	private GlobalVariable() {

	}

	public static List<String> getListCmrocs() throws CimutConfException {
		List<String> list = new ArrayList<String>();

		//recuperation dans les preferences application de la liste des CMROCs
		for (String str : GlobalVariable.getCmrocs().split(";")) {
			list.add(str);
		}
		//retourne la liste
		return list;
	}

	public static String getRsServersHost() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.mongo.rs.servers.host");
	}

	public static String getCmrocFilePath() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.cmroc.file.path") + File.separator;
	}

	private static String getHomePath(String environnement) throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.home.dir") + File.separator + environnement + File.separator;
	}

	public static String getDestinationPath() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.destination.dir") + File.separator;
	}

	public static String getIntegrationPath(String environnement) throws CimutConfException {
		return getHomePath(environnement).concat(getGenericConf("fr.cimut.ged.entrante.integration.dir")) + File.separator;
	}

	public static String getTranscodagePath(String environnement) throws CimutConfException {
		return getHomePath(environnement).concat(getGenericConf("fr.cimut.ged.entrante.transcodage.dir")) + File.separator;
	}

	public static String getErrorPath(String environnement) throws CimutConfException {
		return getIntegrationPath(environnement).concat(getGenericConf("fr.cimut.ged.entrante.error.dir")) + File.separator;
	}

	public static String getIndexationPath(String environnement) throws CimutConfException {
		return getHomePath(environnement).concat(getGenericConf("fr.cimut.ged.entrante.indexation.dir")).concat(File.separator);
	}

	public static String getIndexationPathImportSortie(String environnement) throws CimutConfException {
		return getIndexationPath(environnement).concat("errors").concat(File.separator).concat("add").concat(File.separator);
	}

	public static String getIndexationPathUpdateSortie(String environnement) throws CimutConfException {
		return getIndexationPath(environnement).concat("errors").concat(File.separator).concat("update").concat(File.separator);
	}

	public static String getIndexationPathDeleteSortie(String environnement) throws CimutConfException {
		return getIndexationPath(environnement).concat("errors").concat(File.separator).concat("delete").concat(File.separator);
	}

	public static String getIndexationPathImportEntree(String environnement) throws CimutConfException {
		return getIndexationPath(environnement).concat("add").concat(File.separator);
	}

	public static String getIndexationPathUpdateEntree(String environnement) throws CimutConfException {
		return getIndexationPath(environnement).concat("update").concat(File.separator);
	}

	public static String getIndexationPathDeleteEntree(String environnement) throws CimutConfException {
		return getIndexationPath(environnement).concat("delete").concat(File.separator);
	}

	public static String getNomServeur() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.nom_serveur");
	}

	public static String getPortServeur() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.port_serveur");
	}

	public static String getUser() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.user");
	}

	public static String getPassword() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.password");
	}

	public static String getDBCollection() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.nom_database_collection");
	}

	public static String getUserAdmin() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.admin.user");
	}

	public static String getPasswordAdmin() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.admin.password");
	}

	public static String getCmrocs() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.indexation.cmrocs");
	}

	public static String getIntegrationReportPath() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.integration.report.dir") + File.separator;
	}

	/**
	 * @return la taille max des fichiers intégrable par IDOC en intégration massive. Exprimée en octet. 150ko valeur
	 *         par défaut
	 */
	public static int getTailleMaxFichierIdoc() {
		try {
			return Integer.parseInt(getGenericConf("fr.cimut.ged.entrante.taille.max.idoc"));
		} catch (Exception e) {
			logger.warn("Propriété fr.cimut.ged.entrante.taille.max.idoc non définie, on utilise la valeur par défaut de 150ko");
			return 153600;
		}
	}

	private static String getGenericConf(String var) throws CimutConfException {
		String string = System.getProperty(var);
		if (string == null) {
			throw new CimutConfException("la variable systeme " + var + " n'est pas defini");
		}
		return string;
	}

	public static String getMulticanalUrl() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.multicanal.url");
	}

	public static String getEnvironnement() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.environnement");
	}

	public static String getUrlP360() throws CimutConfException {
		return getGenericConf("fr.cimut.ged.entrante.p360.url");
	}

	public static String getSelligentSudeLibNote() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.selligent.note.lib";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return message;
	}

	public static String getStarwebDaoWsUrl() throws CimutConfException {
		return getGlobalPropertieAndCheckNotNull("fr.cimut.starwebdao.url");
	}

	public static String getAuthServerUrl() throws CimutConfException {
		return getGlobalPropertieAndCheckNotNull("fr.cimut.authserver.url");
	}

	public static long getZipMaxSize() {
		Long maxFileSize = 104857600l; // 100 mo * 1024 * 1024 octets
		try {
			maxFileSize = Long.parseLong(getGenericConf("fr.cimut.ged.entrante.zip.max.size"));
		} catch (CimutConfException e) {
			logger.warn("la variable systeme fr.cimut.ged.entrante.zip.max.size n'est pas defini, on utilise la valeur 100 Mo par défaut");
		}
		return maxFileSize;
	}

	public static long getZipMaxFileCount() {
		Long maxFileCount = 200l;
		try {
			maxFileCount = Long.parseLong(getGenericConf("fr.cimut.ged.entrante.zip.max.file.count"));
		} catch (CimutConfException e) {
			logger.warn("la variable systeme fr.cimut.ged.entrante.zip.max.file.count n'est pas defini, on utilise la valeur 200 par défaut");
		}
		return maxFileCount;
	}

	public static long getZipMaxIndexLines() {
		Long maxIndexLines = 500l;
		try {
			maxIndexLines = Long.parseLong(getGenericConf("fr.cimut.ged.entrante.zip.max.index.lines"));
		} catch (CimutConfException e) {
			logger.warn("la variable systeme fr.cimut.ged.entrante.zip.max.index.lines n'est pas defini, on utilise la valeur 500 par défaut");
		}
		return maxIndexLines;
	}

	private static String getGlobalPropertieAndCheckNotNull(String varName) throws CimutConfException {
		String message = GlobalProperties.getGlobalProperty(varName);
		if (null == message) {
			throw new CimutConfException("la variable systeme " + varName + " n'est pas defini");
		}
		return message;
	}

	public static String getCourtageMailHTMLMessage() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.multicanal.courtage.mail.html";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return message;
	}

	public static int getNbMaxTryMailIntegration() {
		int maxTry = 12;
		String varName = "fr.cimut.ged.entrante.mail.nbMaxTry";
		String message = GlobalProperties.getGlobalProperty(varName);
		try {
			maxTry = Integer.parseInt(message);
		} catch (Exception e) {
			logger.error("la variable systeme " + varName + " n'est pas defini");
		}
		return maxTry;
	}

	public static int getNbMinutesTryInterval() {
		int maxTry = 15;
		String varName = "fr.cimut.ged.entrante.mail.nbMinutesTryInterval";
		String message = GlobalProperties.getGlobalProperty(varName);
		try {
			maxTry = Integer.parseInt(message);
		} catch (Exception e) {
			logger.error("la variable systeme " + varName + " n'est pas defini");
		}
		return maxTry;
	}

	public static String getCourtageMailMessage() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.multicanal.courtage.mail.text";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return message;
	}

	public static String getHeaderMailMessage() {
		String varName = "fr.cimut.ged.entrante.mail.rapport.erreur";
		String message = GlobalProperties.getGlobalProperty(varName);
		if (null == message) {
			logger.warn("la variable systeme " + varName + " n'est pas defini");
			return "";
		}
		return message;
	}

	public static Long getMailPoolerTimeout() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.mail.pooler.timeout";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return Long.valueOf(message);
	}

	public static Long getMailPoolerMaxEmail() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.mail.pooler.maxmail";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return Long.valueOf(message);
	}

	public static boolean isMailContenuPjEnabled(String cmroc) {
		String varName = "fr.cimut.ged.entrante.mail.contenuPJ." + cmroc;
		try {
			String message = getGlobalPropertieAndCheckNotNull(varName);
			return Boolean.valueOf(message).booleanValue();
		} catch (CimutConfException e) {
			return true;
		}
	}

	/**
	 * verification autorisation d'integerer des documents dont le code type n'est pas connu
	 * 
	 * @return
	 */
	public static boolean allowTypeDocumentInconnu() {
		return "true".equals(GlobalProperties.getGlobalProperty("fr.cimut.ged.entrante.allow.type.inconnu"));
	}

	public static Long getMailPoolerInterval() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.mail.pooler.interval";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return Long.valueOf(message);
	}

	public static String getCourtageMailSujetMessage() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.multicanal.courtage.mail.sujet";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return message;
	}

	public static String getMailEntrantSujetMessage() throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.multicanal.mailEntrant.mail.sujet";
		String message = getGlobalPropertieAndCheckNotNull(varName);
		return message;
	}

	public static String getAccuseReceptionTextMailMessage(String cmroc) throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.multicanal.mailEntrant.accuseRecept.text" + cmroc;
		String fileName = getGlobalPropertieAndCheckNotNull(varName);
		String output = "";
		try {
			output = getFileContent(fileName);
		} catch (Exception e) {
			throw new CimutConfException(e.getMessage());
		}
		return output;
	}

	private static String getFileContent(String filename) throws Exception {

		File file = new File(filename);
		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;
		BufferedInputStream bis = null;

		try {
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
			BufferedReader d = new BufferedReader(new InputStreamReader(bis));
			String ligne = null;
			while ((ligne = d.readLine()) != null) {
				sb.append(ligne);
			}
		} catch (FileNotFoundException e) {
			throw new Exception("Fichier " + file + " introuvable");
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();
	}

	public static String getAccuseReceptionHtmlMailMessage(String cmroc) throws CimutConfException {
		String varName = "fr.cimut.ged.entrante.multicanal.mailEntrant.accuseRecept.html." + cmroc;
		String fileName = getGlobalPropertieAndCheckNotNull(varName);
		String output = "";
		try {
			output = getFileContent(fileName);
		} catch (Exception e) {
			throw new CimutConfException(e.getMessage());
		}
		return output;
	}

	public void printvariable() {
		try {
			String environnement = "ENVIR";

			logger.info("GlobalVariable.getIndexationPath() : " + GlobalVariable.getIndexationPath(environnement));
			logger.info("GlobalVariable.getIndexationPathImportSortie() : " + GlobalVariable.getIndexationPathImportSortie(environnement));
			logger.info("GlobalVariable.getIndexationPathUpdateSortie() : " + GlobalVariable.getIndexationPathUpdateSortie(environnement));
			logger.info("GlobalVariable.getIndexationPathDeleteSortie() : " + GlobalVariable.getIndexationPathDeleteSortie(environnement));
			logger.info("GlobalVariable.getIndexationPathImportEntree() : " + GlobalVariable.getIndexationPathImportEntree(environnement));
			logger.info("GlobalVariable.getIndexationPathUpdateEntree() : " + GlobalVariable.getIndexationPathUpdateEntree(environnement));
			logger.info("GlobalVariable.getIndexationPathDeleteEntree() : " + GlobalVariable.getIndexationPathDeleteEntree(environnement));
			logger.info("GlobalVariable.getNomServeur() : " + GlobalVariable.getNomServeur());
			logger.info("GlobalVariable.getPortServeur() : " + GlobalVariable.getPortServeur());
			logger.info("GlobalVariable.getUser() : " + GlobalVariable.getUser());
			logger.info("GlobalVariable.getPassword() : " + GlobalVariable.getPassword());
			logger.info("GlobalVariable.getDBCollection() : " + GlobalVariable.getDBCollection());
			logger.info("GlobalVariable.getUserAdmin() : " + GlobalVariable.getUserAdmin());
			logger.info("GlobalVariable.getMailPoolerMaxEmail() : " + GlobalVariable.getMailPoolerMaxEmail());
			logger.info("GlobalVariable.getMailPoolerInterval() : " + GlobalVariable.getMailPoolerInterval());
			logger.info("GlobalVariable.getMailPoolerTimeout() : " + GlobalVariable.getMailPoolerTimeout());
			logger.info("GlobalVariable.getPasswordAdmin() : " + GlobalVariable.getPasswordAdmin());
			logger.info("GlobalVariable.getCourtageMailSujetMessage() : " + GlobalVariable.getCourtageMailSujetMessage());
			logger.info("GlobalVariable.getCourtageMailMessage() : " + GlobalVariable.getCourtageMailMessage());
			logger.info("GlobalVariable.getCourtageMailHTMLMessage() : " + GlobalVariable.getCourtageMailHTMLMessage());
		} catch (CimutConfException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * méthode permettant de ne savoir si l'instance actuelle porte la responsabilité des tâches schédulées
	 * 
	 * @return
	 * @throws CimutConfException
	 */
	public static boolean checkIfMasterForScheduledTask() {
		// plusieurs solution: conf unix, conf system properties, conf en base mongo		
		// renvoi true par défaut, si la propriété n'est pas définie
		String value = System.getProperty(keyVariableMaster);
		if (Boolean.TRUE.toString().equalsIgnoreCase(value)) {
			return true;
		} else if (Boolean.FALSE.toString().equalsIgnoreCase(value)) {
			return false;
		} else {
			logger.fatal("la variable systeme " + keyVariableMaster + " doit prendre la valeur false ou true, et pas [" + value + "]");
			return true;
		}
	}

	public static Boolean getPartialFecthForAttachement() {
		String value = GlobalProperties.getGlobalProperty("fr.cimut.partial.fetch");
		if (Boolean.TRUE.toString().equals(value)) {
			return true;
		} else if (Boolean.FALSE.toString().equals(value)) {
			return false;
		}
		return null;
	}

	public static String getConfPath() {
		String confPath = "";

		String path = System.getProperty("fr.cimut.util.path.properties");
		if (path != null) {
			confPath = path;
		} else {
			path = System.getProperty("jboss.server.config.dir");
			if (path != null) {
				confPath = path;
			}
		}
		return confPath + "/";
	}

	/**
	 * retourne la clé d'authentification par cmroc variabilisation effective depuis aout 2018 en develppement
	 * 
	 * @return
	 */
	public static String getP360KeyForCmroc(String cmroc) {
		String varName = "fr.cimut.ged.entrante.p360.key." + cmroc;
		String keyForCmroc = GlobalProperties.getGlobalProperty(varName);
		if (StringUtils.isBlank(keyForCmroc)) {
			logger.warn("la clé n'est pas initialisée dans la configuration : " + varName);
			if ("9970".equals(cmroc)) {
				// on assure la retro compat si jamais la clé n'a pas été déposée sur le serveur pour 9970... car avant c'etait en dur dans le code		
				// c'est juste pour éviter les mauvaises surprise si la conf ne monte pas en même temps que ce dev
				keyForCmroc = "32b9ee08-78c7-11e5-8bcf-feff819cdc9f";
			}
		}
		return keyForCmroc;

	}

	public static String getIdocFileSystemInputDirectory() {
		return GlobalProperties.getGlobalProperty("fr.cimut.ged.entrante.idoc.file.system.input.directory");
	}

	public static Integer getIdocFileSystemInputChunkSize() {
		return Integer.parseInt(GlobalProperties.getGlobalProperty("fr.cimut.ged.entrante.idoc.file.system.chunk.size"));
	}

	public static Integer getIdocFileSystemInputCoolDownTime() {
		return Integer.parseInt(GlobalProperties.getGlobalProperty("fr.cimut.ged.entrante.idoc.file.system.cooldown.time"));
	}

	public static List<String> getListCmrocDematDocumentNameByTypedoc () {
		String cmrocsProps = GlobalProperties.getGlobalProperty("fr.cimut.ged.entrante.demat.cmroc.docname.typedoc");
		return toListOfData(cmrocsProps, ",");
	}

	/**
	 * Conversion safe d'une liste de données contenues dans une String en List<String>,
	 * avec suppression des espaces inutiles.
	 *
	 * @param datas
	 * @param dataSeperator
	 * @return Une List des données ou une List vide si la donnée en entrée est vide
	 */
	private static List<String> toListOfData (String datas, String dataSeperator) {
		List<String> safeData = new ArrayList<String>();
		if (StringUtils.isNotBlank(datas)) {
			List<String> listData = Arrays.asList(datas.split(dataSeperator));
			for (String data : listData) {
				safeData.add(data.trim());
			}
		}
		return safeData;
	}


}
