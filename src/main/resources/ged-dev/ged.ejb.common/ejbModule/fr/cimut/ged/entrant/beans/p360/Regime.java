package fr.cimut.ged.entrant.beans.p360;

import java.util.HashMap;
import java.util.Map;

import fr.cimut.util.GlobalProperties;

/**
 * Enumeration des regimes
 * 
 * @author pgarel
 */
public enum Regime {

	/** regime Obligatoire */
	RO,
	/** regime Complementaire */
	RC;

	/** propriete listant les CMROC pour le regime RO */
	private static final String LISTE_CMROC_REGIME_RO = "liste.cmroc.regime.RO";

	/** propriete listant les CMROC pour le regime RC */
	private static final String LISTE_CMROC_REGIME_RC = "liste.cmroc.regime.RC";

	/** Map associant un regime par CMROC */
	private static final Map<String, Regime> mapRegimeParCMROC = new HashMap<String, Regime>();

	/** Peuplement de la map associant un regime par CMROC */
	static {
		peuplerMap(LISTE_CMROC_REGIME_RO, RO);
		peuplerMap(LISTE_CMROC_REGIME_RC, RC);
	}

	/** Methode de peuplement de la map a partir de la propriete */
	private static void peuplerMap(String propriete, Regime regime) {
		String valeurPropriete = GlobalProperties.getGlobalProperty(propriete);
		if (valeurPropriete != null && !valeurPropriete.isEmpty()) {
			String[] cmrocs = valeurPropriete.split(",");
			for (String cmroc : cmrocs) {
				mapRegimeParCMROC.put(cmroc, regime);
			}
		}
	}

	/**
	 * Retourne le regime correspondant au CMROC fourni
	 * 
	 * @param cmroc
	 *            CMROC pour lequel le regime est demandé
	 * @return le {@link Regime} correspondant au CMROC fourni
	 */
	public static Regime getRegime(String cmroc) {
		return mapRegimeParCMROC.get(cmroc);
	}

}
