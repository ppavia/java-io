/**
 * @author gyclon
 */
package dao;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

public class RuleTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	private Date before;
	private Manager<Rule> manager;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();
		manager = MongoManagerFactory.getRuleManager(envirTeste, "9970", new InteractionMongo());

		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "C:/SVN/getEntrante/fr.cimut.ged.entrant.serviceIntegration/trunk/src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.p360.url", "http://lcidxs2.cimut.fr:9601/meslCIP/Search360/");
		props.setProperty("fr.cimut.ged.entrante.cmroc.file.path",
				"C:/SVN/getEntrante/fr.cimut.ged.entrant.serviceIntegration/trunk/src/test/resources/complement_cmroc.csv");
		props.setProperty("fr.cimut.ged.entrante.destination.dir", "C:/tmp/");

		props.setProperty("fr.cimut.ged.entrante.indexation.dir", "indexation");
		props.setProperty("fr.cimut.ged.entrante.transcodage.dir", "transcodage");
		props.setProperty("fr.cimut.ged.entrante.integration.dir", "integration");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.password", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.admin.password", "quimper");
		props.setProperty("fr.cimut.ged.entrante.indexation.admin.user", "rootcimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.nom_database_collection", "documents");
		props.setProperty("fr.cimut.ged.entrante.home.dir", "C:/tmp/");
		// props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:27017");

		props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:7017,lcid-mongodb-01:27017,lcid-mongodb-02:27017");

		props.setProperty("fr.cimut.ged.entrante.environnement", "INTV3");
		props.setProperty("fr.cimut.ged.entrante.error.dir", "errors");
		props.setProperty("fr.cimut.ged.entrante.destination.dir", "/data/gede");
		props.setProperty("fr.cimut.ged.entrante.indexation.active.basename", "9916;9911");
		props.setProperty("fr.cimut.ged.entrante.multicanal.url", "http://lcid-multicanal:28080/server/multicanal/mail");

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
	public void deleteAllTest() {

		System.out.println("deleteAllTest");

		try {
			List<Rule> listing = manager.list();
			for (Rule rule : listing) {
				System.out.println("removing : " + rule.getId());
				manager.remove(rule.getId());
				System.out.println("removed");
			}
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void removeTest() {

		System.out.println("removeTest");
		try {
			manager.remove("54e45478d9df7bd170b41c81");

		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void listTestOrdered() {

		System.out.println("listTestOrdered");

		try {
			BasicDBObject sort = new BasicDBObject();
			sort.put("priority", 1);
			sort.put("name", 1);
			BasicDBObject query = new BasicDBObject();
			query.put("service.id", new BasicDBObject("$not", new BasicDBObject("$eq", null)));
			//query.put("actif", new BasicDBObject("$eq","true"));
			List<Rule> listing = manager.list(query, sort);
			for (Rule rule : listing) {
				System.out.println("id : " + rule.getId() + " name : " + rule.getName() + " P:" + rule.getPriority() + " Actif:"
						+ ((rule.isActif()) ? "Oui" : "Non"));
				for (RuleCriteres crit : rule.getCriteres()) {
					System.out.print("\t" + crit.getId() + " : ");
					List<String> values = crit.getParameters();
					for (String string : values) {
						System.out.print(string + " ");
					}
					System.out.println(" ");
				}
			}
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void listTest() {

		System.out.println("listTest");

		try {

			BasicDBObject query = new BasicDBObject();
			query.append("actif", false);

			List<Rule> listing = manager.list(query);
			for (Rule rule : listing) {
				System.out.println("id : " + rule.getId() + " P:" + rule.getPriority() + " Actif:" + ((rule.isActif()) ? "Oui" : "Non") + " "
						+ rule.getUser() + " " + rule.getDateModif());
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
	public void updateTest() {

		System.out.println("updateTest");

		try {
			Rule rule = manager.get("54f61064e4b01ab05d8cddcd");

			if (rule == null) {
				fail("Occurence not found");
				return;
			}

			List<String> mails = new ArrayList<String>();
			mails.add("yclon.guillaume@gmail.com");
			mails.add("guillaume.yclon@gmail.com");

			rule.setMails(mails);
			rule.setPriority("9");
			rule.setActif(false);

			manager.update(rule);
			rule = manager.get("54f61064e4b01ab05d8cddcd");
			System.out.println("id : " + rule.getId() + " P:" + rule.getPriority() + " Actif:" + ((rule.isActif()) ? "Oui" : "Non") + " mails "
					+ rule.getMails().toString());
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void getTest() {

		System.out.println("getTest");

		try {
			Rule rule = manager.get("54e37e73e4b0ada0e525913d");
			System.out.println("id : " + rule.getId() + " P:" + rule.getPriority() + " Actif:" + ((rule.isActif()) ? "Oui" : "Non"));
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void add() {

		Rule rule = new Rule();
		rule.setName("Regle N°5");
		rule.setActif(true);
		rule.setPriority("1");
		rule.setUser("gyclon9916");

		RuleDa daParameters = new RuleDa();
		daParameters.setId("332886");
		daParameters.setName("ASPBTP");
		daParameters.setSujet("Object demande");
		daParameters.setSupport("DIV");
		daParameters.setType("EXT");

		rule.setService(daParameters);

		List<String> list = new ArrayList<String>();
		list.add("Personne");
		list.add("Partenaire");
		RuleCriteres critere = new RuleCriteres("TYPE_COURRIER", list);

		List<RuleCriteres> criteres = new ArrayList<RuleCriteres>();
		criteres.add(critere);
		rule.setCriteres(criteres);

		rule.addMail("yclon.guillaume@gmail.com");
		rule.addMail("guillaume.yclon@gmail.com");

		ObjectMapper mapper = new ObjectMapper();
		try {
			System.out.println(mapper.writeValueAsString(rule));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			fail();
		}

		try {
			manager.insert(rule);
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
			fail();
		}

		try {
			List<Rule> listing = manager.list();

			for (Rule rule1 : listing) {
				System.out.println("id : " + rule1.getId() + " P:" + rule1.getPriority() + " Actif:" + ((rule1.isActif()) ? "Oui" : "Non") + " "
						+ rule1.getUser() + " " + rule1.getDateModif());
				try {
					System.out.println(mapper.writeValueAsString(rule1));
				} catch (JsonProcessingException e) {
					e.printStackTrace();
					fail();
				}

			}
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}

	}

}
