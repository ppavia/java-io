package fr.cimut.ged.entrant.appelmetier.pool;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.axis2.AxisFault;

import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.habilitation.core.Personne;
import fr.cimut.mos.ClientMos;
import fr.cimut.util.GlobalProperties;

/**
 * <p>
 * </p>
 * 
 * @author Py. MOTREFF
 *
 */
public class ClientMosFactory {

	/**
	 * Singleton CLientMos
	 */
	private static Map<String, ClientMos> CLIENT_MOS = new HashMap<String, ClientMos>();

	/**
	 * recupere les clients
	 * 
	 * @return
	 */
	public static ClientMos getClient(String cmroc, String ihm, String environnement) {
		return CLIENT_MOS.get(ihm + "|" + cmroc + "|" + environnement);
	}

	/**
	 * <p>
	 * default construtor
	 * </p>
	 */
	protected ClientMosFactory() {
	}

	/**
	 * <p>
	 * retour l'instance clientmos
	 * </p>
	 * 
	 * @param user
	 * @return ClientMos
	 * @throws AxisFault
	 */
	public static ClientMos getInstance(Personne user, String ihm, String environnement) throws AxisFault {
		String key = ihm + "|" + user.getCompte().getOrganisme().getCmroc() + "|" + environnement;
		ClientMos clientMos = CLIENT_MOS.get(key);

		if (clientMos == null) {
			try {
				// Récupération de l'URL du MOS à atteindre
				String urlSkeleton = GlobalProperties.getGlobalProperty("fr.cimut.mos.skeleton.address." + environnement);

				if (urlSkeleton == null || urlSkeleton.trim().isEmpty()) {
					// Multi-environnement : L'URL pour l'environnement fourni doit être connu
					if (EnvironnementHelper.isModeMultiEnvironnement()) {
						throw new NullPointerException();
					}
					// Mono-environnement : Si l'URL pour l'environnement fourni n'est pas connu, celui par défaut est utilisé
					else {
						urlSkeleton = GlobalProperties.getGlobalProperty("fr.cimut.mos.skeleton.address").toString();
					}
				}

				clientMos = new ClientMos(user, urlSkeleton);
				addClient(key, clientMos);
			} catch (Exception e) {
				throw new AxisFault("L'url pour joindre le service n'est pas defini (environnement : " + environnement + ")");
			}
		}

		return clientMos;
	}

	/**
	 * référencement du client MOS
	 * 
	 * @param key
	 * @param clientMos
	 */
	private static void addClient(String key, ClientMos clientMos) {
		// ATTENTION A NE RIEN FAIRE DANS CETTE METHODE - AFIN D'EVITER TOUT PROBLEME 
		// (problème de gestion des stubs axis2 : CF. ticket jira HL 14982)			
		// tests reéalisés via jmeter,, au bout de 60 appels (avec 1 appel toute les secondes, ce système de sauvegarde des clients mos échoue)
		// après correction 1000 appels en 100 secondes => OK		
		// CLIENT_MOS.put(key, clientMos);

	}

	/**
	 * retourne tous les clients
	 * 
	 * @return
	 */
	public static Collection<ClientMos> getClients() {
		return CLIENT_MOS.values();
	}
}
