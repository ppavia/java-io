package fr.cimut.ged.entrant.beans.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.OrganismeHelper;

/**
 * Permet d'obtenir les valeurs par défaut d'un Paramètre pour un CMROC donné
 */
public class ParameterDefault {

	/** Map contenant les valeurs par défaut pour des paramètres par CMROC */
	private static HashMap<String, HashMap<String, List<String>>> defaultValuesParCmroc = null;

	/**
	 * Retourne les valeurs par défaut du paramètre identifié par son ID pour le CMROC donné
	 * 
	 * @param cmroc
	 *            CMROC
	 * @param idParametre
	 *            ID du paramètre
	 * @return Les valeurs par défaut du paramètre identifié par son ID pour le CMROC donné
	 * @throws CimutConfException
	 */
	public static List<String> getDefault(String cmroc, String idParametre) throws CimutConfException {
		HashMap<String, List<String>> defaultMap = getDefaultValuesPourCmroc(cmroc);
		List<String> defaultValues = defaultMap.get(idParametre);
		if (defaultValues == null) {
			return new ArrayList<String>();
		} else {
			return defaultValues;
		}
	}

	/**
	 * Retourne la map contenant les valeurs par défaut pour des paramètres pour le CMROC donné
	 * 
	 * @param cmroc
	 *            CMROC pour lequel les valeurs par défaut des paramètres doit être récupérées
	 * @return La map contenant les valeurs par défaut pour des paramètres pour le CMROC donné
	 * @throws CimutConfException
	 */
	private static HashMap<String, List<String>> getDefaultValuesPourCmroc(String cmroc) throws CimutConfException {
		if (defaultValuesParCmroc == null) {
			defaultValuesParCmroc = new HashMap<String, HashMap<String, List<String>>>();
		}

		HashMap<String, List<String>> defaultMap = defaultValuesParCmroc.get(cmroc);
		if (defaultMap == null) {
			defaultMap = new HashMap<String, List<String>>();
			defaultMap.put(GlobalVariable.ATTR_TYPEDOC, Arrays.asList("Personne", "Partenaire", "Entreprise"));
			defaultMap.put(GlobalVariable.ATTR_TUTELLE, OrganismeHelper.getTutelles(cmroc));
			defaultValuesParCmroc.put(cmroc, defaultMap);
		}

		return defaultMap;
	}

}
