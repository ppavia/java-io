package fr.cimut.ged.entrant.mongo;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceOutput;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.mongo.DocumentQueryBuilder;

public class InteractionMongo {

	private List<ReferenceMongo> referentielMongo;

	private Logger logger = Logger.getLogger(this.getClass());

	private List<String> SKIP = Arrays.asList(GlobalVariable.ATTR_ASSU_INSEE, GlobalVariable.ATTR_ASSU_INSEE, GlobalVariable.ATTR_ATTRIBUTS,
			GlobalVariable.ATTR_CODE_POSTAL, GlobalVariable.ATTR_COMMENTAIRES, GlobalVariable.ATTR_DA_ID, GlobalVariable.ATTR_DATE_CREATION,
			GlobalVariable.ATTR_DEPARTEMENT, GlobalVariable.ATTR_DTINTEGRATION, GlobalVariable.ATTR_EDDOC_ID, GlobalVariable.ATTR_ERROR_DA,
			GlobalVariable.ATTR_ERROR_EDDM, GlobalVariable.ATTR_ID, GlobalVariable.ATTR_ID_ENTREPRISE, GlobalVariable.ATTR_ID_SYSENTREPRISE,
			GlobalVariable.ATTR_MAILED, GlobalVariable.ATTR_ID_PROF, GlobalVariable.ATTR_NOM_DE_FAMILLE, GlobalVariable.ATTR_NOM_ENTREPRISE,
			GlobalVariable.ATTR_NUM_ENTREPRISE, GlobalVariable.ATTR_NUM_ADHERENT, GlobalVariable.ATTR_ORGANISME, GlobalVariable.ATTR_PRENOM,
			GlobalVariable.DATE_NAISSANCE_PATIENT, GlobalVariable.ATTR_REGION, GlobalVariable.ATTR_EDDOC_IDS, "_id", "ADRESSE1", "ADRESSE2",
			"ADRESSE3", "ADRESSE4", "ADRESSE5", "ADRESSE6", "ADRESSE7");

	/**
	 * <p>
	 * Constructeur permettant d'initialiser un objet de type InteractionMongo.
	 * </p>
	 */
	public InteractionMongo() {
		this.setReferentielMongo(new ArrayList<ReferenceMongo>());
	}

