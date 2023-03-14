/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.mongo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.mongo.GenericMgDbBean;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.utils.GlobalVariable;

/**
 * Dao abstract class form the mongoDB collection
 * 
 * @author gyclon
 *
 * @param <T>
 */
public abstract class Manager<T extends GenericMgDbBean> {

	private InteractionMongo inter;

	/**
	 * Nom du schema (correspond au nom de l'environnement préfixé par {@link GlobalVariable#NOM_BASE_PREFIXE})
	 */
	protected String nomBase;

	/**
	 * Nom de la collection
	 */
	private String collection;

	/** CMROC */
	private String cmroc;

	private Class<T> entityClass;

	/**
	 * Constructeur
	 * 
	 * @param environnement
	 * @param collection
	 * @param cmroc
	 * @param inter
	 * @param entityClass
	 * @throws CimutMongoDBException
	 */
	public Manager(String environnement, String collection, String cmroc, InteractionMongo inter, Class<T> entityClass) throws CimutMongoDBException {
		if (environnement == null || environnement.isEmpty()) {
			throw new CimutMongoDBException("Récupération du nom de la base impossible : environnement non défini");
		}
		this.nomBase = GlobalVariable.NOM_BASE_PREFIXE + environnement;
		this.collection = collection;
		this.cmroc = cmroc;
		this.inter = inter;
		this.entityClass = entityClass;
	}

	/**
	 * recupere une connection depuis notre pool
	 * 
	 * @return
	 */
	protected InteractionMongo getConnection() {
		return inter;
	}

