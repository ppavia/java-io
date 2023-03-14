package fr.cimut.ged.entrant.mongo;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Client {

	public static void rebuildall(String environnement) {

		Logger logger = Logger.getLogger(Client.class);
		if (logger.isEnabledFor(Level.INFO)) {
			logger.info("rebuildall pour l'environnement " + environnement);
		}

		try {
			InteractionMongo interactionMongo = new InteractionMongo();

			try {
				//suppression des documents de la collection
				interactionMongo.removeCollection(environnement);

				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Suppression de l'ensemble des documents de la collection :   OK.");
				}

				//reparation compactage de la base (operation optionnel)
				interactionMongo.repairDatabase(environnement);

				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Fin rebuildall " + environnement + ": Le rebuild est effectue   OK.");
				}

				// appel de la fonction de regeneration des json d entree
			} catch (Exception e) {
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error("Erreur " + e.getMessage(), e);
				}
			}

			try {
				//fermeture de la connexion mongo
				interactionMongo.closeConnexion();
			} catch (Exception e) {
				if (logger.isEnabledFor(Level.ERROR)) {
					logger.error("Erreur durant la fermeture des connexion ", e);
				}
			}

		} catch (Exception e) {
			if (logger.isEnabledFor(Level.FATAL)) {
				logger.fatal("Erreur : " + e.getMessage(), e);
			}
		}

	}
}
