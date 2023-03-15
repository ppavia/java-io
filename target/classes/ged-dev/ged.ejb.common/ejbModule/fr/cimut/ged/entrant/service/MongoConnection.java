package fr.cimut.ged.entrant.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;

/**
 * Session Bean implementation class MongoConnection
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class MongoConnection {

	private InteractionMongo interactionMongo = null;

	/**
	 * Default constructor.
	 */
	public MongoConnection() {

	}

	/**
	 * recupere le interactionMongo
	 * 
	 * @return InteractionMongo
	 */
	@Lock(LockType.READ)
	public InteractionMongo getMongoClient() {
		return interactionMongo;
	}

	/**
	 * instancie le interactionMongo
	 */
	@PostConstruct
	private void construct() {
		interactionMongo = new InteractionMongo();
	}

	/**
	 * Ferme les connections ouvertes
	 */
	@PreDestroy
	private void destroy() {
		try {
			interactionMongo.closeConnexion();
		} catch (CimutMongoDBException e) {
			Logger.getLogger(MongoConnection.class).error("commentaire manquant", e);
		}
	}

}
