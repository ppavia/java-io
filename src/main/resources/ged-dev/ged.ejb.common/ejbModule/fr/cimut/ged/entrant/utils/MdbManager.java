package fr.cimut.ged.entrant.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;

public class MdbManager {

	public static String getJsonFile(String action, Document document, String environnement) throws CimutFileException, CimutConfException {

		String path = GlobalVariable.getIndexationPath(environnement);

		if (action.equals("add")) {
			path += "add";
		} else if (action.equals("delete")) {
			path += "delete";
		} else if (action.equals("update")) {
			path += "update";
		} else {
			throw new CimutFileException("Aucune action viable dans la methode getJsonFile : " + action);
		}

		path += File.separator;
		path += OrganismeHelper.getOrganisme(document.getCmroc()) + "_" + document.getId();
		path += ".json";

		return path;
	}

	public static void add(fr.cimut.ged.entrant.beans.db.Document document, String environnement)
			throws CimutFileException, CimutDocumentException, CimutConfException {

		String path = getJsonFile("add", document, environnement);
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException e) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e);
		} catch (UnsupportedEncodingException e1) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e1);
		}

		try {
			writer.println(document.getJson().getData());
		} finally {
			writer.close();
		}

	}
	
	public static void addFromDocMongo(DocumentMongo documentMongo, String environnement)
			throws CimutFileException, CimutDocumentException, CimutConfException {
		
		String path = GlobalVariable.getIndexationPath(environnement);
		path += "add";
		path += File.separator;
		path += OrganismeHelper.getOrganisme(documentMongo.getCmroc()) + "_" + documentMongo.getId();
		path += ".json";
		
		PrintWriter writer = null;

		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException e) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e);
		} catch (UnsupportedEncodingException e1) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e1);
		}

		try {
			Json json = new Json();
			json.setData(DocumentHelper.stringify(documentMongo));
			writer.println(json.getData());
		} finally {
			writer.close();
		}

	}


	public static void delete(Document document, String environnement) throws CimutFileException, CimutConfException {

		String path = getJsonFile("delete", document, environnement);

		PrintWriter writer = null;

		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException e) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e);
		} catch (UnsupportedEncodingException e1) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e1);
		}
		try {
			if (document.getJson() != null) {
				writer.println(document.getJson().getData());
			}
		} finally {
			writer.close();
		}
	}

	public static void update(Document document, String environnement) throws CimutFileException, CimutDocumentException, CimutConfException {

		String path = getJsonFile("update", document, environnement);

		PrintWriter writer = null;

		try {
			writer = new PrintWriter(path, "UTF-8");
		} catch (FileNotFoundException e) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e);
		} catch (UnsupportedEncodingException e1) {
			throw new CimutFileException("Impossible de cree le nouveau fichier : " + path, e1);
		}
		try {
			writer.println(document.getJson().getData());
		} finally {
			writer.close();
		}

	}

	public static void deleteInputFile(String action, Document document, String environnement) throws CimutFileException, CimutConfException {

		String path = getJsonFile(action, document, environnement);

		File file = new File(path);
		if (file.exists() && !file.delete()) {
			throw new CimutFileException("Impossible d'effacer le fichier : " + file.getAbsolutePath());
		}
	}

}
