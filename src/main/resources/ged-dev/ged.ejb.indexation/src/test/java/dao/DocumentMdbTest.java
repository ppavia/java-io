package dao;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class DocumentMdbTest {

	private Date before;

	//@Test
	public void add() {

		DocumentMongo document = new DocumentMongo();
		document.setId("sdfgsdfg.pdf");

		document.setCmroc("cmroc");
		document.setCodePostal("codePostal");
		document.setCommentaire("commentaire");
		document.setSudeId("da");
		document.setDepartement("departement");

		document.setDtCreate(new DateTime("03022014"));
		document.setDtIntegration(new DateTime());
		document.setEddocId("eddocId");

		document.setId("id");
		document.setIdEntreprise("idEntreprise");
		document.setIdProf("idProf");
		document.setTypeDocument("libelle");
		document.setNom("nom");
		document.setPrenom("prenom");
		document.setPriorite("priorite");
		document.setRegion("region");
		document.setStatus("status");
		document.setTutelle("tutelle");
		document.setTypeEntiteRattachement(TypeEntite.INCONNU);

		document.addAttribute(GlobalVariable.ATTR_MEDIA, "media");
		document.addAttribute(GlobalVariable.ATTR_DIRECTION, "direction");
		document.addAttribute(GlobalVariable.ATTR_ASSU_INSEE, "assuInsee");
		document.addAttribute(GlobalVariable.ATTR_GESTIONNAIRE, "gestionnaire");

		document.addAttribute("TEST", "toto");
		document.addAttribute("TEST1", "tata");
		document.addAttribute("TEST2", "tonton");

		ObjectMapper mapper = new ObjectMapper();

		try {
			System.out.println(mapper.writeValueAsString(document));
			DBObject mongoDBObject = (DBObject) JSON.parse(mapper.writeValueAsString(document));
			document = mapper.readValue(mongoDBObject.toString(), DocumentMongo.class);

			System.out.println(document.getDtCreate().toLocalDateTime());
			System.out.println(document.getAttribute("TEST"));
			System.out.println(document.getAttribute("TEST1"));
			System.out.println(document.getAttribute("TEST2"));

		} catch (JsonProcessingException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		}

	}

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
}
