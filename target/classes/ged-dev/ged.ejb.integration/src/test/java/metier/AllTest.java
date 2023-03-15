package metier;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Properties;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.cimut.ged.entrant.appelmetier.SudeManager;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.appelmetier.Sude;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;

public class AllTest {

	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	//@Test
	public void Da() throws JsonProcessingException {

		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		document.setNumAdherent("421971");
		document.setDtCreate(new DateTime());
		document.setCommentaire("Test unitaire GEdEntrante auto DA création");
		document.setNom("YCLON");
		document.setPrenom("Guillaume");
		document.addAttribute("ADRESSE1", "14 rue de saint laurent");
		document.setCodePostal("35700");
		document.setVille("Rennes");
		document.setPriorite("2");
		document.setCmroc("9916");
		document.setId("12031628937613k.pdf");
		document.setTypeDocument("Libellé");

		try {

			RuleDa ruleDa = new RuleDa();
			ruleDa.setId("332886");
			ruleDa.setName("ASPBTP");
			ruleDa.setSupport("EXT");
			ruleDa.setSujet("Objet de la demande");
			ruleDa.setType("IJC");

			//Eddm eddm =  eddmManager.create(converter.toEddm(document));
			//System.out.println(eddm.getDocId());

			//Da da = daManager.create(converter.toDa(ruleDa));
			SudeManager daManager = new SudeManager();
			Sude da = daManager.create(ruleDa, document, envirTeste);
			System.out.println(da.getId());

			//da = daManager.create(converter.toDa(ruleDa));
			System.out.println(da.getId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

	}

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		System.out.println("");
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.environnement", "INTV3");
		System.setProperties(props);
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
