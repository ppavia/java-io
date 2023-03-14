package dao;

import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;

import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.indexation.ejb.PoolerMdb;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

public class ParameterTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	private Date before;
	private Manager<Parameter> manager;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();
		manager = MongoManagerFactory.getParameterManager(envirTeste, "8080", new InteractionMongo());
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
	public void getCritereTest() {

		System.out.println("getCritereTest");
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", "CRITERE");
			//query.put("q","C");
			List<Parameter> listing = manager.list(query, 15, 1);
			System.out.println("page 1 :" + listing.get(0).getList().toString());
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void getDepartementTest() {

		System.out.println("getDepartementTest");
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", "DEPARTEMENT");
			query.put("q", "C");
			List<Parameter> listing = manager.list(query, 5, 1);
			System.out.println("page 1 :" + listing.get(0).getList().toString());
			listing = manager.list(query, 5, 2);
			System.out.println("page 1 :" + listing.get(0).getList().toString());
			listing = manager.list(query, 10, 1);
			System.out.println("page 2 :" + listing.get(0).getList().toString());

		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void getRegionTest() {

		System.out.println("getRegionTest");
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", "REGION");
			//query.put("q","B");
			List<Parameter> listing = manager.list(query, 5, 1);
			System.out.println("page 1 :" + listing.get(0).getList().toString());
			listing = manager.list(query, 5, 2);
			System.out.println("page 1 :" + listing.get(0).getList().toString());
			listing = manager.list(query, 10, 1);
			System.out.println("page 2 :" + listing.get(0).getList().toString());

		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void getListMatchTest() {

		System.out.println("getListTest");
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", "TYPE_DOSSIER");
			query.put("q", "pharma");
			List<Parameter> listing = manager.list(query, 10, 1);
			System.out.println(listing.get(0).getList().toString());
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void getListTest() {

		System.out.println("getListSimpleTest");
		try {
			BasicDBObject query = new BasicDBObject();
			query.put("_id", "TYPE_DOSSIER");
			//query.put("q","p");
			List<Parameter> listing = manager.list(query, 10, 1);
			System.out.println("page 1 :" + listing.get(0).getList().toString());
			listing = manager.list(query, 10, 2);
			System.out.println("page 2 :" + listing.get(0).getList().toString());
			listing = manager.list(query, 10, 3);
			System.out.println("page 3 :" + listing.get(0).getList().toString());
			//listing = manager.list(query , 10, 4);
			//System.out.println("page 4 :"+listing.get(0).getList().toString());
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void elligibleTest() {

		System.out.println("elligibleTest");
		InteractionMongo inter = new InteractionMongo();

		try {
			inter.setElligibleCriteres(Arrays.asList(envirTeste));
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				inter.closeConnexion();
			} catch (CimutMongoDBException e) {
				// 
				e.printStackTrace();
			}
		}

	}

	//@Test
	public void updatePollerTest() {

		System.out.println("updatePollerTest");

		InteractionMongo inter = new InteractionMongo();

		try {
			Logger.getLogger(PoolerMdb.class).info("updParameters start");
			inter.updateParameters(Arrays.asList(envirTeste));
		} catch (CimutMongoDBException e) {
			e.printStackTrace();
			fail();
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
		} finally {
			try {
				inter.closeConnexion();
			} catch (CimutMongoDBException e) {
				// 
				e.printStackTrace();
			}
		}

	}

}
