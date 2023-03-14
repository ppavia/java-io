/**
 * @author gyclon
 */

package fr.cimut.ged.entrant.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.New;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutXmlException;

/**
 * Class utile pour les operations filesystemes
 * 
 * @author gyclon
 *
 */
public class FileHelper {

	
	private static final Logger LOGGER = Logger.getLogger(FileHelper.class);

	private FileHelper() {
		
	}

	/**
	 * Efface le fichier xml initial une fois traité
	 * 
	 * @param file
	 * @throws CimutFileException
	 */
	public static void deleteXmlFile(File file) throws CimutFileException {
		if (!(file.exists() && file.canWrite() && file.delete())) {
			throw new CimutFileException("Impossible de supprimer le document suivant : " + file.getAbsolutePath());
		}
	}

	/**
	 * Extracts the extension d'un filename, force un retour en minuscule de l'extension
	 * 
	 * @param fileName
	 * @return
	 * @throws CimutFileException
	 */
	public static String getExtension(String fileName) throws CimutFileException {
		if (fileName.indexOf(".") > 0) {
			fileName = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		} else {
			throw new CimutFileException("Impossible d'extraire l'extension depuis le nom de fichier suivant : " + fileName);
		}
		return fileName.toLowerCase();
	}

	/**
	 * deplace/remplace le fichier document dans son emplacement final
	 * 
	 * @param docFile
	 * @param document
	 * @throws CimutConfException
	 */
	public static File moveDocToDestination(File docFile, fr.cimut.ged.entrant.beans.db.Document document)
			throws CimutFileException, CimutConfException {
		String path = DocumentHelper.getPlanDeClassement(document);
		File fileDest = new File(path + document.getId());

		if (!docFile.exists()) {
			throw new CimutFileException(
					"Impossible de deplacer le fichier " + docFile.getAbsolutePath() + " dans le repertoire " + path + ", le fichier n'existe pas ");
		}
		if (fileDest.exists()) {
			if (!fileDest.delete()) {
				throw new CimutFileException("Impossible remplacer le fichier " + docFile.getAbsolutePath());
			}
			throw new CimutFileException("Impossible de deplacer le fichier " + docFile.getAbsolutePath() + " dans le repertoire " + path
					+ ", le fichier de destination existe deja ");
		}
		try {
			Logger.getLogger(FileHelper.class).info("moveDocToDestination => " + docFile.getAbsolutePath() + " => " + fileDest.getAbsolutePath());
			FileUtils.copyFile(docFile, fileDest);
		} catch (IOException e) {
			throw new CimutFileException("Impossible de deplacer le fichier " + docFile.getAbsolutePath() + " dans le repertoire " + path);
		}
		if (!docFile.delete()) {
			throw new CimutFileException("Impossible de supprimer le fichier " + docFile.getAbsolutePath());
		}
		return fileDest;
	}

	/**
	 * Deplace les fichiers dans le repertoire d'erreur de l'integration
	 * 
	 * @param file
	 * @param environnement
	 * @throws CimutFileException
	 * @throws CimutConfException
	 */

	public static void moveDocToError(File file, String environnement) throws CimutFileException, CimutConfException {
		String path = GlobalVariable.getErrorPath(environnement);
		String filePathDestination = path + file.getName();
		File fileDest = new File(filePathDestination);

		if (!file.exists() && fileDest.exists()) {
			return;
		}

		if (fileDest.exists() && !fileDest.delete()) {
			Logger.getLogger(FileHelper.class).info("impossible de supprimer le fichier : " + fileDest.getAbsolutePath());
		}

		if (file.exists()) {

			Logger.getLogger(FileHelper.class).info("moveDocToError => " + file.getAbsolutePath() + " => " + fileDest.getAbsolutePath());

			try {
				FileUtils.copyFile(file, fileDest);
			} catch (IOException e) {
				throw new CimutFileException("Impossible de deplacer le fichier " + file.getAbsolutePath() + " dans " + fileDest.getAbsolutePath());
			}
			if (!file.delete()) {
				throw new CimutFileException("Impossible de supprimer le fichier " + file.getAbsolutePath());
			}
		}
	}

