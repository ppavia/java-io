package fr.cimut.ged.entrant.appelmetier.pool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.habilitation.core.ApplicationCimut;
import fr.cimut.habilitation.core.Personne;
import fr.cimut.habilitation.server.core.Compte;

/**
 * <p>
 * Factory permettant d'initialiser l'application pour UITSEM : - Creation d'une instance ApplicationCimut (Singleton) -
 * Recherche de l'application "IHM_CONS" - Positionne le code service a "M-ASSU-CPPF" (service de consultation
 * participation)
 * </p>
 * 
 * @author pymotreff
 *
 */
public class ApplicationFactory {

	/**
	 * <p>
	 * pool d'object personne
	 * </p>
	 */
	private final static Map<Personne, Map<String, ApplicationCimut>> OBJECT_POOL_APPLICATION = new HashMap<Personne, Map<String, ApplicationCimut>>();

	/**
	 * <p>
	 * Default Constructor ApplicationFactory
	 * </p>
	 */
	protected ApplicationFactory() {

	}

	/**
	 * getInstance ApplicationCimut (Singleton)
	 * 
	 * @param user
	 * @return Application
	 * @throws Exception
	 */
	public static ApplicationCimut getInstance(Personne user, String codeAppli) throws CimutMetierException {

		// Bon, j'ai un probleme ici. je dois avoir un client Mos specifique pour l'application selligente
		// vu que celle ci doit concerver un contextId.
		// pour eviter des collisions, je cree donc une fausse appli histoire qu'elle ai sont propre client MOS
		// mais je ne veux pas pour autant un ApplicationCimut associé (vu qu'elle est fake)
		if (codeAppli.matches("^IHM_\\w+_\\w+$")) {
			codeAppli = codeAppli.substring(0, codeAppli.lastIndexOf("_"));
		}
		if (OBJECT_POOL_APPLICATION.get(user) == null || OBJECT_POOL_APPLICATION.get(user).get(codeAppli) == null) {
			Map<String, ApplicationCimut> map = new HashMap<String, ApplicationCimut>();
			if (OBJECT_POOL_APPLICATION.get(user) != null) {
				map = OBJECT_POOL_APPLICATION.get(user);
			}
			//----Recherche de l application
			ApplicationCimut app;
			try {
				app = (ApplicationCimut) ((Compte) user.getCompte()).searchApplication(codeAppli, new fr.cimut.habilitation.util.Boolean(false));
			} catch (Exception e) {
				throw new CimutMetierException(e);
			}
			map.put(codeAppli, app);
			OBJECT_POOL_APPLICATION.put(user, map);
		}
		return OBJECT_POOL_APPLICATION.get(user).get(codeAppli);
	}

	/**
	 * <p>
	 * fonction permmettant de supprimer une application du pool
	 * </p>
	 * 
	 * @param app
	 */
	public static void destroy(ApplicationCimut app) {
		if (app != null) {
			Iterator<Personne> iterator = OBJECT_POOL_APPLICATION.keySet().iterator();
			//__Parcour du pool
			while (iterator.hasNext()) {
				Personne keyPers = iterator.next();
				if (app == (ApplicationCimut) OBJECT_POOL_APPLICATION.get(keyPers)) {
					OBJECT_POOL_APPLICATION.remove(keyPers);
					break;
				}
			}
		}
	}

}
