package dao;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class DocumentdaoTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	private Date before;
	private Manager<DocumentMongo> manager;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.ged.entrante.mongo.rs.servers.host", "lcid-mongodb:27017");
		props.setProperty("fr.cimut.ged.entrante.indexation.user", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.password", "cimut");
		props.setProperty("fr.cimut.ged.entrante.indexation.nom_database_collection", "documents");
		System.setProperties(props);
		InteractionMongo inter = new InteractionMongo();
		manager = MongoManagerFactory.getDocumentManager(envirTeste, "9916", inter);
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
	public void updateTest() {

		DocumentMongo document = new DocumentMongo();
		document.setId("9916_20141120182502_09594.pdf");
		document.setStatus(GlobalVariable.STATUS_ERROR);
		document.setTypeDocument("Bla blablabla ...");

		try {
			manager.update(document);
			DocumentMongo doc = manager.get(document.getId());
			System.out.println(doc.getStatus());
			assertTrue(doc.getStatus().equals(GlobalVariable.STATUS_ERROR));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//@Test
	public void documentTest() {
		DocumentMongo document = new DocumentMongo();
		document.setDtCreate(new DateTime());
		ObjectMapper mapper = new ObjectMapper();

		try {
			String json = mapper.writeValueAsString(document);
			System.out.println(json);
			document = mapper.readValue(json, DocumentMongo.class);
			System.out.println(document.getDtCreate().toLocalTime());
			System.out.println(document.getDtCreate().toDateTimeISO());

		} catch (JsonProcessingException e) {
			// 
			e.printStackTrace();
			//fail();
		} catch (IOException e) {
			// 
			e.printStackTrace();
			//fail();
		}

	}

	//@Test
	public void getTest() {

		BasicDBObject queryDoc = new BasicDBObject();
		queryDoc.put("STATUS", "A traiter");
		queryDoc.put("TYPE_COURRIER", "Entreprise");

		ObjectMapper mapper = new ObjectMapper();
		//		RuleDa ruleDa = new RuleDa(); 
		//		ruleDa.setId("332886");
		//		ruleDa.setName("ASPBTP");
		//		ruleDa.setSupport("CRC");
		//		ruleDa.setSujet("Objet de la demande");
		//		ruleDa.setType("GUI");

		try {
			DBCursor cursor = manager.getCursor(queryDoc);
			while (cursor.hasNext()) {
				String json = cursor.next().toString();
				//convert json in bean 
				System.out.println(json);
				DocumentMongo document = mapper.readValue(json, DocumentMongo.class);
				System.out.println(document.getDtCreate().toString() + " " + document.getDtCreate().toLocalDateTime() + " "
						+ document.getDtCreate().toDateTimeISO());
				System.out.println(document.getDtIntegration().toString() + " " + document.getDtIntegration().toLocalDateTime() + " "
						+ document.getDtIntegration().toDateTimeISO());
				//DocumentConverter converter = new DocumentConverter();
				// convert
				//Da da = converter.toDa(ruleDa,document);
				break;
			}
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}

	}

}
