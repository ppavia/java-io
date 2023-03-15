package fr.cimut.ged.entrant.utils.mongo;

import java.io.File;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class FileHelper {

	private final static Logger logger = Logger.getLogger(FileHelper.class);

	private FileHelper() {

	}

	/**
	 * Return list of file json
	 * 
	 * @return
	 */
	public static File[] listJsonFile(String path) {

		DocumentsFileNameFilter filtreNomDocuments = new DocumentsFileNameFilter(".*json$");
		File[] jsons = new File(path).listFiles(filtreNomDocuments);
		return jsons != null ? jsons : new File[0];
	}

	public static void deplacement(File jsonfile, String pathDestination) {

		File fileDestination = new File(pathDestination, jsonfile.getName());

		//si le fichier existe deja a la destination, le supprimer
		if (fileDestination.exists() && !fileDestination.delete()) {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error("Impossible de supprimer Le fichier " + fileDestination.getAbsolutePath());
			}
		}

		if (jsonfile.renameTo(fileDestination)) {
			if (logger.isEnabledFor(Level.DEBUG)) {
				logger.debug("Le fichier " + jsonfile.getAbsolutePath() + " est deplace dans le repertoire " + pathDestination);
			}
		} else {
			if (logger.isEnabledFor(Level.ERROR)) {
				logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n a pas pu etre deplace dans le repertoire des erreurs");
			}
		}
	}

}
