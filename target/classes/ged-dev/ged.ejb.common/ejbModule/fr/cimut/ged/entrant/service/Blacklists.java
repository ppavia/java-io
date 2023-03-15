/**
 * @author gyclon
 */
package fr.cimut.ged.entrant.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.BlackList;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

@Stateless(mappedName = "Blacklist")
public class Blacklists {

	@EJB
	MongoConnection mongoConnection;

	/**
	 * recupere une regle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param id
	 * @return
	 * @throws CimutMongoDBException
	 */
	public boolean get(String environnement, String cmroc, String email) throws CimutMongoDBException {
		try {
			Manager<BlackList> manager = MongoManagerFactory.getBlackListManager(environnement, cmroc, mongoConnection.getMongoClient());

			BasicDBObject query = new BasicDBObject();

			//building email clause
			BasicDBObject clause1 = new BasicDBObject();
			clause1.put("email", email.toLowerCase());

			// building  domain or subDomain clause
			BasicDBObject clause2 = new BasicDBObject();

			// extract sub domain and domain from email
			String domain = email.substring(email.indexOf("@") + 1);
			String[] domains = domain.split("\\.");

			// je reconstruit les sous domain et domain voir si un de ceux là sont references dans la blacklist
			List<String> extractSsDomain = new ArrayList<String>();
			String rebuildDomain = domains[domains.length - 1];
			for (int i = domains.length - 2; i >= 0; i--) {
				rebuildDomain = domains[i] + "." + rebuildDomain;
				extractSsDomain.add(rebuildDomain.toLowerCase());
			}
			BasicDBObject clause3 = new BasicDBObject();
			clause3.put("$in", extractSsDomain);
			clause2.put("email", clause3);
			BasicDBList or = new BasicDBList();
			or.add(clause1);
			or.add(clause2);
			query = new BasicDBObject("$or", or);

			List<BlackList> list = manager.list(query);

			if (list == null || list.isEmpty()) {
				return false;
			} else {
				return true;
			}
		} catch (CimutMongoDBException e) {
			Logger.getLogger(Blacklists.class).error("commentaire manquant", e);
			throw new CimutMongoDBException(e.getMessage());
		} catch (Exception e) {
			Logger.getLogger(Blacklists.class).error("commentaire manquant", e);
			throw new CimutMongoDBException(e.getMessage());
		}
	}

}
