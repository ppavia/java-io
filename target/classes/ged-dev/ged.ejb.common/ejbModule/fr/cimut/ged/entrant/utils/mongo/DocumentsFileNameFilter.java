package fr.cimut.ged.entrant.utils.mongo;

/**
 * 
 */

import java.io.File;
import java.io.FilenameFilter;

/**
 * <p>
 * Permet de créer un filtre sur le nom des fichiers, à utiliser avec la méthode listFiles d'un objet de type File.
 * </p>
 * 
 * @see FilenameFilter
 * @see File#list(FilenameFilter)
 * @see File#listFiles(FilenameFilter)
 * 
 * @author bortner
 * 
 */
public class DocumentsFileNameFilter implements FilenameFilter {

	/**
	 * <p>
	 * Filtre.
	 * </p>
	 */
	private String fileNameRegex = null;

	/**
	 * <p>
	 * Constructeur.
	 * </p>
	 * 
	 * @param pfileNameRegex
	 */
	public DocumentsFileNameFilter(final String pfileNameRegex) {

		super();
		this.setFileNameRegex(pfileNameRegex);
	}

	/**
	 * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
	 */
	@Override
	public final boolean accept(final File dir, final String name) {

		return name.matches(this.getFileNameRegex());
	}

	/**
	 * @return the fileNameRegex
	 */
	private String getFileNameRegex() {
		return fileNameRegex;
	}

	/**
	 * @param pfileNameRegex
	 *            the fileNameRegex to set
	 */
	public final void setFileNameRegex(final String pfileNameRegex) {
		this.fileNameRegex = pfileNameRegex;
	}

}
