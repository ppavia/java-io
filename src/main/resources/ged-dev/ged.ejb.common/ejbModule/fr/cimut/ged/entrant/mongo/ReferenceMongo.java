package fr.cimut.ged.entrant.mongo;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class ReferenceMongo {

	private String cmroc = "";
	private DB database;
	MongoClient mongoClient;

	public ReferenceMongo() {
	}

	public ReferenceMongo(String cmroc, DB database, MongoClient mongoClient) {
		this.setCmroc(cmroc);
		this.setDatabase(database);
		this.setMongoClient(mongoClient);
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public DB getDatabase() {
		return database;
	}

	public void setDatabase(DB database) {
		this.database = database;
	}

	public MongoClient getMongoClient() {
		return mongoClient;
	}

	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}

}
