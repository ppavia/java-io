
package fr.cimut.ged.entrant.indexation.ejb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Date;
import java.util.List;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.service.MongoConnection;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.mongo.FileHelper;

/**
 * Session Bean implementation class Pooler
 */

@Singleton
@TransactionAttribute(value = TransactionAttributeType.NEVER)
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class PoolerMgDB {

	@EJB
	MongoConnection mongoConnection;

	/**
	 * Default constructor.
	 */
	public PoolerMgDB() {

	}

	@Lock(LockType.WRITE)
	@Schedule(minute = "*", hour = "*", persistent = false)
	public void process() {
		// Récupération de la liste des environnements gérés
		List<String> environnements;
		try {
			environnements = EnvironnementHelper.getEnvironnements();
		} catch (CimutConfException e) {
			Logger.getLogger(PoolerMgDB.class).fatal(e.getMessage());
			return;
		}

		// Effectue le traitement pour chacun des environnements gérés
		for (String environnement : environnements) {
			delete(environnement);
			insert(environnement);
			update(environnement);
		}
	}

	private void delete(String environnement) {
		Logger logger = Logger.getLogger(PoolerMgDB.class);
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Pooler delete start");
		}

		//suppresion dans mongo des json contenu dans le repertoire d'entree
		try {

			File[] jsons = FileHelper.listJsonFile(GlobalVariable.getIndexationPathDeleteEntree(environnement));
			if (jsons.length > 0) {

				// integration dans mongo
				String idDelete = "";
				String repertoire_erreur = GlobalVariable.getIndexationPathDeleteSortie(environnement);
				String name = "";

				for (File jsonfile : jsons) {
					//pour eviter les erreurs de cas d'appel simultanee 
					if (!jsonfile.exists()) {
						return;
					}

					try {
						name = jsonfile.getName();
						idDelete = name.substring(name.indexOf("_") + 1, name.length() - 5);
						if (logger.isEnabledFor(Level.INFO)) {
							logger.info("Suppression du document ID_DOC = " + idDelete);
						}
					} catch (Exception exception) {
						logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n est pas nomme selon la norme");
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						continue;
					}

					try {
						//suppression de mongo
						// Le nom de la base est le nom de l'environnement
						mongoConnection.getMongoClient().delete(idDelete, environnement);
						//suppression du fichier
						if (jsonfile.delete()) {
							if (logger.isEnabledFor(Level.INFO)) {
								logger.info("Le fichier " + jsonfile.getAbsolutePath() + " est supprime (OK)");
							}
						} else {
							logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n a pas pu etre supprime");
						}
					} catch (Exception exception) {
						//deplacer le fichier dans les erreurs
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						logger.error(exception.getMessage(), exception);
					}
				}
			}
		} catch (Exception e4) {
			logger.fatal(e4.getMessage(), e4);
		}

		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Pooler delete ended");
		}
	}

	private void update(String environnement) {
		Logger logger = Logger.getLogger(PoolerMgDB.class);
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Pooler update start");
		}
		//insertion dans mongo des json contenu dans le repertoire d'entree
		InputStreamReader flog = null;
		LineNumberReader llog = null;
		String myLine = null;
		StringBuilder json = null;
		boolean deletejson = false;

		try {
			String repertoire_erreur = GlobalVariable.getIndexationPathUpdateSortie(environnement);

			File[] jsons = FileHelper.listJsonFile(GlobalVariable.getIndexationPathUpdateEntree(environnement));

			if (jsons != null && jsons.length > 0) {
				// integration dans mongo
				String name = "";
				String idUpdate = "";
				for (File jsonfile : jsons) {
					//pour eviter les erreurs de cas d'appel simultanee 
					if (!jsonfile.exists()) {
						return;
					}

					try {
						json = new StringBuilder();
						flog = new InputStreamReader(new FileInputStream(jsonfile.getAbsolutePath()));
						llog = new LineNumberReader(flog);
						while ((myLine = llog.readLine()) != null) {
							json = json.append(myLine);
						}
					} catch (Exception exception) {
						logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n est pas nomme selon la norme");
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						continue;
					} finally {
						try {
							llog.close();
							flog.close();
						} catch (Exception e) {
						}
					}

					try {
						name = jsonfile.getName();
						idUpdate = name.substring(name.indexOf("_") + 1, name.length() - 5);
						if (logger.isEnabledFor(Level.INFO)) {
							logger.info("Mise a jour du document ID_DOC = " + idUpdate);
						}
					} catch (Exception exception) {
						logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n est pas nomme selon la norme");
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						continue;
					}

					try {
						//Mise a jour dans mongo
						// Le nom de la base est le nom de l'environnement
						mongoConnection.getMongoClient().update(idUpdate, json.toString(), environnement);
						//suppression du fichier
						deletejson = jsonfile.delete();
						if (deletejson) {
							if (logger.isEnabledFor(Level.INFO)) {
								logger.info("Le fichier " + jsonfile.getAbsolutePath() + " est supprime(OK)");
							}
						} else {
							logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n a pas pu etre supprime (update)");
						}
					} catch (Exception e) {
						//deplacer le fichier dans les erreurs
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						logger.error("Erreur durant le traitement du fichier " + jsonfile.getAbsolutePath() + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e4) {
			logger.fatal(e4.getMessage(), e4);
		}
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Pooler update ended");
		}
	}

	private void insert(String environnement) {
		Logger logger = Logger.getLogger(PoolerMgDB.class);
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Pooler insert start");
		}

		// insertion dans mongo des json contenu dans le repertoire d'entree
		BufferedReader brJson = null;
		String line = "";

		try {
			String repertoire_erreur = GlobalVariable.getIndexationPathImportSortie(environnement);

			File[] jsons = FileHelper.listJsonFile(GlobalVariable.getIndexationPathImportEntree(environnement));
			if (jsons.length > 0) {

				// integration dans mongo
				for (File jsonfile : jsons) {

					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("Insertion du fichier " + jsonfile.getAbsolutePath());
					}

					// pour eviter les erreurs de cas d'appel simultanee
					if (!jsonfile.exists()) {
						continue;
					}

					StringBuilder json = new StringBuilder();

					try {
						brJson = new BufferedReader(new FileReader(jsonfile.getAbsolutePath()));
						line = brJson.readLine();
						while (line != null) {
							json.append(line);
							json.append("\n");
							line = brJson.readLine();
						}
					} catch (Exception exception) {
						logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n a pu etre lu");
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						continue;
					} finally {
						try {
							brJson.close();
						} catch (Exception e) {
							logger.error("commentaire manquant", e);
						}
					}

					// integration dans mongo
					try {
						long time = new Date().getTime();
						// Le nom de la base est le nom de l'environnement
						mongoConnection.getMongoClient().insert(json.toString(), environnement);
						logger.info("insert file in " + (new Date().getTime() - time) + "ms");

						// suppression du fichier
						if (jsonfile.delete()) {
							if (logger.isEnabledFor(Level.INFO)) {
								logger.info("Le fichier " + jsonfile.getAbsolutePath() + " est supprime(OK)");
							}
						} else {
							logger.error("Le fichier " + jsonfile.getAbsolutePath() + " n a pas pu etre supprime");
						}

					} catch (CimutConfException e) {
						// deplacer le fichier dans les erreurs
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						logger.error(e.getMessage(), e);
						break;
					} catch (Exception e) {
						// deplacer le fichier dans les erreurs
						FileHelper.deplacement(jsonfile, repertoire_erreur);
						logger.error("Erreur pour le fichier : " + jsonfile.getAbsolutePath() + " " + e.getMessage(), e);
					}
				}
			}
		} catch (Exception e4) {
			logger.fatal(e4.getMessage(), e4);
		}
		if (logger.isEnabledFor(Level.DEBUG)) {
			logger.debug("Pooler insert ended");
		}
	}

}
