package fr.cimut.ged.entrant.service;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import fr.cimut.ged.entrant.appelmetier.AlphManager;
import fr.cimut.ged.entrant.appelmetier.SudeManager;
import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.appelmetier.MetierManager;
import fr.cimut.ged.entrant.appelmetier.PartManager;
import fr.cimut.ged.entrant.appelmetier.StruManager;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.habilitation.server.dao.jdbc.KeyedObjectPool;
import fr.cimut.mos.pool.GenericObjectPool;
import fr.cimut.util.GlobalProperties;

/**
 * Session Bean implementation class Metier
 */
@Singleton
@Startup
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class Metier {

	/**
	 * Appel Metier STRU
	 */
	private StruManager struManager;
	/**
	 * Appel Metier PART
	 */
	private PartManager partManager;

	/**
	 * Appel Metier ALPH
	 */
	private AlphManager alphManager;

	/**
	 * Appel Metier Eddm
	 */
	private EddmManager eddmManager;
	/**
	 * Appel Metier Sude
	 */
	private SudeManager sudeManager;

	/**
	 * Default constructor.
	 */
	public Metier() {
	}

	@Lock(LockType.READ)
	public PartManager getPartManager() throws CimutConfException {
		if (partManager == null) {
			partManager = new PartManager();
		}
		return partManager;
	}

	@Lock(LockType.READ)
	public StruManager getStruManager() throws CimutConfException {
		if (struManager == null) {
			struManager = new StruManager();
		}
		return struManager;
	}

	@Lock(LockType.READ)
	public AlphManager getAlphManager() throws CimutConfException {
		if (alphManager == null) {
			alphManager = new AlphManager();
		}
		return alphManager;
	}

	@Lock(LockType.READ)
	public EddmManager getEddmManager() throws CimutConfException {
		if (eddmManager == null) {
			eddmManager = new EddmManager();
		}
		return eddmManager;
	}

	@Lock(LockType.READ)
	public SudeManager getSudeManager() throws CimutConfException {
		if (sudeManager == null) {
			sudeManager = new SudeManager();
		}
		return sudeManager;
	}

	@PostConstruct
	public void initialiser() throws Exception {

		// Vérification des variables de connection MOS
		Logger.getLogger(Metier.class).debug("Vérification des variables de connection MOS");

		// Récupération de l'URL MOS par défaut
		String url = GlobalProperties.getGlobalProperty("fr.cimut.mos.skeleton.address");

		// Multi-environnement : Vérification de la présence de l'URL MOS pour chacun des environnements
		if (EnvironnementHelper.isModeMultiEnvironnement()) {
			List<String> adressesManquantes = new ArrayList<String>();
			for (String environnement : EnvironnementHelper.getEnvironnements()) {
				String property = "fr.cimut.mos.skeleton.address." + environnement;
				String urlEnv = GlobalProperties.getGlobalProperty(property);
				if (urlEnv == null || urlEnv.trim().isEmpty()) {
					adressesManquantes.add(property);
				} else if (url == null || url.trim().isEmpty()) {
					// Si l'URL MOS par défaut est absente, utilisation de la 1ère de la liste
					url = urlEnv;
				}
			}
			if (!adressesManquantes.isEmpty()) {
				String msg = "Les propriétés suivantes, définissant les URLs des environnements gérés, sont manquantes : " + adressesManquantes;
				Logger.getLogger(Metier.class).fatal(msg);
				throw new Exception(msg);
			}
		}
		// Mono-environnement : Vérification de la présence de l'URL MOS pour l'environnement si celui par défaut est absent
		else if (url == null || url.trim().isEmpty()) {
			String environnement = EnvironnementHelper.getEnvironnements().get(0);
			url = GlobalProperties.getGlobalProperty("fr.cimut.mos.skeleton.address");
			if (url == null || url.trim().isEmpty()) {
				throw new NullPointerException("la donnee 'fr.cimut.mos.skeleton.address' n a pas ete trouvee dans les fichiers de properties!!!");
			}
		}

		// Vérification de la présence de la valeur du timeout
		try {
			Long.parseLong(GlobalProperties.getGlobalProperty("fr.cimut.mos.stub.timeout").trim());
		} catch (NullPointerException exception) {
			throw new NullPointerException("la donnee 'fr.cimut.mos.stub.timeout' n a pas ete trouvee dans les fichiers de properties!!!");
		} catch (NumberFormatException exception) {
			throw new NullPointerException("la donnee 'fr.cimut.mos.stub.timeout' est mal formee!!!");
		}

		// Vérification de la présence de la valeur du poolsize
		try {
			Integer.parseInt(GlobalProperties.getGlobalProperty("fr.cimut.mos.stub.nbConnection"));
		} catch (NullPointerException exception) {
			throw new NullPointerException("la donnee 'fr.cimut.mos.stub.nbConnection' n a pas ete trouvee dans les fichiers de properties!!!");
		} catch (NumberFormatException exception) {
			throw new NullPointerException("la donnee 'fr.cimut.mos.stub.nbConnection' est mal formee!!!");
		}

		Logger.getLogger(Metier.class).info("Vérification des variables de connection MOS - OK");

		try {
			Logger.getLogger(Metier.class).debug("Initialisation KeyedObjectPool");
			KeyedObjectPool.init(this.getClass());
		} catch (Exception exception) {
			Logger.getLogger(Metier.class).fatal(
					"Le pool de connection jdbc ne c est pas ouvert correctement. L api fr.cimut.habilitation.server n est pas utilisable pour l application en cours.",
					exception);
		}

		try {
			Logger.getLogger(Metier.class).debug("Initialisation log4j");

			String file = null;
			long periode = 0;
			try {
				file = GlobalProperties.getGlobalProperty("fr.cimut.editique.purge.log4j.path");
			} catch (NullPointerException exception) {
				throw new NullPointerException(
						"la donnee 'fr.cimut.editique.purge.log4j.path' n a pas ete trouvee dans les fichiers de properties!!!");
			} catch (NumberFormatException exception) {
				throw new NullPointerException("la donnee 'fr.cimut.editique.purge.log4j.path' est mal formee!!!");
			}

			try {
				periode = Integer.parseInt(GlobalProperties.getGlobalProperty("fr.cimut.editique.purge.log4j.interval"));
			} catch (NullPointerException exception) {
				throw new NullPointerException(
						"la donnee 'fr.cimut.editique.purge.log4j.interval' n a pas ete trouvee dans les fichiers de properties!!!");
			} catch (NumberFormatException exception) {
				throw new NullPointerException("la donnee 'fr.cimut.editique.purge.log4j.interval' est mal formee!!!");
			}

			DOMConfigurator.configureAndWatch(file, periode);

		} catch (Exception e) {
			Logger.getLogger(Metier.class)
					.fatal("L'init de log4j n'est pas correctement faite . La GED entrant n est pas utilisable pour l application en cours.", e);
			throw e;
		}

	}

	@PreDestroy
	public void Detruire() {
		try {
			// on ferme le pool si necessaire
			if (GenericObjectPool.isPoolActif()) {
				GenericObjectPool.stopPool();
				Logger.getLogger(Metier.class).info("pool Mos closed");
			}
		} catch (Exception e) {
			Logger.getLogger(Metier.class).fatal("Erreur lors de la fermeture du GenericObjectPool", e);
		}
		try {
			KeyedObjectPool.getInstance().close();
		} catch (Exception exception) {
			Logger.getLogger(Metier.class).fatal("Le pool de connection jdbc ne c est pas ferme correctement.", exception);
		}

		try {
			MetierManager.closeAll();
		} catch (Exception e) {
			Logger.getLogger(Metier.class).error("Le pool de connection jdbc ne c est pas ferme correctement.", e);
		}

		struManager = null;
		partManager = null;
		alphManager = null;
		eddmManager = null;
		sudeManager = null;
	}

}
