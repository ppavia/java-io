package rest;

import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cimut.ged.entrant.appelP360.P360Manager;
import fr.cimut.ged.entrant.beans.p360.ReponseSearch360Agregee;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class P360Test {

	private final static P360Manager managerP360 = new P360Manager();

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.p360.url", "http://lcid-star-01.cimut.fr:8180/starwebWS/Search360"); // devc
		props.setProperty("fr.cimut.ged.entrante.cmroc.file.path", "src/test/resources/complement_cmroc.csv");
		System.setProperties(props);
		System.out.println("STARTED");
	}

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

	//@Test
	public void getAdherent() throws CimutConfException {
		try {

			fr.cimut.ged.entrant.beans.mongo.DocumentMongo json = managerP360.getAffectation("2700762261023", "7070", new Metier(), envirTeste);

			ObjectMapper mapper = new ObjectMapper();
			try {
				System.out.println(mapper.writeValueAsString(json));

				//				System.out.println(json.getAssuInsee());
				//				System.out.println(json.getRang());
				//				System.out.println(json.getCmroc());
				//				System.out.println(json.getNom());
				//				System.out.println(json.getPrenom());
				//				System.out.println(json.getVille());
				//				System.out.println(json.getCodePostal());
				//				System.out.println(json.getNumAdherent());

			} catch (JsonProcessingException e) {
				// 
				e.printStackTrace();
			}

		} catch (CimutMetierException e) {
			// 
			e.printStackTrace();
		} catch (CimutDocumentException e) {
			// 
			e.printStackTrace();
		}

	}

	//@Test
	public void getAgregeTest() {
		//String email = "JEANNINE.AUBINAIS@FREE.FR"; // assuré
		//String email = "04092012@NC.FR"; // assuré
		//String email = "0231820490"; // partenaire
		String email = "WEBMASTER@RISKASSUR-HEBDO.ORG";
		//String email = "FAM.PHILIPPART@WANADOO.FR";
		//String email = "0545978350"; // entreprise MBA !!!!!
		P360Manager managerP360 = new P360Manager();
		try {
			fr.cimut.ged.entrant.beans.mongo.DocumentMongo json = managerP360.getAffectation(email, "9970", new Metier(), envirTeste);
			if (GlobalVariable.STATUS_NOAFFECT.equals(json.getStatus())) {
				System.out.println("email " + GlobalVariable.STATUS_NOAFFECT);
			}

			System.out.println(new ObjectMapper().writeValueAsString(json));
		} catch (CimutMetierException e) {
			// 
			e.printStackTrace();
		} catch (CimutDocumentException e) {
			// 
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// 
			e.printStackTrace();
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
		}

	}

	@Test
	public void getAdherentSimple() throws CimutConfException, CimutMetierException {

		//String email = "04092012@NC.FR";
		//String email = "0231820490"; // partenaire
		String email = "johann.lebourgocq@cimut.fr"; // entreprise MBA !!!!!

		// test sur différents cmroc
		ReponseSearch360Agregee reponse = managerP360.get(email, "9970");
		Assert.assertNotNull(reponse);

		ReponseSearch360Agregee reponse9929 = managerP360.get(email, "9929");
		Assert.assertNotNull(reponse9929);

	}
}