	/**
	 * move the doc file to the error folder
	 * 
	 * @param document
	 * @param environnement
	 * @throws CimutFileException
	 * @throws CimutConfException
	 */

	public static void moveDocFromDestToError(fr.cimut.ged.entrant.beans.db.Document document, String environnement)
			throws CimutFileException, CimutConfException {
		String path = DocumentHelper.getPlanDeClassement(document);
		File docFile = new File(path, document.getId());

		// build destination file
		File fileDest = new File(GlobalVariable.getErrorPath(environnement) + document.getId());
		// remove if already present !

		if (fileDest.exists() && !docFile.exists()) {
			return;
		}

		if (fileDest.exists() && !fileDest.delete()) {
			throw new CimutFileException("Impossible de supprimer le fichier " + fileDest.getAbsolutePath() + " pour ecrasement");
		}

		if (docFile.exists()) {
			try {
				Logger.getLogger(FileHelper.class)
						.info("moveDocFromDestToError => " + docFile.getAbsolutePath() + " => " + fileDest.getAbsolutePath());
				FileUtils.copyFile(docFile, fileDest);
			} catch (IOException e) {
				throw new CimutFileException("Impossible de deplacer le fichier " + docFile.getAbsolutePath() + " dans le repertoire " + path);
			}
			if (!docFile.delete()) {
				throw new CimutFileException("Impossible de supprimer le fichier " + docFile.getAbsolutePath());
			}
		}
	}

	/**
	 * recupere le md5 d'un fichier
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String getSignature(File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}

	/**
	 * Return list of file for each occurence
	 * 
	 * @return
	 * @throws CimutFileException
	 * @throws CimutConfException
	 */

	public static Map<String, List<File>> listXmlFile(String environnement) throws CimutFileException, CimutConfException {
		String path = GlobalVariable.getIntegrationPath(environnement).concat("/in");
		File[] xmls = new File(path).listFiles();
		Map<String, List<File>> output = new HashMap<String, List<File>>();

		Map<String, String> mapSignature = new HashMap<String, String>();

		if (xmls == null) {

			return output;
		}

		// collect all xml in a hasmap
		for (int i = 0; i < xmls.length; i++) {

			// avoid directory
			if (xmls[i].isDirectory() || !(xmls[i].canWrite())) {
				continue;
			}

			String name = xmls[i].getName();
			// process here only the xml file
			if (name.endsWith(".xml")) {
				String key = name.substring(0, name.length() - 4);
				List<File> listFile = new ArrayList<File>();
				listFile.add(xmls[i]);
				output.put(key, listFile);
			}
			try {
				mapSignature.put(name, getSignature(xmls[i]));
			} catch (IOException e) {
				throw new CimutFileException("Calcul de la signature impossible du fichier : " + xmls[i].getAbsoluteFile(), e);
			}
		}

		// reloop over all files to find wich is the document of the associated file of the xml
		for (int i = 0; i < xmls.length; i++) {

			String name = xmls[i].getName();

			if (xmls[i].isDirectory() || name.indexOf(".") < 0) {
				continue;
			}

			String key = name.substring(0, name.lastIndexOf("."));

			// supprime de la liste tous les fichier dont le md5 a été modifié au cours de ce traitement
			try {
				if (!mapSignature.get(name).equals(getSignature(xmls[i]))) {
					output.remove(key);
				} else if (!name.endsWith(".xml") && output.containsKey(key)) {
					output.get(key).add(xmls[i]);
				}
			} catch (IOException e1) {
				throw new CimutFileException("Calcul de la signature impossible du fichier : " + xmls[i].getAbsoluteFile(), e1);
			}
		}

		mapSignature.clear();

		// on supprime les elements non associé
		for (Iterator<Map.Entry<String, List<File>>> it = output.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, List<File>> entry = it.next();
			List<File> value = entry.getValue();
			if (value.size() < 2) {
				it.remove();
			}
		}
		return output;
	}
	
	/**
	 * Return list of file for each occurence
	 * 
	 * @return
	 * @throws CimutFileException
	 * @throws CimutConfException
	 * @throws CimutXmlException 
	 * @throws  
	 */