	/**
	 * Retourne la collection concernée par ce Manager
	 * 
	 * @return
	 * @throws CimutMongoDBException
	 */
	protected DBCollection getCollection() throws CimutMongoDBException {
		try {
			return getConnection().getCollection(getNomBase(), getNomCollection());
		} catch (NumberFormatException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		} catch (CimutConfException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * Retourne le nom de la collection
	 * 
	 * @return
	 * @throws CimutMongoDBException
	 */
	protected String getNomCollection() throws CimutMongoDBException {
		if (collection == null || collection.isEmpty()) {
			throw new CimutMongoDBException("Nom de la collection inexistant");
		}
		return collection;
	}

	/**
	 * Retourne le nom du schema
	 * 
	 * @return
	 * @throws CimutMongoDBException
	 */
	protected String getNomBase() throws CimutMongoDBException {
		return nomBase;
	}

	/** Retourne le CMROC concerné par ce Manager */
	protected String getCmroc() {
		return cmroc;
	}

	/**
	 * methode d'insertion
	 * 
	 * @param json
	 * @return
	 * @throws CimutMongoDBException
	 */
	public T insert(T object) throws CimutMongoDBException {

		ObjectMapper mapper = getObjectMapper();
		String docMaj;
		try {
			docMaj = mapper.writeValueAsString(object);
			DBObject mongoDBObject = (DBObject) JSON.parse(docMaj);
			//insert du document dans la collection
			WriteResult writeresult = this.getCollection().insert(mongoDBObject);
			// test de resultat de l insert
			int result = (Integer) writeresult.getField("ok");
			if (result != 1) {
				throw new CimutMongoDBException("Insert du fichier json en erreur " + writeresult.getError());
			}
			object = mapper.readValue(mongoDBObject.toString(), entityClass);
		} catch (JsonProcessingException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
		return object;
	}

	/**
	 * remove an occurrence from collection
	 * 
	 * @param id
	 * @throws CimutMongoDBException
	 */
	public void remove(String id) throws CimutMongoDBException {
		if (id.isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}

		DBObject oldObject = this.getDBobj(id);
		if (oldObject == null) {
			throw new CimutMongoDBException("l'Occurence n'existe pas");
		}
		this.getCollection().remove(oldObject);
	}

	/**
	 * update an occurrence from collection
	 * 
	 * @param id
	 * @throws CimutMongoDBException
	 */
	public void update(T object) throws CimutMongoDBException {
		if (object.getId().isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}

		if (getDBobj(object.getId()) == null) {
			throw new CimutMongoDBException("l'Occurence n'existe pas");
		}

		try {
			Boolean upsert = false;

			BasicDBObject docCriteres = new BasicDBObject("_id", object.getId());
			ObjectMapper mapper = getObjectMapper();
			String docMaj = mapper.writeValueAsString(object);
			DBObject mongoDBObject = (DBObject) JSON.parse(docMaj);
			Logger.getLogger(EddmManager.class).debug("UPDATE MONGO" + object.getId() + " " + docMaj);
			WriteResult writeresult = this.getCollection().update(docCriteres, mongoDBObject, false, false);
			Boolean resultupdatedExisting = (Boolean) writeresult.getField("updatedExisting");
			if (upsert) {
				//Update du fichier document  ok 	
			} else {
				if (!resultupdatedExisting) {
					throw new CimutMongoDBException("Update du document ( id = " + object.getId()
							+ " ) en erreur. Le document n existe pas en base. Aucune operation d effectue. ");
				}
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/** Fournit un {@link ObjectMapper} instancié et paramétré */
	protected ObjectMapper getObjectMapper() {
		return new ObjectMapper().configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
	}

	/**
	 * Get the DBObject from its id
	 * 
	 * @param id
	 * @return
	 * @throws CimutMongoDBException
	 */
	protected DBObject getDBobj(String id) throws CimutMongoDBException {
		if (id.isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}

		BasicDBObject query = new BasicDBObject("_id", id);
		return this.getCollection().findOne(query);
	}

	/**
	 * Get the bean from his id
	 * 
	 * @param id
	 * @return
	 * @throws CimutMongoDBException
	 */
	public T get(String id) throws CimutMongoDBException {
		try {
			DBObject jsonObj = this.getDBobj(id);
			if (jsonObj == null) {
				return null;
			}
			ObjectMapper mapper = getObjectMapper();
			return mapper.readValue(jsonObj.toString(), entityClass);
		} catch (JsonParseException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	public T convert(String json) throws CimutMongoDBException {
		ObjectMapper mapper = getObjectMapper();
		try {
			return mapper.readValue(json, entityClass);
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	/**
	 * Get the whole collection
	 * 
	 * @return
	 * @throws CimutMongoDBException
	 */
	public List<T> list() throws CimutMongoDBException {
		return list(new BasicDBObject());
	}

	protected List<T> cursorToList(DBCursor cursor) throws CimutMongoDBException {
		List<T> list = new ArrayList<T>();
		ObjectMapper mapper = getObjectMapper();

		try {
			// loop over it
			while (cursor.hasNext()) {
				T element = mapper.readValue(cursor.next().toString(), this.entityClass);
				// add to object list
				list.add(element);
			}

		} catch (JsonParseException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		} catch (JsonMappingException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		} catch (IOException e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}

		// then return
		return list;
	}

	/** Ajoute le CMROC à la requête */
	protected void addCmrocToQuery(BasicDBObject query) {
		query.put("cmroc", cmroc);
	}

	/**
	 * Get the whole collection with query and pagination
	 * 
	 * @param query
	 * @param pagesize
	 * @param page
	 * @return
	 * @throws CimutMongoDBException
	 */
	public List<T> list(BasicDBObject query, int pagesize, int page) throws CimutMongoDBException {
		if (query == null) {
			query = new BasicDBObject();
		}

		// Ajout du CMROC
		addCmrocToQuery(query);

		// do the request
		DBCursor cursor = this.getCollection().find(query).limit(pagesize).skip((page - 1) * pagesize);
		return cursorToList(cursor);
	}

	/**
	 * Get the whole collection with query, for the specified CMROC of this manager
	 * 
	 * @param query
	 * @return
	 * @throws CimutMongoDBException
	 */
	public List<T> list(BasicDBObject query) throws CimutMongoDBException {
		if (query == null) {
			query = new BasicDBObject();
		}

		// Ajout du CMROC
		addCmrocToQuery(query);

		// do the request
		DBCursor cursor = this.getCollection().find(query);
		return cursorToList(cursor);
	}

	public DBCursor getCursor(BasicDBObject query) throws CimutMongoDBException {
		if (query == null) {
			throw new CimutMongoDBException("Les parametres de recherche ne sont pas valorisé");
		}

		// Ajout du CMROC
		addCmrocToQuery(query);

		return this.getCollection().find(query);
	}

	/**
	 * Get the whole collection with query
	 * 
	 * @param query
	 * @return
	 * @throws CimutMongoDBException
	 */
	public List<T> list(BasicDBObject query, BasicDBObject sort) throws CimutMongoDBException {
		if (query == null) {
			query = new BasicDBObject();
		}

		// Ajout du CMROC
		addCmrocToQuery(query);

		// do the request
		DBCursor cursor = this.getCollection().find(query);

		if (sort != null) {
			cursor = cursor.sort(sort);
		}

		return cursorToList(cursor);
	}

	/**
	 * ferme toute les connections
	 * 
	 * @throws CimutMongoDBException
	 */
	public void close() throws CimutMongoDBException {
		getConnection().closeConnexion();
		inter = null;
	}

}
