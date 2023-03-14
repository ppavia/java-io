/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.service;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

@Stateless(mappedName = "Rules")
@TransactionAttribute(TransactionAttributeType.NEVER)
public class Rules {

	@EJB
	MongoConnection mongoConnection;

	/**
	 * Insère une nouvelle règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 * @return
	 */
	public Rule insert(String environnement, String cmroc, Rule rule) {
		try {
			Manager<Rule> manager = MongoManagerFactory.getRuleManager(environnement, cmroc, mongoConnection.getMongoClient());
			return manager.insert(rule);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Récupère une règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param id
	 * @return
	 */
	public Rule get(String environnement, String cmroc, String id) {
		try {
			Manager<Rule> manager = MongoManagerFactory.getRuleManager(environnement, cmroc, mongoConnection.getMongoClient());
			return manager.get(id);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Liste toutes les règles
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param parameters
	 * @return
	 */
	public List<Rule> list(String environnement, String cmroc, Map<String, List<String>> parameters) {
		try {
			Manager<Rule> manager = MongoManagerFactory.getRuleManager(environnement, cmroc, mongoConnection.getMongoClient());
			BasicDBObject query = new BasicDBObject();

			if (parameters.containsKey("name")) {
				query.append("name", parameters.get("name").get(0));
			}
			if (parameters.containsKey("priority")) {
				query.append("priority", parameters.get("priority").get(0));
			}
			if (parameters.containsKey("actif")) {
				if (!parameters.get("actif").get(0).equals("tous")) {
					query.append("actif", parameters.get("actif").get(0).equals("true"));
				}
			}
			if (parameters.containsKey("service")) {
				query.append("service.name", parameters.get("service").get(0));
			}

			BasicDBObject sort = new BasicDBObject();
			sort.put("priority", 1);
			sort.put("name", 1);

			return manager.list(query, sort);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Efface une règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 */
	public void remove(String environnement, String cmroc, String id) {
		try {
			Manager<Rule> manager = MongoManagerFactory.getRuleManager(environnement, cmroc, mongoConnection.getMongoClient());
			manager.remove(id);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Met a jour une règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 */
	public void update(String environnement, String cmroc, Rule rule) {
		try {
			Manager<Rule> manager = MongoManagerFactory.getRuleManager(environnement, cmroc, mongoConnection.getMongoClient());
			manager.update(rule);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		}
	}

	/**
	 * Insère une nouvelle règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 * @return
	 */

	public List<RuleHistorique> getHistorique(String environnement, String cmroc, String id) {
		try {
			Manager<RuleHistorique> manager = MongoManagerFactory.getRuleHistoryManager(environnement, cmroc, mongoConnection.getMongoClient());
			BasicDBObject query = new BasicDBObject();
			query.put("id", id);
			BasicDBObject sort = new BasicDBObject();
			sort.put("dateModif", -1);
			return manager.list(query, sort);
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Rules.class).error("commentaire manquant", e);
			throw new RuntimeException(e.getMessage());
		}
	}
}
