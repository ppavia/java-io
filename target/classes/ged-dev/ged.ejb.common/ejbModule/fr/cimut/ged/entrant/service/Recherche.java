package fr.cimut.ged.entrant.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.utils.GlobalVariable;

/**
 * Session Bean implementation class Recherche
 */
@Stateless(mappedName = "Recherche")
public class Recherche {

	@EJB
	MongoConnection mongoConnection;

	public Recherche() {
	}

	public String idFromEddoc(String environnement, String eddoc, String cmroc) {

		String id = null;

		try {
			fr.cimut.ged.entrant.mongo.Manager<DocumentMongo> managerDoc = MongoManagerFactory.getDocumentManager(environnement, cmroc,
					mongoConnection.getMongoClient());
			BasicDBObject query = new BasicDBObject();
			query.append(GlobalVariable.ATTR_EDDOC_ID, eddoc);
			List<DocumentMongo> list = managerDoc.list(query);

			if (list == null || list.isEmpty() || list.size() > 1) {
				throw new CimutMongoDBException("");
			}
			id = list.get(0).getId();
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Recherche.class).error("idFromEddoc(" + eddoc + "," + cmroc + ") => " + e.getMessage(), e);
		}
		return id;
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String recherche(String environnement, Map<String, String> parameters) {
		Date date = new Date();

		try {
			String output = mongoConnection.getMongoClient().getList(environnement, parameters);
			Logger.getLogger(Recherche.class).info("recherche Time elapsed : " + (new Date().getTime() - date.getTime()) + " Msecs");
			return output;
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Recherche.class).error(e.getMessage(), e);
		}
		return null;
	}

	public void update(String environnement, String json) throws Exception {
		String id = null;

		try {
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonColumn = null;
			jsonColumn = (ObjectNode) mapper.readTree(json);
			if (!jsonColumn.hasNonNull(GlobalVariable.ATTR_ID_ORGANISME) || !jsonColumn.hasNonNull(GlobalVariable.ATTR_ID_DOC)) {
				throw new Exception("le cmroc ou l'identifiant est null");
			}
			id = jsonColumn.get(GlobalVariable.ATTR_ID_DOC).asText();
		} catch (JsonProcessingException e1) {
			Logger.getLogger(Recherche.class).error(e1);
			throw new Exception(e1.getMessage());
		} catch (IOException e1) {
			Logger.getLogger(Recherche.class).error(e1);
			throw new Exception(e1.getMessage());
		}

		if (environnement == null || id == null) {
			throw new Exception("le nom de la base ou l'identifiant est null");
		}

		try {
			mongoConnection.getMongoClient().update(id, json, environnement);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Recherche.class).error("commentaire manquant", e);
			throw new Exception(e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public String statistiques(String environnement, Map<String, String> parameters) throws Exception {
		try {
			return mongoConnection.getMongoClient().getStats(environnement, parameters);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Recherche.class).error("commentaire manquant", e);
			throw new Exception(e.getMessage());
		}
	}

	@TransactionAttribute(TransactionAttributeType.NEVER)
	public Parameter getParameters(String environnement, String cmroc, Map<String, String> search) throws Exception {

		Logger.getLogger(Recherche.class).info("getParameters(" + cmroc + "," + search.toString() + ")");

		try {
			fr.cimut.ged.entrant.mongo.Manager<Parameter> parameterManager = MongoManagerFactory.getParameterManager(environnement, cmroc,
					mongoConnection.getMongoClient());
			// tricky ...
			BasicDBObject query = new BasicDBObject();
			query.put("_id", search.get("id"));
			if (search.containsKey("q") && !search.get("q").isEmpty()) {
				query.put("q", search.get("q"));
			}

			int pagesize = Integer.parseInt(search.get("pageSize"));
			int page = Integer.parseInt(search.get("page"));
			List<Parameter> parameters = parameterManager.list(query, pagesize, page);

			if (!parameters.isEmpty()) {
				return parameters.get(0);
			} else {
				Parameter parameter = new Parameter();
				parameter.setId(search.get("id"));
				parameter.setList(new ArrayList<String>());
				return parameter;
			}
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Recherche.class).error(e.getMessage());
			throw new Exception(e.getMessage());
		}
	}

}
