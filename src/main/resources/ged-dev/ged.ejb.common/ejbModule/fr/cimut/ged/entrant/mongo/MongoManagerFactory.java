package fr.cimut.ged.entrant.mongo;

import fr.cimut.ged.entrant.beans.mongo.BlackList;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;

public class MongoManagerFactory {

	private MongoManagerFactory() {
	}

	public static Manager<Rule> getRuleManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		return new RuleManager(environnement, cmroc, inter);
	}

	public static Manager<DocumentMongo> getDocumentManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		return new DocumentManager(environnement, cmroc, inter);
	}

	public static Manager<RuleHistorique> getRuleHistoryManager(String environnement, String cmroc, InteractionMongo inter)
			throws CimutMongoDBException {
		return new RuleHistoryManager(environnement, cmroc, inter);
	}

	public static Manager<Parameter> getParameterManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		return new ParameterManager(environnement, cmroc, inter);
	}

	public static Manager<BlackList> getBlackListManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		return new BlackListManager(environnement, cmroc, inter);
	}

}