	public static Map<File, List<Map<String, Object>>> listXmlFileMultiple(String environnement) throws CimutFileException, CimutConfException, CimutXmlException {
		String path = GlobalVariable.getIntegrationPath(environnement).concat("/in");
		File[] xmls = new File(path).listFiles();
		Map<File, List<Map<String, Object>>> output = new HashMap<File, List<Map<String, Object>>>();
		Map<String, String> mapSignature = new HashMap<String, String>();

		if (xmls == null) {
			return output;
		}

		// collect all xml in a hasmap
		for (int i = 0; i < xmls.length; i++) {

			// avoid directory
			if (xmls[i].isDirectory() || !(xmls[i].canWrite())) {
				continue;
			}

			String name = xmls[i].getName();
			// process here only the xml file
			if (name.endsWith(".xml")) {
				List<Map<String, Object>> listPdfAssociated = getDocsAssociatedWithFile(xmls[i], path);
				output.put(xmls[i], listPdfAssociated);
			}
			try {
				mapSignature.put(name, getSignature(xmls[i]));
			} catch (IOException e) {
				throw new CimutFileException("Calcul de la signature impossible du fichier : " + xmls[i].getAbsoluteFile(), e);
			}
		}

		mapSignature.clear();
		return output;
	}
	
	/**
	 * retourne la liste des documents associés à un fichier XML
	 * 
	 * @param file le fichier XML à parcourir
	 * @param path répertoire dans lequel se trouve les documents associés
	 * @return la liste des fichiers associés à l'XML
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws CimutXmlException 
	 */
	public static List<Map<String, Object>>getDocsAssociatedWithFile(File xml, String path) throws CimutXmlException {
		ArrayList<Map<String, Object>> listDocs = new ArrayList<Map<String, Object>>();
		
		Map<String, String> xmlDataMap = readInputFile(xml);
		//parsing des données du xml pour récupérer la liste des fichiers associes
		for (Map.Entry<String, String> entry : xmlDataMap.entrySet()) {
			Map<String, Object> mapAttributesDocument = new HashMap<String, Object>();
			String key = entry.getKey();
			if (key != null && key.matches(GlobalVariable.DOCUMENT_TAG_PATTERN) ) {
				String index = key.substring(GlobalVariable.ATTR_DOCUMENT.length());
				File pdf = new File(path.concat("/" + entry.getValue()));
				mapAttributesDocument.put(GlobalVariable.ATTR_DOCUMENT, pdf);
				mapAttributesDocument.put(GlobalVariable.ATTR_TYPE_DOCUMENT, xmlDataMap.get(GlobalVariable.ATTR_TYPE_DOCUMENT + index));
				mapAttributesDocument.put(GlobalVariable.ATTR_ID_EXT_DOC, xmlDataMap.get(GlobalVariable.ATTR_ID_EXT_DOC + index));
				mapAttributesDocument.put(GlobalVariable.ATTR_LIBELLE_DOC, xmlDataMap.get(GlobalVariable.ATTR_LIBELLE_DOC + index));
				listDocs.add(mapAttributesDocument);
			}
		}
		return listDocs;
	}
	
	/**
	 * Lit un fichier XML d'entrée
	 * 
	 * @param xml fichier XML d'entrée
	 * @return la map des données récoltées
	 * @throws CimutXmlException 
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public static Map<String, String> readInputFile(File xml) throws CimutXmlException  {
		
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;	
			db = dbf.newDocumentBuilder();
			Map<String, String> xmlDataMap = new HashMap<String, String>();
			Document document = db.parse(xml);
			
			Element node = document.getDocumentElement();
			for (int s = 0; s < node.getChildNodes().getLength(); s++) {

				Node fstNode = ((NodeList) node).item(s);
				if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
						Element fstElmnt = (Element) fstNode;
						xmlDataMap.put(fstElmnt.getNodeName(), fstElmnt.getTextContent());					}
			}
			return xmlDataMap;
			
		} catch (ParserConfigurationException e) {
			throw new CimutXmlException("Impossible de parser le fichier xml : " + xml.getName(), e);
		} catch (SAXException e1) {
			throw new CimutXmlException("Impossible de parser le fichier xml : " + xml.getName(), e1);
		} catch (IOException e2) {
			throw new CimutXmlException("Impossible de parser le fichier xml : " + xml.getName(), e2);
		}		
		
	}


	/**
	 * Permet d'ecrire les erreurs dans un fichier plats
	 * 
	 * @param text
	 * @param filename
	 * @throws CimutFileException
	 */

