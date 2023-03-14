/**
 * @author gyclon
 */
package dao;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.BlackList;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

public class BlackListTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	private Date before;
	private Manager<BlackList> manager;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();
		manager = MongoManagerFactory.getBlackListManager(envirTeste, "9970", new InteractionMongo());
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.cmroc.file.path", "src/test/resources/complement_cmroc.csv");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.password", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.admin.password", "quimper");
		props.setProperty("fr.cimut.ged.entrante.indexation.admin.user", "rootcimut");
		props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:27017");
		// props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:7017,lcid-mongodb-01:27017,lcid-mongodb-02:27017");
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
			List<BlackList> listing = manager.list();
			for (BlackList blackList : listing) {
				System.out.println("removing : " + blackList.getId());
				manager.remove(blackList.getId());
				System.out.println("removed");
			}
		} catch (CimutMongoDBException e) {
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
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void listTestPaginated() {

		System.out.println("listTestOrdered");

		try {
			BasicDBObject query = new BasicDBObject();
			List<BlackList> listing = manager.list(query, 2, 1);
			for (BlackList blackList : listing) {
				System.out.println("id : " + blackList.getId() + " email : " + blackList.getEmail() + " DtMaJ:" + blackList.getDtModification()
						+ " userMaJ:" + blackList.getUserModification());
			}
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void listTest() {

		System.out.println("listTest");

		try {

			BasicDBObject query = new BasicDBObject();
			List<BlackList> listing = manager.list(query);
			for (BlackList blackList : listing) {
				System.out.println("id : " + blackList.getId() + " email : " + blackList.getEmail() + " DtMaJ:" + blackList.getDtModification()
						+ " userMaJ:" + blackList.getUserModification());
			}
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void updateTest() {

		System.out.println("updateTest");

		try {
			BlackList blackList = manager.get("56e28f7a7ce92223b4da9fdd");

			if (blackList == null) {
				fail("Occurence not found");
				return;
			}
			blackList.setEmail("ABCD");
			blackList.setDtModification(new Date().getTime());
			blackList.setUserModification("GYC TEST U");
			manager.update(blackList);
			blackList = manager.get("56e6888c7ce92d2eb438fc3c");
			System.out.println("id : " + blackList.getId() + " email : " + blackList.getEmail() + " DtMaJ:" + blackList.getDtModification()
					+ " userMaJ:" + blackList.getUserModification());
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void getTest() {

		System.out.println("getTest");

		try {
			BlackList blackList = manager.get("56e6888c7ce92d2eb438fc3c");
			if (blackList == null) {
				System.out.println("nothing found ! ");
				fail();
				return;
			}
			System.out.println("id : " + blackList.getId() + " email : " + blackList.getEmail() + " DtMaJ:"
					+ new Date(blackList.getDtModification()).toLocaleString() + " userMaJ:" + blackList.getUserModification());
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void search() {
		BasicDBObject query = new BasicDBObject();
		query.put("email", Pattern.compile("PXES@CIMUT.FR", Pattern.CASE_INSENSITIVE));
		try {
			List<BlackList> list = manager.list(query);

			if (list == null || list.isEmpty()) {
				System.out.println("NOT FOUND !");
			} else {
				System.out.println(" FOUND !");
			}

		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void all() {

		BlackList blackList = new BlackList();
		blackList.setEmail("TEST UNITAIRE");
		blackList.setCmroc("9970");
		blackList.setUserModification("TEST U");

		try {
			blackList = manager.insert(blackList);
			assertTrue(blackList != null);
			assertTrue(!blackList.getId().isEmpty());
			assertTrue(!blackList.getEmail().isEmpty());
			assertTrue(!blackList.getUserModification().isEmpty());
			assertTrue(blackList.isCreated());

			Calendar now = Calendar.getInstance();
			now.add(Calendar.MINUTE, -1);
			assertTrue(new Date(blackList.getDtModification()).after(now.getTime()));

		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}

		try {

			blackList.setEmail("TEST U2");
			manager.update(blackList);

			blackList = manager.get(blackList.getId());
			if (blackList == null) {
				System.out.println("nothing found ! ");
				fail();
				return;
			}

			assertTrue(!blackList.getEmail().isEmpty());
			assertTrue("TEST U2".equals(blackList.getEmail()));
			assertTrue(!blackList.isCreated());

			manager.remove(blackList.getId());

			blackList = manager.get(blackList.getId());
			assertTrue(blackList == null);

		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void add() {
		BlackList blackList = new BlackList();
		blackList.setEmail("email121");
		blackList.setCmroc("9970");
		blackList.setUserModification("GYC CREATE II");

		try {
			blackList = manager.insert(blackList);
			System.out.println(blackList.getId());
			System.out.println(blackList.getEmail());
			System.out.println(blackList.getUserModification());
			System.out.println(new Date(blackList.getDtModification()).toLocaleString());
			System.out.println(blackList.isCreated());

		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void isSpam() {

		String email = "guillaume.yclon@gmail.com";

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
		List<BlackList> listing;

		System.out.println(query.toString());

		try {
			listing = manager.list(query);
			for (BlackList blackList : listing) {
				System.out.println("id : " + blackList.getId() + " email : " + blackList.getEmail() + " DtMaJ:" + blackList.getDtModification()
						+ " userMaJ:" + blackList.getUserModification());
			}
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
		}

	}
}
