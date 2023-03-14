/**
 * @author gyclon
 */
package dao;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

public class RuleHistTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	private Date before;
	private Manager<RuleHistorique> manager;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();
		manager = MongoManagerFactory.getRuleHistoryManager(envirTeste, "9916", new InteractionMongo());
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:27017");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.password", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.nom_database_collection", "documents");
		System.setProperties(props);
	}

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("END                                               #");
		System.out.println("###################################################");
		try {
			manager.close();
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
		}
	}

	//@Test
	public void listHistTest() {

		System.out.println("listHistTest");

		try {
			BasicDBObject query = new BasicDBObject();
			query.put("id", "REGLE N°1");
			List<RuleHistorique> list = manager.list(query);
			for (RuleHistorique rule : list) {
				ObjectMapper mapper = new ObjectMapper();
				try {
					System.out.println(mapper.writeValueAsString(rule));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					fail();
				}
			}
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void listHistorique() {

		System.out.println("listHistorique");

		try {
			BasicDBObject query = new BasicDBObject();
			query.put("id", "Regle N°1");
			List<RuleHistorique> listing = manager.list(query);
			for (RuleHistorique rule : listing) {
				System.out.println("id : " + rule.getId() + " P:" + rule.getObject() + " " + rule.getUser() + " " + rule.getDateModif());
			}
		} catch (CimutMongoDBException e1) {
			// 
			e1.printStackTrace();
			fail();
		}
	}

}