	public static void logFile(String text, String filename) throws CimutFileException {
		java.io.PrintWriter out = null;
		try {
			out = new java.io.PrintWriter(filename);
			out.println(text);
		} catch (Exception e) {
			throw new CimutFileException(e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * transform xml into Map<String,Map<String,String>> wich is suitable to rebuild our Document later
	 * 
	 * @param file
	 *            xml file
	 * @return Map => Map<String,Map<String,String>>
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws CimutXmlException
	 */
	public static Map<String, Map<String, String>> loadXmlFile(File file) throws CimutXmlException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Document doc = null;

		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);
		} catch (ParserConfigurationException e) {
			throw new CimutXmlException("Impossible de parser le fichier xml : " + file.getName(), e);
		} catch (SAXException e1) {
			throw new CimutXmlException("Impossible de parser le fichier xml : " + file.getName(), e1);
		} catch (IOException e2) {
			throw new CimutXmlException("Impossible de parser le fichier xml : " + file.getName(), e2);
		}

		doc.getDocumentElement().normalize();
		Element node = doc.getDocumentElement();

		// initialize the output structure
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		for (String string : GlobalVariable.HASH_KEYS) {
			map.put(string, new HashMap<String, String>());
		}

		for (int s = 0; s < node.getChildNodes().getLength(); s++) {

			Node fstNode = ((NodeList) node).item(s);

			if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
				Element fstElmnt = (Element) fstNode;
				// get key/value
				String attrName = fstElmnt.getNodeName();
				String attrValue = fstElmnt.getTextContent();

				// map them
				if (!attrName.equalsIgnoreCase(GlobalVariable.ATTR_ATTRIBUTS)) {
					if (GlobalVariable.OBLIGATOIRE_DB.contains(attrName.toUpperCase())) {
						map.get(GlobalVariable.ATTR_DATABASE).put(attrName, attrValue);
					} else if (GlobalVariable.OPTIONAL_DB.contains(attrName.toUpperCase())) {
						map.get(GlobalVariable.ATTR_DATABASE).put(attrName, attrValue);
					}
					map.get(GlobalVariable.ATTR_JSON).put(attrName, attrValue);
				} else {

					// get attributes
					for (int attIdx = 0; attIdx < fstNode.getChildNodes().getLength(); attIdx++) {
						if (fstNode.getNodeType() == Node.ELEMENT_NODE) {
							Element attFstElmnt = (Element) fstNode;
							String attAttrName = attFstElmnt.getNodeName();
							String attAttrValue = attFstElmnt.getTextContent();
							map.get(GlobalVariable.ATTR_ATTRIBUTS).put(attAttrName, attAttrValue);
						}
					}
				}
			} else if (fstNode.getNodeType() == Node.TEXT_NODE) {
				// looks like indent goes here
			} else {
				throw new CimutXmlException("le fichier XML n'est pas au format cimut : " + fstNode.getNodeName() + " " + fstNode.getNodeType() + " "
						+ fstNode.getNodeValue());
			}
		}
		return map;
	}

	/**
	 * Recupere le Type Mime pour le nom de fichier fournit Se base sur l'extension du fichier
	 * 
	 * @param nameFile
	 * @return
	 * @throws CimutFileException
	 */
	public static String getTypeMime(String nameFile) throws CimutFileException {
		String extension = FileHelper.getExtension(nameFile);
		String mime = null;
		if (extension.equalsIgnoreCase(".pdf")) {
			mime = "application/pdf";
		} else if (extension.equalsIgnoreCase(".ps") || extension.equalsIgnoreCase(".eps")) {
			mime = "application/postscript";
		} else if (extension.equalsIgnoreCase(".bmp")) {
			mime = "image/bmp";
		} else if (extension.equalsIgnoreCase(".csv")) {
			mime = "text/csv";
		} else if (extension.equalsIgnoreCase(".gif")) {
			mime = "image/gif";
		} else if (extension.equalsIgnoreCase(".jpeg") || extension.equalsIgnoreCase(".jpg")) {
			mime = "image/jpeg";
		} else if (extension.equalsIgnoreCase(".png")) {
			mime = "image/png";
		} else if (extension.equalsIgnoreCase(".rtf")) {
			mime = "application/rtf";
		} else if (extension.equalsIgnoreCase(".tif") || extension.equalsIgnoreCase("tiff")) {
			mime = "image/tiff";
		} else if (extension.equalsIgnoreCase(".txt")) {
			mime = "text/plain";
		} else if (extension.equalsIgnoreCase(".doc")) {
			mime = "application/msword";
		} else if (extension.equalsIgnoreCase(".docx")) {
			mime = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
		} else if (extension.matches("^\\.html?$")) {
			mime = "text/html";
		} else if (extension.equalsIgnoreCase(".xls")) {
			mime = "application/vnd.ms-excel";
		} else if(extension.equalsIgnoreCase(".xlsx")) {
			mime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		} else if (extension.matches("^\\.(?i:ppt[x]?)$")) {
			mime = "application/vnd.ms-powerpoint";
		} else if (extension.equalsIgnoreCase(".xml")) {
			mime = "application/xml";
		} else if (extension.equalsIgnoreCase(".eml")) {
			mime = "message/rfc822";
		} else if (extension.equalsIgnoreCase(".vcf")) {
			mime = "text/x-vcard";
		} else if (extension.equalsIgnoreCase(".msg") || extension.equalsIgnoreCase(".oft")) {
			mime = "application/vnd.ms-outlook";
		} else if(extension.equalsIgnoreCase(".odt")) {
			mime = "application/vnd.oasis.opendocument.text";
		} else if(extension.equalsIgnoreCase(".zip")) {
			mime = "application/zip";
		} else {
			mime = "application/octet-stream";
		}


		if (mime.equals("application/octet-stream")) {
			LOGGER.warn("Extension (" + extension + ") sans mapping sur un type mime");
		}
		return mime;
	}

	public static String getExtensionFromMime(String mimeType) {
		String extension = ".unk";
		if (mimeType == null || mimeType.isEmpty()) {
			return extension;
		} else if (mimeType.startsWith("application/pdf")) {
			return ".pdf";
		} else if (mimeType.startsWith("application/postscript")) {
			return ".ps";
		} else if (mimeType.startsWith("image/bmp")) {
			return ".bmp";
		} else if (mimeType.startsWith("text/csv")) {
			return ".csv";
		} else if (mimeType.startsWith("image/gif")) {
			return ".gif";
		} else if (mimeType.startsWith("image/jpeg")) {
			return ".jpeg";
		} else if (mimeType.startsWith("image/png")) {
			return ".png";
		} else if (mimeType.startsWith("application/rtf")) {
			return ".rtf";
		} else if (mimeType.startsWith("image/tiff")) {
			return ".tiff";
		} else if (mimeType.startsWith("text/plain")) {
			return ".txt";
		} else if (mimeType.startsWith("application/msword")) {
			return ".doc";
		} else if (mimeType.startsWith("text/html")) {
			return ".html";
		} else if (mimeType.startsWith("application/vnd.ms-excel")) {
			return ".xls";
		} else if (mimeType.startsWith("application/vnd.ms-powerpoint")) {
			return ".ppt";
		} else if (mimeType.startsWith("application/xml")) {
			return ".xml";
		} else if (mimeType.startsWith("message/rfc822")) {
			return ".eml";
		} else if (mimeType.startsWith("text/x-vcard")) {
			return ".vcf";
		} else if (mimeType.startsWith("application/vnd.ms-outlook")) {
			return ".msg";
		} else {
			return extension;
		}
	}

	// On trim du filename, IE ramène tout le chemin du fichier lors d'une upload i.e. C:\....\fichier.zip
	public static String trimBackSlashs(String filename) {
		int indexLastBackSlach = filename.lastIndexOf('\\');
		if (indexLastBackSlach > 0) {
			filename = filename.substring(indexLastBackSlach + 1);
		}
		return filename;
	}
}