	private MongoClient newConnection(String nom_base) throws CimutMongoDBException, CimutConfException {

		MongoClient mongoClient = null;
		String user = GlobalVariable.getUser();
		String password = GlobalVariable.getPassword();
		List<ServerAddress> addrList = new ArrayList<ServerAddress>();
		try {
			for (String s : GlobalVariable.getRsServersHost().split(",")) {
				addrList.add(new ServerAddress(s));
			}
		} catch (UnknownHostException e) {
			throw new CimutConfException(e);
		}

		try {
			MongoCredential credential = MongoCredential.createMongoCRCredential(user, nom_base, password.toCharArray());
			mongoClient = new MongoClient(addrList, Arrays.asList(credential));
			DB base = this.getBase(mongoClient, nom_base);
			this.getReferentielMongo().add(new ReferenceMongo(nom_base, base, mongoClient));
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
		return mongoClient;
	}

	/**
	 * <p>
	 * Fonction retournant la base en fonction de son nom.
	 * </p>
	 * 
	 * @param mongoClient
	 * @param nom_base
	 * 
	 * @return la base
	 * @throws CimutMongoDBException
	 */
	private DB getBase(MongoClient mongoClient, String nom_base) throws CimutMongoDBException {
		DB db = null;

		if (mongoClient == null) {
			throw new CimutMongoDBException("Le client mongo est null");
		}

		try {
			db = mongoClient.getDB(nom_base);
			if (db == null) {
				throw new CimutMongoDBException("La base " + nom_base + " n existe pas ");
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
		return db;
	}

	/**
	 * <p>
	 * Fonction retournant le MongoClient s il est present dans le referenciel sinon retourne null
	 * </p>
	 * 
	 * @param nom_base
	 * 
	 * @return MongoClient
	 * @throws CimutConfException
	 * @throws CimutMongoDBException
	 * @throws NumberFormatException
	 */
	private MongoClient getMongoClient(String nom_base) throws CimutMongoDBException, CimutConfException {
		//si la collection existe dans la base
		MongoClient mongoClient = null;

		for (ReferenceMongo ref : this.getReferentielMongo()) {
			if (nom_base.equalsIgnoreCase(ref.getCmroc())) {
				mongoClient = ref.getMongoClient();
				break;
			}
		}

		if (mongoClient == null) {
			mongoClient = newConnection(nom_base);
		}

		return mongoClient;
	}

	/**
	 * <p>
	 * Fonction retournant la collection en fonction de la base et du nom de la collection
	 * </p>
	 * 
	 * @param mongoClient
	 * @param db
	 * @param nom_collection
	 * 
	 * @return DBCollection
	 * @throws CimutMongoDBException
	 */
	private DBCollection getCollection(MongoClient mongoClient, DB db, String nom_collection) throws CimutMongoDBException {
		DBCollection collection = null;

		try {
			if (db.collectionExists(nom_collection)) {
				collection = db.getCollection(nom_collection);
			} else {
				collection = db.createCollection(nom_collection, null);
				//throw (new  CimutMongoDBException("La collection "+ nom_collection + " n existe pas dans la base " + db.getName()));
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}

		return collection;

	}

	/**
	 * <p>
	 * Fonction retournant la collection si elle presente dans le referenciel sinon il la creer
	 * </p>
	 * 
	 * @param nom_base
	 * @param collection_name
	 * @return DBCollection
	 * @throws CimutConfException
	 * @throws CimutMongoDBException
	 * @throws NumberFormatException
	 */
	public DBCollection getCollection(String nom_base, String collection_name)
			throws NumberFormatException, CimutMongoDBException, CimutConfException {
		//si la collection existe dans la base
		MongoClient mongoClient = getMongoClient(nom_base);
		return getCollection(mongoClient, mongoClient.getDB(nom_base), collection_name);
	}

	/**
	 * Retourne la collection Documents de la base Mongo correspondant à l'environnement fourni
	 * 
	 * @param environnement
	 * @return
	 * @throws CimutMongoDBException
	 * @throws CimutConfException
	 */
	private DBCollection getCollectionDocuments(String environnement) throws CimutMongoDBException, CimutConfException {
		String nomBase = getNomBaseFromEnvironnement(environnement);
		return getCollection(nomBase, GlobalVariable.getDBCollection());
	}

	/** Retourne le nom de la base à utiliser pour l'environnement donné */
	private String getNomBaseFromEnvironnement(String environnement) {
		return GlobalVariable.NOM_BASE_PREFIXE + environnement;
	}

	/**
	 * <p>
	 * Fonction retournant la base si elle presente dans le référenciel sinon retourne null
	 * </p>
	 * 
	 * @param String
	 *            nom_base
	 * 
	 * @return DB
	 * @throws CimutConfException
	 * @throws CimutMongoDBException
	 */
	private DB getBase(String nom_base) throws CimutMongoDBException, CimutConfException {
		//si la collection existe dans la base
		DB base = null;

		getMongoClient(nom_base);

		for (ReferenceMongo ref : this.getReferentielMongo()) {
			if (nom_base.equalsIgnoreCase(ref.getCmroc())) {
				base = ref.getDatabase();
				break;
			}
		}

		if (base == null) {
			throw new CimutMongoDBException("impossible de recuperer la base : " + nom_base);
		}
		return base;
	}

	/**
	 * <p>
	 * Fonction permettant d'inserer un document ou un ensemble de document dans mongo.
	 * </p>
	 * 
	 * @param json
	 * @param environnement
	 * 
	 * @throws CimutMongoDBException
	 * @throws CimutConfException
	 * @throws NumberFormatException
	 */
	public void insert(String json, String environnement) throws CimutMongoDBException, NumberFormatException, CimutConfException {
		//insertion du document
		insert(json, getCollectionDocuments(environnement));
	}

	/**
	 * <p>
	 * Fonction permettant d'inserer un document ou un ensemble de document dans mongo.
	 * </p>
	 * 
	 * @param String
	 *            json
	 * @param DBCollection
	 *            collection
	 * 
	 * @throws CimutMongoDBException
	 */
	private void insert(String json, DBCollection collection) throws CimutMongoDBException {

		try {
			if (json.startsWith("[")) {
				ArrayList<DBObject> listdbObjest = ((ArrayList<DBObject>) JSON.parse(json));
				//insert du document dans la collection
				WriteResult writeresult = collection.insert(listdbObjest);

				// test de resultat de l insert
				int result = (Integer) writeresult.getField("ok");

				if (result == 1) {
					//Insert du fichier json ok
				} else {
					throw new CimutMongoDBException("Insert du fichier json en erreur " + writeresult.getError());
				}

			} else {

				DBObject dbObject = (DBObject) JSON.parse(json);

				//insert du document dans la collection
				WriteResult writeresult = collection.insert(dbObject);

				// test de resultat de l insert
				int result = (Integer) writeresult.getField("ok");

				if (result != 1) {
					throw new CimutMongoDBException("Insert du fichier json en erreur " + writeresult.getError());
				}
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fonction permettant de supprimer un document dans mongo.
	 * </p>
	 * 
	 * @param id_delete
	 * @param environnement
	 * 
	 * @throws CimutMongoDBException
	 */
	public void delete(String id_delete, String environnement) throws CimutMongoDBException {
		try {
			//insertion du document
			delete(id_delete, getCollectionDocuments(environnement));
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fonction permettant de supprimer un document dans mongo.
	 * </p>
	 * 
	 * @param String
	 *            identifiant du ID_DOC du document a supprimer
	 * @throws CimutMongoDBException
	 * 
	 */
	private void delete(String id_delete, DBCollection collection) throws CimutMongoDBException {
		try {
			WriteResult writeresult = collection.remove(new BasicDBObject("ID_DOC", id_delete));

			int result = (Integer) writeresult.getField("ok");
			if (result == 1) {
				int nbrsup = (Integer) writeresult.getField("n");
				if (nbrsup == 1) {
				} else {
					if (nbrsup == 0) {
						throw new CimutMongoDBException("Suppression de mongo impossible (ID_DOC " + id_delete + " ) n 'existe pas");
					} else {
						throw new CimutMongoDBException(
								"Suppression multiple de mongo (ID_DOC " + id_delete + " )  est supprime " + nbrsup + " fois");
					}
				}
			} else {
				throw new CimutMongoDBException("Suppression de mongo en erreur (ID_DOC " + id_delete + " ) " + writeresult.getError());
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fonction permettant d'inserer un document ou un ensemble de documents dans mongo.
	 * </p>
	 * 
	 * @param id_update
	 * @param json
	 * @param environnement
	 * 
	 * @throws CimutMongoDBException
	 */
	public void update(String id_update, String json, String environnement) throws CimutMongoDBException {
		try {
			//update du document
			update(id_update, json, getCollectionDocuments(environnement));
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fonction permettant de mettre a jour des documents dans mongo.
	 * </p>
	 * 
	 * @param String
	 *            id_update identifiant du ID_DOC du document a mettre a jour
	 * @param String
	 *            json document json
	 * @param DBCollection
	 *            collection
	 * 
	 * @throws CimutMongoDBException
	 * 
	 */
	private void update(String id_update, String json, DBCollection collection) throws CimutMongoDBException {
		//upsert
		// When you specify upsert: true for an update operation to replace a document and no matching documents are found, MongoDB creates a new document
		//using the equality conditions in the update conditions document, and replaces this document, except for the _id field if specified, 
		//with the update document.
		// attention au risque de doublons si  upsert n est pas false

		try {
			Boolean upsert = false;

			BasicDBObject docCriteres = new BasicDBObject("ID_DOC", id_update);
			DBObject docMaj = (DBObject) JSON.parse(json);

			WriteResult writeresult = collection.update(docCriteres, docMaj, upsert, false);
			//4eme critere = multi
			//  false = 1 seul document est mis a jour
			//  true  = plusieurs documents sont mis ajour

			// test de resultat de l update
			Boolean resultupdatedExisting = (Boolean) writeresult.getField("updatedExisting");
			if (upsert) {
				//Update du fichier document  ok 	
			} else {
				if (resultupdatedExisting) {
				} else {
					throw new CimutMongoDBException("Update du document ( ID_DOC = " + id_update
							+ " ) en erreur. Le document n existe pas en base. Aucune operation d effectue. ");
				}
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fonction permettant de rechercher des documents dans mongo.
	 * </p>
	 * 
	 * @throws CimutMongoDBException
	 */
	public String getList(String environnement, Map<String, String> parameters) throws CimutMongoDBException {

		DBCursor cursor = null;

		logger.info("parameters " + parameters.toString());

		try {

			BasicDBObject query;
			if (parameters.containsKey(GlobalVariable.RULE_ID) && !parameters.get(GlobalVariable.RULE_ID).isEmpty()) {
				String cmroc = parameters.get(GlobalVariable.ATTR_ID_ORGANISME);
				Manager<Rule> ruleManager = MongoManagerFactory.getRuleManager(environnement, cmroc, this);
				Rule rule = ruleManager.get(parameters.get(GlobalVariable.RULE_ID));
				query = DocumentQueryBuilder.getQueryFromRule(rule);
				if (parameters.containsKey(GlobalVariable.RULE_DATE) && !parameters.get(GlobalVariable.RULE_DATE).isEmpty()) {
					query.put(GlobalVariable.RULE_DATE, parameters.get(GlobalVariable.RULE_DATE));
				}

				// Utile pour ne pas lister les documents provenant d'une régle qui ne peux s'appliquer sur les mails entrants !
				if (!query.containsField(GlobalVariable.ATTR_DESTINATAIRE)) {
					query.put(GlobalVariable.ATTR_DESTINATAIRE, new BasicDBObject("$exists", false));
				}

			} else {
				query = DocumentQueryBuilder.getSearchQuery(parameters);
			}

			cursor = getCollectionDocuments(environnement).find(query).sort(DocumentQueryBuilder.getSort(parameters))
					.limit(DocumentQueryBuilder.getLimit(parameters)).skip(DocumentQueryBuilder.getSkip(parameters));

			logger.info("QUERY : " + cursor.getQuery().toString());

		} catch (NumberFormatException e) {
			throw new CimutMongoDBException("Erreur lors de la recherche [NumberFormatException]", e);
		} catch (CimutConfException e) {
			throw new CimutMongoDBException("Erreur lors de la recherche [CimutConfException]", e);
		}
		return DocumentQueryBuilder.convertDBCursorToString(cursor);
	}

	public String getStats(String environnement, Map<String, String> parameters) throws CimutMongoDBException {
		AggregationOutput aggregationOutput = null;
		try {
			aggregationOutput = getCollectionDocuments(environnement).aggregate(DocumentQueryBuilder.getStatsQuery(parameters));
		} catch (NumberFormatException e) {
			throw new CimutMongoDBException("Erreur lors de la recherche [NumberFormatException]", e);
		} catch (CimutConfException e) {
			throw new CimutMongoDBException("Erreur lors de la recherche [CimutConfException]", e);
		}
		return DocumentQueryBuilder.convertAggregationOutputToString(aggregationOutput);
	}

	/**
	 * <p>
	 * Fonction permettant de supprimer l'ensemble des documents de la collection mongo etant initialise dans le
	 * constructeur.
	 * </p>
	 * 
	 * @param environnement
	 * 
	 * @throws CimutMongoDBException
	 */
	public void removeCollection(String environnement) throws CimutMongoDBException {
		try {
			removeCollection(getCollectionDocuments(environnement));
		} catch (Exception e) {
			throw new CimutMongoDBException("Erreur de recherche de mongo " + e.getMessage(), e);
		}

	}

	/**
	 * <p>
	 * Fonction permettant de supprimer l'ensemble des documents de la collection mongo etant initialise dans le
	 * constructeur.
	 * </p>
	 * 
	 * @throws CimutMongoDBException
	 */
	private void removeCollection(DBCollection collection) throws CimutMongoDBException {
		//si la collection existe dans la base
		WriteResult writeresult = null;

		try {
			if (collection != null) {
				writeresult = collection.remove(new BasicDBObject());
			}

			int result = (Integer) writeresult.getField("ok");
			if (result == 1) {
				int nbrsup = (Integer) writeresult.getField("n");

				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Suppression des documents de la collection OK : " + nbrsup + " documents supprimes");
				}
			} else {
				throw new CimutMongoDBException("Vidage de la collection en erreur " + writeresult.getError());
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * <p>
	 * Fonction permettant de reparer/compacter une base.
	 * </p>
	 * 
	 * @param environnement
	 * 
	 * @throws CimutMongoDBException
	 */
	public void repairDatabase(String environnement) throws CimutMongoDBException {
		try {
			DB base = getBase(getNomBaseFromEnvironnement(environnement));
			repairDatabase(base);
		} catch (Exception e) {
			throw (new CimutMongoDBException(e.getMessage()));
		}

	}

	/**
	 * <p>
	 * Fonction permettant de reparer/compacter une base.
	 * </p>
	 * 
	 * @throws CimutMongoDBException
	 */
	private void repairDatabase(DB database) throws CimutMongoDBException {
		try {
			CommandResult resultCommandResult = database.command(new BasicDBObject("repairDatabase", 1));

			if (!resultCommandResult.ok()) {
				throw (new CimutMongoDBException("Vidage de la collection en erreur " + resultCommandResult.getErrorMessage()));
			} else {
				if (logger.isEnabledFor(Level.INFO)) {
					logger.info("Reparation de la base OK ");
				}
			}
		} catch (Exception e) {
			throw (new CimutMongoDBException(e.getMessage()));
		}

	}

	/**
	 * <p>
	 * Fonction qui ferme toutes les connexions effectuees.
	 * </p>
	 * 
	 * @throws CimutMongoDBException
	 */
	public void closeConnexion() throws CimutMongoDBException {
		ReferenceMongo ref = null;

		try {
			int nbr_connexion = this.getReferentielMongo().size();

			while (!getReferentielMongo().isEmpty()) {

				ref = this.getReferentielMongo().get(0);
				ref.getMongoClient().close();
				boolean test = getReferentielMongo().remove(ref);

				if (!test) {
					if (logger.isEnabledFor(Level.ERROR)) {
						logger.info("Erreur a la fermeture de la connexion ");
					}
				} else {
					if (logger.isEnabledFor(Level.INFO)) {
						logger.info("Fermeture de la connexion OK ");
					}
				}

			}

			if (logger.isEnabledFor(Level.INFO)) {
				logger.info("Fermeture des " + nbr_connexion + " connexions OK ");
			}
		} catch (Exception e) {
			throw (new CimutMongoDBException(e.getMessage()));
		}
	}

	private List<ReferenceMongo> getReferentielMongo() {
		return referentielMongo;
	}

	private void setReferentielMongo(List<ReferenceMongo> referentielMongo) {
		this.referentielMongo = referentielMongo;
	}

	/**
	 * <p>
	 * Fonction permettant de lister les bases.
	 * </p>
	 * 
	 * @param String
	 *            user
	 * @param String
	 *            password
	 * @param String
	 *            nom_base
	 * @param String
	 *            nom_serveur
	 * @param int
	 *            serveur_port
	 * 
	 * @throws CimutMongoDBException
	 */
	public List<String> getBases(String environnement) throws CimutMongoDBException {

		List<String> dbs = null;

		try {
			String nomBase = getNomBaseFromEnvironnement(environnement);
			MongoClient mongoClient = this.getMongoClient(nomBase);
			dbs = getBases(mongoClient);
		} catch (Exception e) {
			throw (new CimutMongoDBException(e.getMessage()));
		}

		return dbs;

	}

	/**
	 * <p>
	 * Fonction retournant la liste des base.
	 * </p>
	 * 
	 * @return List<String> la liste des bases
	 * 
	 * @throws CimutMongoDBException
	 */
	private List<String> getBases(MongoClient mongoClient) throws CimutMongoDBException {
		List<String> dbs = null;
		try {
			dbs = mongoClient.getDatabaseNames();
		} catch (Exception e) {
			throw (new CimutMongoDBException(e.getMessage()));
		}
		return dbs;
	}

	public List<String> getInitCritereList() {
		List<String> list = new ArrayList<String>();
		list.addAll(GlobalVariable.LISTE_CRITERES);
		list.add("REGION");
		list.add("DEPARTEMENT");
		return list;
	}

	/**
	 * Initialise les critères de sélection pour le CMROC donné
	 * 
	 * @param collectionParam
	 * @param cmroc
	 * @return La liste des critères de sélection
	 */
	@SuppressWarnings("unchecked")
	public List<String> initCritere(DBCollection collectionParam, String cmroc) {
		List<Object> list = new BasicDBList();
		list.addAll(getInitCritereList());
		collectionParam.update(new BasicDBObject("_id", "CRITERE"), new BasicDBObject("$set", new BasicDBObject(cmroc, list)), true, false);
		return (List<String>) (List<?>) (BasicDBList) list;
	}

	/**
	 * Crée/Met à jour la liste de critères pour chaque environnement et chaque CMROC
	 * 
	 * @param environnements
	 *            Liste des environnements
	 * @throws CimutMongoDBException
	 */
	public void setElligibleCriteres(List<String> environnements) throws CimutMongoDBException {
		MongoClient mongoClient = null;
		DBCollection collectionDoc = null;
		DBCollection collectionParam = null;

		// Parcours de chacun des environnements, cad de chacune des bases MongoDB
		for (String environnement : environnements) {
			String baseName = getNomBaseFromEnvironnement(environnement);
			logger.info("updating criteria list from schema : " + baseName);

			try {
				logger.info("get Mongo client");
				mongoClient = getMongoClient(baseName);

				logger.info("get Base");
				DB base = getBase(mongoClient, baseName);

				logger.info("get Collection documents");
				collectionDoc = base.getCollection("documents");

				logger.info("get Collection parameters");
				collectionParam = base.getCollection("parametres");

				List<String> listInit = getInitCritereList();

				// Récupération des critères pour chacun des CMROCs définis
				for (String cmroc : GlobalVariable.getListCmrocs()) {
					List<String> listElligible = elligibles(collectionDoc, cmroc);

					Iterator<String> i = listElligible.iterator();
					while (i.hasNext()) {
						String critere = i.next(); // must be called before you can call i.remove()

						if (SKIP.contains(critere)) {
							i.remove();
							continue;
						} else if (listInit.contains(critere)) {
							i.remove();
							continue;
						}

						List<String> distinct = new ArrayList<String>();
						int countDistinct = 0;
						try {
							// cela peux peter ici si nombre d'occurence trop elevée, donc non elligible !
							// on enleve !

							// Récupération de la liste des valeurs pour le critère dans le CMROC courant
							BasicDBObject cmrocQuery = new BasicDBObject(GlobalVariable.ATTR_ID_ORGANISME, cmroc);
							distinct = collectionDoc.distinct(critere, cmrocQuery);
							countDistinct = distinct.size();
						} catch (Exception e) {
							logger.error("commentaire manquant", e);
						}

						if (countDistinct == 0 || countDistinct > 100) {
							i.remove();
							continue;
						}

						BasicDBObject queryCount = new BasicDBObject();
						queryCount.put(critere, new BasicDBObject("$exists", true));
						queryCount.put(GlobalVariable.ATTR_ID_ORGANISME, cmroc);
						int countTotal = collectionDoc.find(queryCount).count();

						if (countTotal == 0) {
							i.remove();
							continue;
						}
					}

					List<Object> list = new BasicDBList();
					list.addAll(listElligible);
					list.addAll(listInit);

					logger.info(list.toString());

					collectionParam.update(new BasicDBObject("_id", "CRITERE"), new BasicDBObject("$set", new BasicDBObject(cmroc, list)), true,
							false);
				}
			} catch (Exception e) {
				throw new CimutMongoDBException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Met a jour la liste des valeurs pour chaque critère pour chaque environnement et chaque CMROC
	 * 
	 * @throws CimutConfException
	 * @throws CimutMongoDBException
	 */
	public void updateParameters(List<String> environnements) throws CimutConfException, CimutMongoDBException {
		MongoClient mongoClient = null;
		DBCollection collectionDoc = null;
		DBCollection collectionParam = null;

		// Parcours de chacun des environnements, cad de chacune des bases MongoDB
		for (String environnement : environnements) {
			String baseName = getNomBaseFromEnvironnement(environnement);
			logger.info("updating parameters from schema : " + baseName);

			try {

				logger.info("get Mongo client");
				mongoClient = getMongoClient(baseName);

				logger.info("get Base");
				DB base = getBase(mongoClient, baseName);

				logger.info("get Collection documents");
				collectionDoc = base.getCollection("documents");

				logger.info("get Collection parameters");
				collectionParam = base.getCollection("parametres");

				BasicDBObject query = new BasicDBObject();
				query.put("_id", "CRITERE");
				DBObject critereDb = collectionParam.findOne(query);

				// Récupération des critères pour chacun des CMROCs définis
				for (String cmroc : GlobalVariable.getListCmrocs()) {

					List<String> criteres = new ArrayList<String>();
					if (critereDb != null) {
						ObjectMapper mapper = new ObjectMapper();
						criteres = mapper.readValue(critereDb.get(cmroc).toString(), new TypeReference<List<String>>() {
						});
					}

					// on reinitialise et on recupere a nouveau
					if (criteres == null || criteres.isEmpty()) {
						criteres = initCritere(collectionParam, cmroc);
					}

					// Limite la mise à jour des valeurs de chaque critère aux documents du CMROC donné
					BasicDBObject cmrocQuery = new BasicDBObject(GlobalVariable.ATTR_ID_ORGANISME, cmroc);

					// Peuple les valeurs éligibles pour chacun des critères
					for (String critere : criteres) {
						logger.debug("\tadding list for : " + critere);

						// Récupération de la liste des valeurs pour le critère dans le CMROC courant
						List<Object> distinct = collectionDoc.distinct(critere, cmrocQuery);
						if (critere.equals(GlobalVariable.ATTR_STATUS)) {
							// on force le rajout 
							if (!distinct.contains(GlobalVariable.STATUS_ERROR)) {
								distinct.add(GlobalVariable.STATUS_ERROR);
							}
						}

						// remove null value
						for (Iterator<Object> itr = distinct.iterator(); itr.hasNext();) {
							Object value = itr.next();
							if (value == null || value.toString().isEmpty()) {
								itr.remove();
							}
						}

						// TODO : ne pas virer les anciennes references.
						collectionParam.update(new BasicDBObject("_id", critere), new BasicDBObject("$set", new BasicDBObject(cmroc, distinct)), true,
								false);
					}
				}
			} catch (CimutMongoDBException e) {
				throw new CimutMongoDBException(e.getMessage(), e);
			} catch (NumberFormatException e) {
				throw new CimutMongoDBException(e.getMessage(), e);
			} catch (Exception e) {
				throw new CimutMongoDBException(e.getMessage(), e);
			}
		}
	}

	/**
	 * Recupere l'integralité des cles presentes dans la collection pour le CMROC donné
	 * 
	 * @param collection
	 * @param cmroc
	 * @return La liste des clés
	 * @throws UnknownHostException
	 * @throws CimutMongoDBException
	 */
	private List<String> elligibles(DBCollection collection, String cmroc) throws UnknownHostException, CimutMongoDBException {

		String map = "function () {for (var key in this) {emit(key, null);}}";
		String reduce = "function (key, values) { null }";

		// Date range
		BasicDBObject query = new BasicDBObject();
		Calendar now = Calendar.getInstance();
		// on recherche sur les derniers 400 jours
		now.add(Calendar.DATE, -400);
		BasicDBObject dateRange = new BasicDBObject();
		dateRange.put("$gte", now.getTime());
		query.put(GlobalVariable.ATTR_DTINTEGRATION, dateRange);

		// CMROC
		query.put(GlobalVariable.ATTR_ID_ORGANISME, cmroc);

		// Création et exécution de la commande Map-Reduce
		MapReduceCommand cmd = new MapReduceCommand(collection, map, reduce, null, MapReduceCommand.OutputType.INLINE, query);
		MapReduceOutput out = collection.mapReduce(cmd);

		List<String> mapped = new ArrayList<String>();
		String key = null;
		ObjectMapper mapper = new ObjectMapper();
		String id = null;

		for (DBObject o : out.results()) {
			try {
				key = o.toString();
				JsonNode criteres = mapper.readTree(key);
				id = criteres.get("_id").asText();
				mapped.add(id);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return mapped;
	}

}
