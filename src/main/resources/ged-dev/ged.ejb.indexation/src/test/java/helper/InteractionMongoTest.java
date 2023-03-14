package helper;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.exceptions.CimutXmlException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class InteractionMongoTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	private Date before;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		System.out.println("");
		this.before = new Date();
	}

	@After
	public void terminate() {
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

	//@Test 
	public void getErreur() {
		ObjectMapper mapper = new ObjectMapper();
		InteractionMongo inter = new InteractionMongo();
		BasicDBObject queryDoc = new BasicDBObject();
		queryDoc.put(GlobalVariable.ATTR_STATUS, "A traiter");
		Pattern john = Pattern.compile("^EDDM \\:");
		queryDoc.put(GlobalVariable.ATTR_DA_ID, john);
		try {
			Manager<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> documentManager = MongoManagerFactory.getDocumentManager(envirTeste, "9916", inter);
			DBCursor cursor = documentManager.getCursor(queryDoc);
			while (cursor.hasNext()) {
				String json = cursor.next().toString();
				fr.cimut.ged.entrant.beans.mongo.DocumentMongo document = null;
				if (json != null) {
					document = mapper.readValue(json, fr.cimut.ged.entrant.beans.mongo.DocumentMongo.class);
					System.out.println(document.getId());
				}
			}
		} catch (NumberFormatException e) {
			// 
			e.printStackTrace();
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
		} catch (JsonParseException e) {
			// 
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}

	}

	//@Test
	public void insertDate() {

		String path = "C:\\gedEntrante\\integration\\in\\new\\9916_20141021111946_00008.xml";

		try {
			Document docu = DocumentHelper.toDocument(new File(path), "9916_20141021111945_00000.pdf", envirTeste, null, null, null);
			System.out.println(docu.getJson().getData());

			InteractionMongo inter = new InteractionMongo();
			inter.insert(docu.getJson().getData(), "INTV3");

		} catch (CimutXmlException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (CimutDocumentException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// 
			e.printStackTrace();
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
		}

	}

	//@Test
	@SuppressWarnings("unused")
	public void test() {

		String user = "cimut";
		String password = "cimut";
		String environnement = "INTV3";
		String cmroc = "9916";
		String nom_serveur = "localhost";
		int serveur_port = 27017;
		String nom_collection = "documents";
		String json = "{\"SITE_SCAN\":\"Paris\",\"DIRECTION\":\"Entrant\",\"ID_ENTREPRISE\":\"236\",\"NUMERO_ENTREPRISE\":\"0236\",\"ORIGINAL\":\"true\",\"NPAI\":\"false\",\"TYPE_DOSSIER\":\"Cotisations\",\"DATE_CREATION\": {\"$date\": \"2013-02-07\"},\"TYPE_COURRIER\":\"Entreprise\",\"NOM_ENTREPRISE\":\"ANCERNE THIERRY\",\"TUTELLE\":\"9916\",\"PRIORITE\":\"2\",\"RECOMMANDE\":\"false\",\"MEDIA\":\"Courrier\",\"VILLE\":\"SAINT GERMAIN LE VASSON\",\"ID_ORGANISME\":\"9916\",\"DATABASE\":\"20141017\",\"ATTRIBUTS\":{},\"ID_DOC\":\"9916_20141021111946_00006.pdf\"}";
		String id_delete = "9916_20141022025026_00000.pdf";
		String id_update = "9916_20141022025026_00000.pdf";
		String id_select = "9916_20141021111946_00007.pdf";

		assertTrue(true);

		//testListJsonFile();
		testInsertjsonMongo(json, environnement);
		testDeletejsonMongo(id_delete, environnement);

		testUpdateMongo(id_update, json, environnement);
		testBasesMongo(environnement);

		//testSelectMongo("9916_20141022025026_00000.pdf");
		//testSelectMongo("9916_20141021111946_00007.pdf");
		//testSelect(user,id_select,password,nom_base,nom_serveur,serveur_port,nom_collection);

		//testRemoveDocInCollection(user,password,nom_base,nom_serveur,serveur_port,nom_collection);
		//testrepairDatabase(user, password, nom_base,nom_serveur,serveur_port,nom_collection)
		//testrebuildall(user, password, nom_base,nom_serveur,serveur_port,nom_collection);

	}

	public void testInsertjsonMongo(String json, String environnement) {
		InteractionMongo interactionMongo = null;

		try {
			interactionMongo = new InteractionMongo();
			interactionMongo.insert(json, environnement);

			System.out.println("Integration ok");
			interactionMongo.closeConnexion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testDeletejsonMongo(String id_delete, String environnement) {
		InteractionMongo interactionMongo = null;

		try {
			interactionMongo = new InteractionMongo();

			//String json = "{\"SITE_SCAN\":\"Caen\",\"DIRECTION\":\"Entrant\",\"ID_ENTREPRISE\":\"236\",\"NUMERO_ENTREPRISE\":\"0236\",\"ORIGINAL\":\"true\",\"NPAI\":\"false\",\"TYPE_DOSSIER\":\"Cotisations\",\"DATE_CREATION\":\"20141017\",\"TYPE_COURRIER\":\"Entreprise\",\"NOM_ENTREPRISE\":\"ANCERNE THIERRY\",\"TUTELLE\":\"9916\",\"PRIORITE\":\"2\",\"RECOMMANDE\":\"false\",\"MEDIA\":\"Courrier\",\"VILLE\":\"SAINT GERMAIN LE VASSON\",\"ID_ORGANISME\":\"9916\",\"DATABASE\":\"20141017\",\"ATTRIBUTS\":{},\"ID_DOC\":\"9916_20141021111946_00006.pdf\"}";

			interactionMongo.delete(id_delete, environnement);
			System.out.println("Delete ok");
			interactionMongo.closeConnexion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testUpdateMongo(String id_update, String json, String environnement) {
		InteractionMongo interactionMongo = null;
		try {
			interactionMongo = new InteractionMongo();

			interactionMongo.update(id_update, json, environnement);

			System.out.println("update ok");
			interactionMongo.closeConnexion();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//	public void testcreationCollectionMongo(){
	//		InteractionMongo interactionMongo = null;
	//		try {
	//			interactionMongo = new InteractionMongo("lcidxs6.cimut.fr",27017);
	//			interactionMongo.creationCollection("test","doccimut");
	//			interactionMongo.closeConnexion();
	//		} catch(UnknownHostException unknownHostException){
	//			unknownHostException.printStackTrace();
	//		} catch (CimutBaseNoExistException e) {
	//			e.printStackTrace();
	//		}
	//	}

	//	public void testNbrDocInCollection(){
	//		InteractionMongo interactionMongo = null;
	//		try {
	//			interactionMongo =  new InteractionMongo("localhost",27017,"cimut","docentrant","cimut","cimut");
	//			//interactionMongo.removeCollection();
	//			
	//			System.out.println("Nombre de documents present dans la collection : " + interactionMongo.getNbrDoc());
	//			interactionMongo.closeConnexion();
	//		} catch(UnknownHostException unknownHostException){
	//			unknownHostException.printStackTrace();
	//		} catch (CimutCollectionNoExistException e) {
	//			e.printStackTrace();
	//		} catch (Exception exception){
	//			exception.printStackTrace();
	//		}
	//	}

	public void testRemoveDocInCollection(String environnement) {
		InteractionMongo interactionMongo = null;
		try {
			interactionMongo = new InteractionMongo();
			//interactionMongo = new InteractionMongo("localhost",27017,"cimut","docentrant");
			//System.out.println("Nombre de documents present dans la collection : " + interactionMongo.getNbrDoc());

			interactionMongo.removeCollection(environnement);
			interactionMongo.closeConnexion();

			System.out.println("Suppression de l ensemble des documents present dans la collection OK ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testrepairDatabase(String environnement) {
		InteractionMongo interactionMongo = null;
		try {
			interactionMongo = new InteractionMongo();
			//interactionMongo = new InteractionMongo("localhost",27017,"cimut","docentrant");
			//System.out.println("Nombre de documents present dans la collection : " + interactionMongo.getNbrDoc());

			interactionMongo.repairDatabase(environnement);
			interactionMongo.closeConnexion();

			System.out.println("Reparation database OK ");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void testrebuildall(String environnement) {

		System.out.println("rebuildall");

		InteractionMongo interactionMongo = null;
		try {
			interactionMongo = new InteractionMongo();

			try {
				//suppression des documents de la collection
				interactionMongo.removeCollection(environnement);

				try {
					//reparation compactage de la base (operation optionnel)
					interactionMongo.repairDatabase(environnement);

					System.out.println("Fin rebuild: Le rebuild c est effectue sans erreur.");

				} catch (Exception e) {
					System.out.println("Erreur durant la reparation de la base " + environnement);
				}

				// appel de la fonction de regeneration des json d entree

				interactionMongo.closeConnexion();
			} catch (Exception e) {
				System.out.println("Erreur durant la suppression des documents de la base " + environnement);
			}

		} catch (Exception e) {
			System.out.println("Erreur : " + e.getMessage());
		}

	}

	@SuppressWarnings("unused")
	public void testSelect(String user, String id_select, String password, String nom_base, String nom_serveur, int serveur_port,
			String nom_collection) {
		InteractionMongo interactionMongo = null;

		//	System.out.println("Liste des bases Mongo:");
		//	BasicDBObject requete=new BasicDBObject("ID_DOC", id_select );
		//	
		//	
		//	try {
		//		interactionMongo = new InteractionMongo();
		//		List<DBObject> resultats =interactionMongo.select(requete,user, password,nom_base,nom_serveur,serveur_port,nom_collection);
		//		
		//		for(DBObject resultat: resultats){
		//			System.out.println(resultat.toString());
		//		}
		//		
		//		interactionMongo.closeConnexion();
		//	} catch (Exception e) {
		//		e.printStackTrace();
		//	} 

	}

	public void testBasesMongo(String environnement) {
		InteractionMongo interactionMongo = null;

		System.out.println("Liste des bases Mongo:");

		try {
			interactionMongo = new InteractionMongo();
			List<String> bases = interactionMongo.getBases(environnement);

			for (String base : bases) {
				System.out.println(base);
			}

			interactionMongo.closeConnexion();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
