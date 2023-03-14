package rest;

import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.beans.mongo.BlackList;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;

public class BlacklistTest {

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:27017");
		props.setProperty("fr.cimut.ged.entrante.indexation.password", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "cimut");
		System.setProperties(props);
	}

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

	//@Test
	public void removeTest() throws CimutConfException {

		InteractionMongo mongoConnection = new InteractionMongo();
		try {

			Manager<BlackList> blackListManager = MongoManagerFactory.getBlackListManager("INTV3", "9970", mongoConnection);
			blackListManager.remove("5718e8a5e4b094c40e6996fa");

		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
		}
	}

}