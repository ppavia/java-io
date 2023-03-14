package fr.cimut.ged.entrant.integration.ejb;

import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import org.hibernate.*;

import javax.ejb.*;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Session Bean implementation class Process
 */
@SuppressWarnings("JpaQueryApiInspection") @Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class Rebuild implements RebuildRemote {

	private final static int NUM_MAX_OCCURS = 10000;

	@PersistenceContext(unitName = "MyPersistence")
	private Session session;

	/**
	 * Reconstruit la base MongoDB à partir des JSON stockés dans Oracle
	 * 
	 * @param organisme
	 * @param envir
	 * @return
	 */
	@Override
	@Lock(LockType.WRITE)
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	//@TransactionTimeout(value = 600, unit = TimeUnit.SECONDS)
	public String rebuild(String organisme, String envir) {

		if (!organisme.matches("^\\d{4}$")) {
			return "Organisme invalide : " + organisme;
		}

		// Vérification de la validité de l'environnement
		String environnement = null;
		try {
			environnement = EnvironnementHelper.determinerEnvironnement(envir);
		} catch (CimutConfException e) {
			return "Erreur de configuration : " + e.getMessage();
		}
		if (environnement == null) {
			return "Environnement invalide (non géré) : " + envir;
		}

		StringBuilder message = new StringBuilder();

		session.setFlushMode(FlushMode.MANUAL);
		session.setCacheMode(CacheMode.IGNORE);
		ScrollableResults sr = null;

		Query query = session.getNamedQuery("Json.rebuild");
		query.setParameter("organisme", organisme);

		// /!\ ICI ON A UN GROS PROBLEME : Il faudrait cloisonner par envirronnment
		//@NamedQuery(name="Json.rebuild", query="select e from  Json e, Document d where  e.organisme = :organisme and d.sidstar = :env and d.id = e.id")
		// mais en production, on est bon pour aller au casse pipe.
		// alors on touche a rien ... on comment et on met ca en rouge dans la doc
		//query.setParameter("env", envir);
		query.setCacheMode(CacheMode.IGNORE);
		query.setCacheable(false);

		int counter = 0;
		int counterFile = 0;
		List<String> listJson = new ArrayList<String>();

		try {

			sr = query.scroll(ScrollMode.FORWARD_ONLY);

			while (sr.next()) {
				Json json = (Json) sr.get(0);
				counter++;
				if (counter % NUM_MAX_OCCURS == 0) {
					String filePath = GlobalVariable.getIndexationPath(environnement) + "add" + File.separator + organisme + "_Rebuild" + "_"
							+ (counterFile++) + ".json";
					try {
						listIntoFile(listJson, filePath);
					} catch (CimutFileException e) {
						message.append(e.getMessage());
					}
					listJson.clear();
				}
				listJson.add(json.getData());
				if (counter % 500 == 0) {
					session.clear();
				}

			}

			if (!listJson.isEmpty()) {
				String filePath = GlobalVariable.getIndexationPath(environnement) + "add" + File.separator + organisme + "_Rebuild" + "_"
						+ (counterFile++) + ".json";
				try {
					listIntoFile(listJson, filePath);
				} catch (CimutFileException e) {
					message.append(e.getMessage());
				}
			}

		} catch (Exception e) {
			message.append(e.getMessage());
		} finally {
			if (sr != null) {
				sr.close();
			}
		}

		if (message.length() == 0) {
			message.append("OK");
		}
		return message.toString();
	}

	/**
	 * Write in UTF8 the json list into the specified filename
	 * 
	 * @param listJson
	 * @param fileName
	 * @throws CimutFileException
	 */
	private void listIntoFile(List<String> listJson, String fileName) throws CimutFileException {

		if (listJson.isEmpty())
			return;
		Writer out = null;

		int last = listJson.size() - 1;
		String lastOccurence = listJson.get(last);
		listJson.remove(last);
		try {

			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
			out.write("[");
			for (String str : listJson) {
				out.write(str + ",\n");
			}
			out.write(lastOccurence + "]");
		} catch (Exception e) {
			throw new CimutFileException(e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {

			}
		}
	}
}
