package fr.cimut.ged.entrant.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;

public class GedeUtils {

	/**
	 * retourne l'unique élément de la liste, si elle n'est pas vide <br/>
	 * si plusieurs entités alors throw exception
	 * 
	 * @param <T>
	 * 
	 * @param list
	 * @return
	 * @throws GedeCommonException
	 */
	public static <T> T getUniqueInListIfExists(List<T> list) throws GedeCommonException {
		int size = CollectionUtils.size(list);
		if (0 == size) {
			return null;
		} else if (1 == size) {
			return list.get(0);
		} else {
			throw new GedeCommonException("Plusieurs éléments correspondants");
		}
	}



	/**
	 * retourne une list non nulle
	 * 
	 * @param <T>
	 * 
	 * @param list
	 * @return
	 */
	public static <T> List<T> listEmptyIfNull(List<T> list) {
		if (null == list) {
			return new ArrayList<T>(0);
		} else {
			return list;
		}
	}

	/**
	 * construction des headers pour les requetes vers starwebDao avec forçage de l'environnement
	 * 
	 * @param cmroc
	 * @param env
	 * @return
	 * @throws CimutConfException
	 */
	private static Map<String, String> buildHeaderForStarwebDaoWithForcedEnv(String cmroc, String env) throws CimutConfException {
		Map<String, String> headerParam = new HashMap<String, String>();
		headerParam.put("CMROC", "OC" + cmroc);
		headerParam.put("ENVIRONNEMENT", env);
		return headerParam;
	}

	/**
	 * construction des headers pour les requetes vers starwebDao sur l'environnement courant
	 * @param cmroc
	 * @return
	 * @throws CimutConfException
	 */
	private static Map<String, String> buildHeaderForStarwebDaoWithCurrentEnv(String cmroc) throws CimutConfException {
		Map<String, String> headerParam = new HashMap<String, String>();
		headerParam.put("CMROC", "OC" + cmroc);
		headerParam.put("ENVIRONNEMENT", GlobalVariable.getEnvironnement());
		return headerParam;
	}

	public static Map<String, String> buildHeaderForStarwebDao(String cmroc, String env) throws CimutConfException {
		Map<String, String> headerParam = new HashMap<String, String>();
		if (StringUtils.isEmpty(env)) {
			return buildHeaderForStarwebDaoWithCurrentEnv(cmroc);
		}
		else {
			return buildHeaderForStarwebDaoWithForcedEnv(cmroc, env);
		}
	}

	public static Map<String, String> buildHeaderForStarwebDao(String cmroc, String env, String user) throws CimutConfException {
		Map<String, String> headerMap = buildHeaderForStarwebDao(cmroc, env);
		if (!StringUtils.isEmpty(user)) {
			headerMap.put("user", user);
		}
		return headerMap;
	}

}
