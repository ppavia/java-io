package fr.cimut.ged.entrant.appelmetier.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.cimut.habilitation.core.AuthentificationException;
import fr.cimut.habilitation.core.Personne;
import fr.cimut.habilitation.server.core.Compte;

/**
 * <p>
 * PoolFactory permettant d'initialiser l'utilisateur UITSEM : - Creation d'une instance Personne - Authentification de
 * l'utilisateur - Transformation du compte Core vers compte Serveur - Initialisation de profils
 * </p>
 * 
 * @author Py. MOTREFF
 *
 */
public class UserPoolFactory {

	/**
	 * <p>
	 * Logger a utiliser par la classe en cours.
	 * </p>
	 */
	private static final Logger LOGGER = Logger.getLogger(UserPoolFactory.class);

	/**
	 * <p>
	 * pool d'object personne
	 * </p>
	 */
	private static final Map<String, Personne> OBJECT_POOL_PERSONNE = new HashMap<String, Personne>();

	/**
	 * <p>
	 * Default Constructor UserFactory
	 * </p>
	 */
	protected UserPoolFactory() {
	}

	/**
	 * <p>
	 * getInstance
	 * </p>
	 * 
	 * @return Personne
	 */
	private static Personne createPersonne(String login, String password) {
		Personne user = new Personne();

		//----Authentification de l'utilisateur
		user.authentifier(login, password);

		//----Transformation du compte Core vers compte Serveur
		try {
			user.setCompte(new Compte(user.getCompte()));
		} catch (CloneNotSupportedException exception) {
			UserPoolFactory.LOGGER.fatal("CloneNotSupportedException", exception);
		}

		//----Initialisation de profils
		try {
			((Compte) user.getCompte()).initProfils();
		} catch (Exception exception) {
			UserPoolFactory.LOGGER.fatal("initProfils en erreur", exception);
		}

		return user;
	}

	/**
	 * <p>
	 * getInstance(String login, String password)
	 * </p>
	 * 
	 * @param login
	 * @param password
	 * @return Personne
	 * @throws AuthentificationException
	 */
	public static Personne getInstance(String login, String password) {
		if (OBJECT_POOL_PERSONNE.get(login + "|" + password) == null) {
			//__Creation et authentification du user
			Personne pers = createPersonne(login, password);
			OBJECT_POOL_PERSONNE.put(login + "|" + password, pers);
		}
		return OBJECT_POOL_PERSONNE.get(login + "|" + password);
	}

	/**
	 * <p>
	 * fonction permmettant de supprimer une personne du pool
	 * </p>
	 * <p>
	 * util en cas de probl�me d'habilitation pour force l'authentification
	 * </p>
	 * 
	 * @param p
	 */
	public static void destroy(Personne p) {
		if (p != null) {
			Iterator<String> iterator = OBJECT_POOL_PERSONNE.keySet().iterator();
			//__Parcour du pool
			while (iterator.hasNext()) {
				String key = iterator.next();
				if (p == (Personne) OBJECT_POOL_PERSONNE.get(key)) {
					OBJECT_POOL_PERSONNE.remove(key);
					break;
				}
			}
		}
	}
}
