package fr.cimut.ged.entrant.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import fr.cimut.ged.entrant.exceptions.CimutConfException;

public class OrganismeHelper {

	/**
	 * structure mettant en realtion les organisme et leur tutelles
	 */
	private static Map<String, ArrayList<String>> cmrocToTutelles;

	/**
	 * recupere le chemin du fichier de parametrage
	 * 
	 * @return
	 * @throws CimutConfException
	 */
	private static String getFilePath() throws CimutConfException {
		return GlobalVariable.getCmrocFilePath();
	}

	private static Map<String, ArrayList<String>> getCmrocToTutelles() throws CimutConfException {
		//if (cmrocToTutelles == null){
		loadCmrocs();
		//}
		return cmrocToTutelles;
	}

	/**
	 * realise le mapping entre organismes et tutelles depuis un fichier plat
	 * 
	 * @throws IOException
	 * @throws CimutConfException
	 */
	private static void loadCmrocs() throws CimutConfException {

		// reset previous stored
		cmrocToTutelles = new HashMap<String, ArrayList<String>>();

		// get conf file
		File file = new File(getFilePath());
		BufferedReader reader = null;
		try {

			reader = new BufferedReader(new FileReader(file));
			String line = null;
			StringTokenizer stringToken = null;

			// loop over it to load datas
			while ((line = reader.readLine()) != null) {
				stringToken = new StringTokenizer(line, ";");
				String organisme = stringToken.nextToken();
				ArrayList<String> tutelles = new ArrayList<String>();
				while (stringToken.hasMoreTokens()) {
					tutelles.add(stringToken.nextToken());
				}
				cmrocToTutelles.put(organisme, tutelles);
			}

			// close file
		} catch (FileNotFoundException e) {
			throw new CimutConfException(e);
		} catch (IOException e) {
			throw new CimutConfException(e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {

				}
			}
		}

	}

	/**
	 * recupere l'organisme au dessus de la tutelle. lui meme si non trouver dans le fichier de parametrage
	 * 
	 * @param cmroc
	 * @return
	 * @throws IOException
	 * @throws CimutConfException
	 */

	public static String getOrganisme(String cmroc) throws CimutConfException {

		Map<String, ArrayList<String>> cmroc2Tutelles;
		cmroc2Tutelles = getCmrocToTutelles();
		for (String org : cmroc2Tutelles.keySet()) {
			if (cmroc2Tutelles.containsKey(cmroc)) {
				return cmroc;
			} else {
				for (String tut : cmroc2Tutelles.get(org)) {
					if (tut.equals(cmroc)) {
						return org;
					}
				}
			}
		}
		return cmroc;
	}

	/**
	 * recupere la listes de tutelles ratache a un organisme en particuler
	 * 
	 * @param cmroc
	 * @return
	 * @throws IOException
	 * @throws CimutConfException
	 */
	public static List<String> getTutelles(String cmroc) throws CimutConfException {

		Map<String, ArrayList<String>> cmroc2Tutelles = getCmrocToTutelles();

		// init
		ArrayList<String> list = new ArrayList<String>();

		// get list
		if (cmroc2Tutelles.containsKey(cmroc)) {
			list = cmroc2Tutelles.get(cmroc);
		}

		// add the org as a tutelle of himself ...
		list.add(cmroc);
		return list;
	}

}
