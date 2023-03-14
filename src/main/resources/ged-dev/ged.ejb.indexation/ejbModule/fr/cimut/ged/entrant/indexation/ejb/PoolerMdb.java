package fr.cimut.ged.entrant.indexation.ejb;

import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.service.DocumentService;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.service.MongoConnection;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

/**
 * Session Bean implementation class PoolerMdb
 */
@TransactionAttribute(value = TransactionAttributeType.NEVER)
@Singleton(mappedName = "Pooler")
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PoolerMdb {

	private Logger LOGGER = Logger.getLogger(PoolerMdb.class);

	@EJB
	DocumentService documentService;

	@EJB
	MongoConnection mongoConnection;

	@EJB
	Metier metier;

	public PoolerMdb() {

	}

	@Schedule(hour = "*/10", dayOfWeek = "Mon-Fri", persistent = false)
	@Lock(LockType.WRITE)
	public void findElligibleCritere() {
		if (GlobalVariable.checkIfMasterForScheduledTask()) {
			// Récupération de la liste des environnements gérés
			List<String> environnements;
			try {
				environnements = EnvironnementHelper.getEnvironnements();
			} catch (CimutConfException e) {
				LOGGER.fatal("commentaire manquant", e);
				return;
			}

			// Mise à jour des Critères éligibles pour l'ensemble des environnements et des CMROCs
			try {
				mongoConnection.getMongoClient().setElligibleCriteres(environnements);
			} catch (CimutMongoDBException e) {
				LOGGER.fatal("commentaire manquant", e);
			}
		}
	}

	@Schedule(hour = "8", dayOfWeek = "Mon-Fri", persistent = false)
	@Lock(LockType.WRITE)
	public void sendMail() {
		// Récupération de la liste des environnements gérés
		List<String> environnements;
		try {
			environnements = EnvironnementHelper.getEnvironnements();
		} catch (CimutConfException e) {
			LOGGER.fatal("commentaire manquant", e);
			return;
		}

		// Parcours de chacun des environnements gérés
		PoolerHelper poolerHelper = new PoolerHelper(documentService, mongoConnection.getMongoClient(), metier);
		for (String environnement : environnements) {
			LOGGER.info("START MAIL POOLER");
			try {
				for (String cmroc : GlobalVariable.getListCmrocs()) {
					poolerHelper.processBaseNameMail(environnement, cmroc);
				}
			} catch (Exception e) {
				LOGGER.fatal("commentaire manquant", e);
			}
			LOGGER.info("END MAIL POOLER");
		}
	}

	/**
	 * Methode qui lit les regles definis dans CRDE et cree les DAs pour tous les documents qui matchent.
	 * 
	 */

	@Schedule(minute = "*/2", hour = "*", persistent = false)
	@Lock(LockType.WRITE)
	public void getRules() {
		if (GlobalVariable.checkIfMasterForScheduledTask()) {
			// Récupération de la liste des environnements gérés
			List<String> environnements;
			try {
				environnements = EnvironnementHelper.getEnvironnements();
			} catch (CimutConfException e) {
				LOGGER.fatal("commentaire manquant", e);
				return;
			}

			// Parcours des Rules de chacun des environnements gérés
			PoolerHelper poolerHelper = new PoolerHelper(documentService, mongoConnection.getMongoClient(), metier);
			for (String environnement : environnements) {
				try {
					for (String cmroc : GlobalVariable.getListCmrocs()) {
						poolerHelper.processBaseName(environnement, cmroc);
					}
				} catch (CimutConfException e) {
					LOGGER.fatal("commentaire manquant", e);
				} catch (Exception e) {
					LOGGER.fatal("commentaire manquant", e);
				}
				LOGGER.debug("END POOLER FOR ENV " + environnement);
			}
		}
	}

	@Schedule(hour = "*/1", dayOfWeek = "Mon-Fri", persistent = false)
	@Lock(LockType.WRITE)
	public void updParameters() {
		if (GlobalVariable.checkIfMasterForScheduledTask()) {
			// Récupération de la liste des environnements gérés
			List<String> environnements;
			try {
				environnements = EnvironnementHelper.getEnvironnements();
			} catch (CimutConfException e) {
				LOGGER.fatal("commentaire manquant", e);
				return;
			}

			// Mise à jour des Paramètres pour l'ensemble des Critères de chaque CMROC dans chaque environnement
			try {
				LOGGER.debug("updParameters start");
				mongoConnection.getMongoClient().updateParameters(environnements);
			} catch (CimutConfException e) {
				LOGGER.fatal(e.getMessage(), e);
			} catch (CimutMongoDBException e) {
				LOGGER.fatal(e.getMessage(), e);
			} finally {
				LOGGER.debug("updParameters end");
			}
		}
	}
}
