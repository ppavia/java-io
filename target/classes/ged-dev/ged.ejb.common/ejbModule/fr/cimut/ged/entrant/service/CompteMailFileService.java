package fr.cimut.ged.entrant.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.utils.GlobalVariable;

/**
 * Service de manipulation de fichiers XML et properties dans le cadre de l'administration des mails
 */
@Stateless(mappedName = "CompteMailFile")
public class CompteMailFileService {

	/** Format de date pour le suffixe des noms des fichiers de sauvegarde de compte mail */
	private static final SimpleDateFormat BACKUP_FILE_DATE_FORMAT = new SimpleDateFormat("yyMMddHHmmss");

	/**
	 * Détermine si un fichier est un fichier XML de Compte mail
	 * 
	 * @param fileName
	 *            le nom du fichier à tester
	 * @return true si c'est un fichier XML de Compte mail
	 */
	public boolean isACompteMailFileName(String fileName) {
		return fileName.contains("@") && fileName.endsWith(".xml");
	}

	/**
	 * Construit le nom de fichier XML du Compte mail à partir de son adresse email
	 * 
	 * @param email
	 *            l'adresse email d'un compte mail
	 * @param optionalSuffix
	 *            suffixe du fichier pour une version spéciale
	 * @return le nom de fichier XML du Compte mail
	 */
	public String buildCompteMailFileName(String email, String optionalSuffix) {
		return email + (optionalSuffix == null ? "" : optionalSuffix) + ".xml";
	}

	/**
	 * Extrait l'adresse email contenue dans un fichier XML de Compte mail
	 * 
	 * @param fileName
	 *            le nom du fichier XML de Compte mail
	 * @return l'email du Compte mail
	 */
	private String extractEmailFromCompteMailFileName(String fileName, String optionalSuffix) {
		return fileName.replace(optionalSuffix, "").replace(".xml", "");
	}

	/**
	 * Récupère tous les fichiers compte mail dans le répertoire de configuration
	 * 
	 * @return la liste des noms de fichier
	 */
	private List<String> getCompteMailFilesNames() {
		List<String> filesNames = new ArrayList<String>();
		File[] files = new File(GlobalVariable.getConfPath()).listFiles();
		for (File file : files) {
			if (isACompteMailFileName(file.getName())) {
				filesNames.add(file.getName());
			}
		}
		return filesNames;
	}

