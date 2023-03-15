package helper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.FileHelper;
import fr.cimut.ged.entrant.utils.OrganismeHelper;

public class DocumentHelperTest {
	private Date before;

	@SuppressWarnings("unused")
	private static final String home = "C:\\gedEntrante\\";

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

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

	@Test
	public void testSanitize() {
		"Hyderaanegeas.pdf".equals(DocumentHelper.sanitize("Hydéràanègeas.pdf"));
		"Hyd-eraanegeas.pdf".equals(DocumentHelper.sanitize("Hyd-éràanè	geas.pdf"));
		"Hyd-era_anegeas.pdf".equals(DocumentHelper.sanitize("Hyd-érà_anè	geas.pdf"));
	}

	@Test
	public void testTruncate() {

		// Gestion du truncate sur des string de 50 chars mais plus de 50 octets

		// 50 chars
		String weirdStr = "ŸŸŸŸŸŸŸŸŸŸ" + "ŸŸŸŸŸŸŸŸŸŸ" + "ŸŸŸŸŸŸŸŸŸŸ" +"ŸŸŸŸŸŸŸŸŸŸ" +"ŸŸŸŸŸŸŸŸŸŸ";

		assertTrue(weirdStr.length() == 50);
		assertTrue(weirdStr.getBytes().length > 50);

		String weirdTruncated = DocumentHelper.getTruncatedString(weirdStr, "default value", 50);

		assertTrue(weirdTruncated.length() <= 50);
		assertTrue(weirdTruncated.getBytes().length <= 50);
		assertTrue(weirdStr.contains(weirdTruncated));

		// Gestion du truncate sur des petites strings

		String simpleStr = "abcdŸŸ";

		String simpleTruncated = DocumentHelper.getTruncatedString(simpleStr, "default value", 50);

		assertTrue(simpleStr.length() == simpleTruncated.length());

		// Gestion du truncate sur des strings vides

		String emptyStr = "";

		String emptyStrTruncated = DocumentHelper.getTruncatedString(emptyStr, "default value", 50);

		assertTrue(emptyStrTruncated.equals("default value"));

		// Gestion du truncate sur des strings vides (BIS)

		String emptyStrBis = "";

		String emptyStrTruncatedBis = DocumentHelper.getTruncatedString(emptyStrBis, "", 50);

		assertTrue(emptyStrTruncatedBis.equals(""));

		// Gestion du truncate sur des strings vides

		String nullStr = null;

		String nullStrTruncated = DocumentHelper.getTruncatedString(nullStr, "default value", 50);

		assertTrue(nullStrTruncated.equals("default value"));
	}

	@Test
	public void testContainTutelles() {
		try {
			assertTrue(OrganismeHelper.getTutelles("8080").contains("8080"));
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void xmlToDocumentTest() {
		File xml = new File("C:/gedEntrante/integration/in/new/9916_20141021111946_00009.xml");
		Document document;
		try {
			document = DocumentHelper.toDocument(xml, "12837468_0139746.pdf", envirTeste, null, null, null);
			System.out.println(document.getJson().getData());
			DocumentHelper.validate(document);
			fr.cimut.ged.entrant.beans.mongo.DocumentMongo doc = DocumentHelper.getDocMongoFromJson(document);
			System.out.println("ADRESSE 1 : " + doc.getAttribute("ADRESSE1"));
		} catch (Exception e) {
			// 
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void mergeBeanTest() throws CimutConfException {

		Document oldDocument = new Document();

		try {

			fr.cimut.ged.entrant.beans.mongo.DocumentMongo oldJson = new fr.cimut.ged.entrant.beans.mongo.DocumentMongo();
			oldJson.setId("AV.pdf");
			oldJson.setTutelle("9916");
			oldJson.setCmroc("9916");
			oldJson.setCodePostal("35700");
			oldJson.setDtCreate(new DateTime());
			oldJson.setDtIntegration(new DateTime());
			oldJson.setStatus("A traiter");
			oldJson.setTypeEntiteRattachement(TypeEntite.PERSONNE);
			oldJson.setNumAdherent("45889");
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put("MACHIN", "Trucs");
			oldJson.setAttributes(attributes);
			oldDocument.setId(oldJson.getId());
			oldDocument.setMimeType("application/pdf");
			oldDocument.setDtcreate(oldJson.getDtCreate().toDate());
			oldDocument.setTypeDocument(oldJson.getTypeDocument());
			oldDocument.setCmroc(oldJson.getTutelle());
			oldDocument = DocumentHelper.setDefaultValue(oldDocument, envirTeste);
			oldDocument.setIdstar(123894723L);
			oldDocument.setTsstar(12394723L);

			fr.cimut.ged.entrant.beans.mongo.DocumentMongo newJson = new fr.cimut.ged.entrant.beans.mongo.DocumentMongo();
			newJson.setId("AV.xls");
			//newJson.setTutelle("9916");
			//newJson.setCmroc("9915");
			//newJson.setCodePostal("35000");
			//newJson.setDtCreate(new DateTime());
			//newJson.setDtIntegration(new DateTime());
			//newJson.setStatus("Traiter");
			//newJson.setTypeDocument("Personne");
			//newJson.setNumAdherent("45888");

			//Map<String, String> attributes_ = new HashMap<String, String>();
			//attributes_.put("MACHIN","Bidule");
			//newJson.setAttributes(attributes_);

			//newDocument.setDtcreate(newJson.getDtCreate().toDate());
			//newDocument.setTypedocument(newJson.getTypeDocument());
			//newDocument.setCmroc(newJson.getTutelle());
			//newDocument.setId(newJson.getId());
			//newDocument = DocumentHelper.setDefaultValue(newDocument);

			oldDocument.getJson().setData(DocumentHelper.stringify(oldJson));

			Document newDocument = new Document();
			newDocument.getJson().setData(DocumentHelper.stringify(oldJson));

			System.out.println("data before : " + newDocument.getJson().getData());
			newDocument = DocumentHelper.mergeBean(oldDocument, newDocument);
			System.out.println("data after  : " + newDocument.getJson().getData());
			System.out.println("          ");

			System.out.println("id : " + newDocument.getId());
			System.out.println("TypeDoc : " + newDocument.getTypeDocument());
			System.out.println("TypeMime : " + newDocument.getMimeType());
			System.out.println("cmroc : " + newDocument.getCmroc());
			System.out.println("Site : " + newDocument.getSite());
			System.out.println("Libelle : " + newDocument.getLibelle());
			System.out.println("dtCreate : " + newDocument.getDtcreate());
			System.out.println("idStar : " + newDocument.getIdstar());

			System.out.println("#############################################");
			System.out.println(DocumentHelper.diff(oldJson, newJson));
			System.out.println("#############################################");

			System.out.println("full : " + newDocument.getJson().getData());

		} catch (CimutDocumentException e) {
			// 
			e.printStackTrace();
		}
	}

	//@Test
	public void xmlToDocument() {

		Map<String, List<File>> map = null;
		try {
			map = FileHelper.listXmlFile(envirTeste);
		} catch (CimutFileException e1) {
			// 
			e1.printStackTrace();
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
		}

		for (String key : map.keySet()) {

			System.out.println(map.get(key).get(0).getAbsoluteFile());

			File xml = map.get(key).get(0);
			File doc = map.get(key).get(1);
			Document document = null;

			try {
				// on convertis en document
				document = DocumentHelper.toDocument(xml, doc.getName(), envirTeste, null, null, null);

				System.out.println(document.getJson().getData());

				assertTrue(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
