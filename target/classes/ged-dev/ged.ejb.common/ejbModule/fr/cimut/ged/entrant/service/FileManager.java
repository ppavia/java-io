package fr.cimut.ged.entrant.service;

import java.io.File;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.utils.FileHelper;

public class FileManager {

	private FileManager() {

	}

	/**
	 * Ajoute un document
	 * 
	 * @param xml
	 * @param file
	 * @return
	 * @throws CimutConfException
	 * @throws CimutDocumentException
	 */
	public static File add(File doc, Document document) throws CimutFileException, CimutConfException {
		return FileHelper.moveDocToDestination(doc, document);
	}

}