	/**
	 * Détermine si le fichier du compte mail existe parmi les fichiers du répertoire de conf
	 * 
	 * @param emailToFind
	 *            email du compte mail à trouver
	 * @return true si l'email a été trouvé
	 * @throws CimutConfException
	 */
	public boolean isCompteMailFileExist(String emailToFind) throws CimutConfException {
		String fileNameToFind = buildCompteMailFileName(emailToFind, null);
		List<String> currentFilesNames = getCompteMailFilesNames();
		for (String fileName : currentFilesNames) {
			if (fileName.equals(fileNameToFind)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Récupère une instance de compte mail à partir du fichier XML
	 * 
	 * @param fileName
	 *            nom du fichier du compte mail
	 * @param email
	 *            email du compte mail
	 * @return le compte mail récupéré
	 * @throws CimutConfException
	 */
	private CompteMail getCompteMailFromFile(String email, String fileName) throws CimutConfException {
		File file = new File(GlobalVariable.getConfPath() + fileName);
		if (!file.exists()) {
			throw new CimutConfException("Impossible de lire le fichier suivant : " + file.getAbsolutePath());
		}
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(CompteMail.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			CompteMail compte = (CompteMail) jaxbUnmarshaller.unmarshal(file);
			if (compte == null) {
				throw new CimutConfException("Erreur lors de la récupération du compte mail à partir du fichier : " + fileName);
			} else {
				compte.setEmail(email);
				return compte;
			}
		} catch (JAXBException e) {
			throw new CimutConfException("format du compte dans le fichier " + fileName + " incorrect", e);
		}
	}

	/**
	 * Récupère une instance de compte mail à partir du fichier XML identifié par l'email
	 * 
	 * @param email
	 *            email du compte mail
	 * @return le compte mail récupéré
	 * @throws CimutConfException
	 */
	public CompteMail getCompteMailFromFileByEmail(String email) throws CimutConfException {
		return getCompteMailFromFile(email, buildCompteMailFileName(email, null));
	}

	/**
	 * Récupère les instances de comptes mail à partir de tous les fichiers XML compte mail du répertoire de
	 * configuration
	 * 
	 * @return une liste de comptes mails
	 * @throws CimutConfException
	 */
	public List<CompteMail> getAllComptesMailFromFiles() throws CimutConfException {
		List<String> filesNames = getCompteMailFilesNames();
		List<CompteMail> compteMailList = new ArrayList<CompteMail>();
		for (String fileName : filesNames) {
			compteMailList.add(getCompteMailFromFile(extractEmailFromCompteMailFileName(fileName, ""), fileName));
		}
		return compteMailList;
	}

	/**
	 * Récupère les instances de comptes mail pour tous les fichiers XML compte mail d'une mutuelle
	 * 
	 * @param cmroc
	 *            l'identifiant de mutuelle
	 * @return une liste de comptes mails
	 * @throws CimutConfException
	 */
	public List<CompteMail> getComptesMailFromFiles(String cmroc) throws CimutConfException {
		List<CompteMail> compteMailList = new ArrayList<CompteMail>();
		List<CompteMail> allCompteMailList = getAllComptesMailFromFiles();
		for (CompteMail compteMail : allCompteMailList) {
			if (compteMail.getCmroc().equals(cmroc)) {
				compteMailList.add(compteMail);
			}
		}
		return compteMailList;
	}

	/**
	 * Création d'un fichier XML de compte mail
	 * 
	 * @param newCompteMail
	 *            nouveau compte mail
	 * @param path
	 *            chemin du fichier à créer
	 * @param fileToCreateName
	 *            nom du fichier à créer
	 * @throws CimutConfException
	 */
	private void createCompteMailFile(CompteMail newCompteMail, String path, String fileToCreateName) throws CimutConfException {
		try {
			File file = new File(path + fileToCreateName);
			OutputStream os = new FileOutputStream(file);
			javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(newCompteMail.getClass());
			javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(newCompteMail, os);
		} catch (Exception e) {
			throw new CimutConfException("Erreur lors de la creation du compte mail " + newCompteMail.getEmail() + " : " + e.getMessage());
		}
	}

	/**
	 * Création d'un fichier XML de compte mail
	 * 
	 * @param newCompteMail
	 *            nouveau compte mail
	 * @throws CimutConfException
	 */
	public void createCompteMailFile(CompteMail newCompteMail) throws CimutConfException {
		createCompteMailFile(newCompteMail, GlobalVariable.getConfPath(), buildCompteMailFileName(newCompteMail.getEmail(), null));
	}

	/**
	 * Suppression d'un fichier XML de compte mail
	 * 
	 * @param email
	 *            email du CompteMail du fichier à supprimer
	 * @throws CimutConfException
	 */

	public void deleteCompteMailFile(String email) throws CimutConfException {
		String fileToDeleteName = buildCompteMailFileName(email, null);
		File fileToDelete = new File(GlobalVariable.getConfPath() + fileToDeleteName);
		if (!fileToDelete.delete()) {
			throw new CimutConfException("Impossible de supprimer le fichier " + fileToDeleteName);
		}
	}

	/**
	 * Effectue une copie de sauvegarde d'un compte mail dans un fichier XML dans le répertoire "backup"
	 * 
	 * @param compteMailToUpdate
	 *            ancienne version du compte mail à mettre à jour
	 * @throws CimutConfException
	 */
	public void saveCompteMailFile(CompteMail compteMailToSave) throws CimutConfException {
		File backupDirectory = new File(GlobalVariable.getConfPath() + "\\CompteMailBackup\\");
		if (!backupDirectory.exists()) {
			if (!backupDirectory.mkdirs()) {
				throw new CimutConfException("Impossible de créer le répertoire de sauvegarde : " + backupDirectory.getName());
			}
		}
		String backupFileName = buildCompteMailFileName(compteMailToSave.getEmail(), "_BACKUP_" + BACKUP_FILE_DATE_FORMAT.format(new Date()));
		createCompteMailFile(compteMailToSave, backupDirectory.getPath() + "/", backupFileName);
	}
}