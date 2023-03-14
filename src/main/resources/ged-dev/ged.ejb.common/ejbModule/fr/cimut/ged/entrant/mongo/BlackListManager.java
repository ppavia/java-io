package fr.cimut.ged.entrant.mongo;

import java.util.Date;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import fr.cimut.ged.entrant.beans.mongo.BlackList;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;

public class BlackListManager extends Manager<BlackList> {

	private static final String collection = "Blacklist";

	@Override
	protected DBObject getDBobj(String id) throws CimutMongoDBException {
		if (id.isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}
		BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
		return this.getCollection().findOne(query);
	}

	public BlackListManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		super(environnement, collection, cmroc, inter, BlackList.class);
	}

	/**
	 * Met à jour la regle en base + historique
	 */
	@Override
	public void update(BlackList blacklist) throws CimutMongoDBException {

		blacklist.setDtModification(new Date().getTime());
		blacklist.setCreated(false);
		blacklist.setEmail(blacklist.getEmail().toLowerCase());

		try {
			BasicDBObject docCriteres = new BasicDBObject("_id", new ObjectId(blacklist.getId()));
			ObjectMapper mapper = getObjectMapper();
			String docMaj = mapper.writeValueAsString(blacklist);
			DBObject mongoDBObject = (DBObject) JSON.parse(docMaj);
			mongoDBObject.removeField("_id");
			WriteResult writeresult = this.getCollection().update(docCriteres, mongoDBObject, false, false);
			Boolean resultupdatedExisting = (Boolean) writeresult.getField("updatedExisting");
			if (!resultupdatedExisting) {
				throw new CimutMongoDBException("Update du document ( id = " + blacklist.getId()
						+ " ) en erreur. Le document n existe pas en base. Aucune operation d effectue. ");
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

	@Override
	public BlackList insert(BlackList object) throws CimutMongoDBException {
		object.setDtModification(new Date().getTime());
		object.setCreated(true);
		object.setEmail(object.getEmail().toLowerCase());
		return super.insert(object);
	}

}
