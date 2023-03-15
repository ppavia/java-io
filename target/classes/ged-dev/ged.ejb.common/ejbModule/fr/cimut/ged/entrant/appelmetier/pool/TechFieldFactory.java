package fr.cimut.ged.entrant.appelmetier.pool;

import java.util.HashMap;
import java.util.Map;

import fr.cimut.mos.TechnicalFieldAdaptator;

/**
 * <p>
 * </p>
 * 
 * @author Py. MOTREFF
 *
 */

public class TechFieldFactory {

	/**
	 * <p>
	 * Singleton TechnicalFieldAdaptator
	 * </p>
	 */

	private static Map<String, TechnicalFieldAdaptator> TECHNICAL_FIELD = new HashMap<String, TechnicalFieldAdaptator>();

	/**
	 * <p>
	 * default constructor
	 * </p>
	 */

	protected TechFieldFactory() {
	}

	/**
	 * <p>
	 * </p>
	 * 
	 * @return TechnicalFieldAdaptator
	 */

	public static TechnicalFieldAdaptator getInstance(String code) {
		if (!TECHNICAL_FIELD.containsKey(code)) {
			TECHNICAL_FIELD.put(code, new TechnicalFieldAdaptator());
		}
		return TECHNICAL_FIELD.get(code);
	}

	public static boolean isEmpty() {
		return TECHNICAL_FIELD.isEmpty();
	}

	public static void clean(String code) {
		if (TECHNICAL_FIELD.containsKey(code)) {
			TECHNICAL_FIELD.remove(code);
		}
	}

	public static void clean() {
		TECHNICAL_FIELD.clear();
	}
}