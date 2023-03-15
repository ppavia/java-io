package fr.cimut.ged.entrant.mongo;

import java.util.List;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class DocumentManager extends Manager<DocumentMongo> {

	private static final String collection = "documents";

	public DocumentManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		super(environnement, collection, cmroc, inter, DocumentMongo.class);
	}

	@Override
	public List<DocumentMongo> list() throws CimutMongoDBException {
		throw new CimutMongoDBException("not implemented");
	}

	@Override
	public List<DocumentMongo> list(BasicDBObject query, BasicDBObject sort) throws CimutMongoDBException {
		return super.list(query, sort);
	}

	@Override
	public List<DocumentMongo> list(BasicDBObject query, int pagesize, int page) throws CimutMongoDBException {
		throw new CimutMongoDBException("not implemented");
	}

	@Override
	protected DBObject getDBobj(String id) throws CimutMongoDBException {
		if (id.isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}
		BasicDBObject query = new BasicDBObject(GlobalVariable.ATTR_ID, id);
		return this.getCollection().findOne(query);
	}

	@Override
	protected void addCmrocToQuery(BasicDBObject query) {
		query.put(GlobalVariable.ATTR_ORGANISME, getCmroc());
	}

	/**
	 * Only use to update status in "En erreur"; DA_ID, EDDOC_ID, and ERROR_DA ERROR_DA and DA_ID
	 */

	@Override
	public void update(DocumentMongo doc) throws CimutMongoDBException {

		if (doc.getId().isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}
		DBObject dbObject = getDBobj(doc.getId());
		if (dbObject == null) {
			throw new CimutMongoDBException("l'Occurence n'existe pas");
		}

		try {
			Boolean upsert = false;
			BasicDBObject docCriteres = new BasicDBObject(GlobalVariable.ATTR_ID, doc.getId());
			DBObject modif = new BasicDBObject();

			if (doc.getStatus() != null && !doc.getStatus().isEmpty()) {
				modif.put(GlobalVariable.ATTR_STATUS, doc.getStatus());
			}
			if (doc.getSudeId() != null && !doc.getSudeId().isEmpty()) {
				modif.put(GlobalVariable.ATTR_DA_ID, doc.getSudeId());
			}
			if (doc.getErreurDa() != null && !doc.getErreurDa().isEmpty()) {
				modif.put(GlobalVariable.ATTR_ERROR_DA, doc.getErreurDa());
			}
			if (doc.getErreurEddm() != null && !doc.getErreurEddm().isEmpty()) {
				modif.put(GlobalVariable.ATTR_ERROR_EDDM, doc.getErreurEddm());
			}
			if (doc.getEddocId() != null && !doc.getEddocId().isEmpty()) {
				modif.put(GlobalVariable.ATTR_EDDOC_ID, doc.getEddocId());
			}
			if (doc.getMailed() != null && !doc.getMailed().isEmpty()) {
				modif.put(GlobalVariable.ATTR_MAILED, doc.getMailed());
			}
			if (doc.getIntegrationsTotale() != null) {
				modif.put(GlobalVariable.ATTR_INTEGRATIONS_TOT, doc.getIntegrationsTotale());
			}
			if (doc.getIntegrationsOK() != null) {
				modif.put(GlobalVariable.ATTR_INTEGRATIONS_OK, doc.getIntegrationsOK());
			}
			if (doc.getIntegrationsKO() != null) {
				modif.put(GlobalVariable.ATTR_INTEGRATIONS_KO, doc.getIntegrationsKO());
			}
			DBObject update = new BasicDBObject("$set", modif);
			Logger.getLogger(DocumentManager.class).info(update.toString());

			WriteResult writeresult = this.getCollection().update(docCriteres, update, false, false);
			Boolean resultupdatedExisting = (Boolean) writeresult.getField("updatedExisting");
			if (!upsert && !resultupdatedExisting) {
				throw new CimutMongoDBException(
						"Update du document ( id = " + doc.getId() + " ) en erreur. Le document n existe pas en base. Aucune operation d effectue. ");
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}
	}

}
