package fr.cimut.ged.entrant.integration.ejb;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.beans.IntegrationReport;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutXmlException;
import fr.cimut.ged.entrant.service.Manager;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.FileHelper;

/**
 * Session Bean implementation class Pooler
 */
@TransactionAttribute(value = TransactionAttributeType.NEVER)
@Singleton(mappedName = "PoolerDirectory")
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PoolerDirectory {

	private Logger LOGGER = Logger.getLogger(this.getClass());

	@EJB
	Manager manager;

	/**
	 * Default constructor.
	 */
	public PoolerDirectory() {

	}
	
	@Schedule(minute = "*", hour = "*", dayOfWeek = "Mon-Fri", persistent = false)
	@Lock(LockType.WRITE)
	public void checkNewEntryMultiple() {
		// Récupération de la liste des environnements gérés
		List<String> environnements;
		try {
			environnements = EnvironnementHelper.getEnvironnements();
		} catch (CimutConfException e) {
			Logger.getLogger(PoolerDirectory.class).fatal(e.getMessage());
			return;
		}

		// Effectue le traitement pour chacun des environnements gérés
		for (String environnement : environnements) {
			Date date = new Date();

			int errorCounter = 0;

			Map<File, List<Map<String, Object>>> map = null;

			try {
				map = FileHelper.listXmlFileMultiple(environnement);
			} catch (CimutFileException e) {
				Logger.getLogger(PoolerDirectory.class).fatal("unable to list content of folder for environment" + environnement, e);
				continue;
			} catch (CimutConfException e) {
				Logger.getLogger(PoolerDirectory.class).fatal(e.getMessage());
				continue;
			} catch (CimutXmlException e) {
				Logger.getLogger(PoolerDirectory.class).fatal(e.getMessage());
				continue;
			}

			for (Map.Entry<File, List<Map<String, Object>>> entry : map.entrySet()) {
				File xml = entry.getKey();
				List<Map<String, Object>> listPdfs = entry.getValue();
				List<IntegrationReport> reports = null;
				try {
					reports = manager.addDocsFromPoolerDirectory(xml, listPdfs, environnement);
					LOGGER.info("Intégration de document : " + reports.toString());
				} catch (Exception e) {
					Logger.getLogger(PoolerDirectory.class).error(e.getMessage());
					errorCounter++;
					reports.add(new IntegrationReport(xml.toString(), false, e.getMessage()));
					LOGGER.error("Intégration de document : " + reports.toString());
				}
			}

			if (!map.isEmpty()) {
				String message = "Traitement realise avec " + errorCounter + " erreur(s) sur un total de " + map.size() + " documents en "
						+ ((new Date().getTime() - date.getTime())) + " Msecs pour l'environnement " + environnement;
				Logger.getLogger(PoolerDirectory.class).info(message);
			}

			if (errorCounter > 0) {
				Logger.getLogger(PoolerDirectory.class)
						.fatal("Des documents n'ont pas été integré correctement dans la GED pour l'environnement " + environnement);
			}
		}
	}
}