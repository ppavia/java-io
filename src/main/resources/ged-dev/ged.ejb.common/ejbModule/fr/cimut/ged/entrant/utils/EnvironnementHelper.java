package fr.cimut.ged.entrant.utils;

import java.util.Arrays;
import java.util.List;

import fr.cimut.ged.entrant.exceptions.CimutConfException;

/**
 * Facilite la détermination de l'environnement cible
 * 
 * @author pgarel
 */
public class EnvironnementHelper {

	/** Séparateur utilisé pour séparer les environnements gérés dans le fichier standalone.xml */
	private static final String SEPARATEUR_ENV = ";";

	/** Constructeur privé, pour empêcher son instantiation */
	private EnvironnementHelper() {
	}

	/**
	 * Retourne :
	 * <ul>
	 * <li>la liste des environnements gérés dans le cadre d'une gestion multi-environnement</li>
	 * <li>l'environnement par défaut dans le cadre d'une gestion mono-environnement</li>
	 * </ul>
	 * 
	 * @throws CimutConfException
	 *             Levée si aucun environnement n'est défini
	 */
	public static List<String> getEnvironnements() throws CimutConfException {
		String varEnvir = GlobalVariable.getEnvironnement().trim();
		if (varEnvir.isEmpty()) {
			throw new CimutConfException("Aucun environnement n'est défini");
		}
		return Arrays.asList(varEnvir.toUpperCase().split(SEPARATEUR_ENV));
	}

	/**
	 * Retourne <code>true</code> si l'environnement passé en paramètre est géré par cette instance de la GEDe,
	 * <code>false</code> sinon.
	 * 
	 * @throws CimutConfException
	 *             Levée si l'environnement par défaut n'est pas défini
	 */
	private static boolean isEnvironnementGere(String environnement) throws CimutConfException {
		return getEnvironnements().contains(environnement);
	}

	/**
	 * Retourne <code>true</code> si l'instance est en mode multi-environnement, <code>false</code> sinon
	 * 
	 * @throws CimutConfException
	 *             Levée si aucun environnement n'est défini
	 */
	public static boolean isModeMultiEnvironnement() throws CimutConfException {
		return getEnvironnements().size() > 1;
	}

	/**
	 * Retourne l'environnement à utiliser :
	 * <ul>
	 * <li>Gestion mono-environnement : retourne l'environnement défini pour le serveur</li>
	 * <li>Gestion multi-environnement :
	 * <ul>
	 * <li>retourne l'environnement passé en paramètre s'il est géré,</li>
	 * <li>retourne l'environnement par défaut (le 1er configuré) si l'environnement passé est <code>null</code>,</li>
	 * <li>sinon retourne <code>null</code></li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param environnement
	 *            Environnement, obligatoire dans le cas d'une gestion multi-environnement
	 * @throws CimutConfException
	 *             Levée si aucun environnement n'est défini
	 */
	public static String determinerEnvironnement(String environnement) throws CimutConfException {
		if (isModeMultiEnvironnement()) {
			if (environnement == null || environnement.trim().isEmpty()) {
				// Mode multi-environnement : utilisation du 1er environnement par défaut si aucun n'est passé
				return getEnvironnements().get(0);
			} else {
				// Mode multi-environnement : utilisation de l'environnement passé s'il est géré
				environnement = environnement.trim().toUpperCase();
				if (isEnvironnementGere(environnement)) {
					return environnement;
				} else {
					return null;
				}
			}
		} else {
			// Mode mono-environnement : utilisation de l'environnement défini
			return getEnvironnements().get(0);
		}
	}

}
