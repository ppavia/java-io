package fr.cimut.ged.entrant.mongo;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;

public class RuleHistoryManager extends Manager<RuleHistorique> {

	private static final String collection = "RuleHistorique";

	public RuleHistoryManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		super(environnement, collection, cmroc, inter, RuleHistorique.class);
	}

	/**
	 * remove an occurrence from collection
	 * 
	 * @param id
	 * @throws CimutMongoDBException
	 */
	@Override
	public void remove(String id) throws CimutMongoDBException {
		BasicDBObject query = new BasicDBObject();
		query.put("id", id);
		this.getCollection().remove(query);
	}

	@Override
	public List<RuleHistorique> list() throws CimutMongoDBException {
		BasicDBObject query = new BasicDBObject();
		query.put("cmroc", getCmroc());
		BasicDBObject sort = new BasicDBObject();
		sort.put("dateModif", -1);
		DBCursor cursor = this.getCollection().find(query).sort(sort);
		return cursorToList(cursor);
	}

}
