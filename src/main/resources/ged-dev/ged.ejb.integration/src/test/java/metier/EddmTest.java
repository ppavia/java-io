package metier;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.appelmetier.MetierManager;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class EddmTest {

	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "RECV";

	//@Test
	public void updateEddmTest() throws JsonProcessingException {
		//

		try {
			EddmManager eddmManager = new EddmManager();
			//eddmManager.remove("000016305972_20150226204025", "9916");

			Document document = new Document();
			document.setId("9916_20141021111946_00087.doc");
			document.setTypeDocument("Test unitaire");
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonColumn = mapper.createObjectNode();
			jsonColumn.put(GlobalVariable.ATTR_ID_ENTREPRISE, "1");
			jsonColumn.put(GlobalVariable.ATTR_TYPE_DOSSIER, "Test unitaire");
			jsonColumn.put(GlobalVariable.ATTR_ID_ORGANISME, "9916");
			jsonColumn.put(GlobalVariable.ATTR_TUTELLE, "9916");
			jsonColumn.put(GlobalVariable.ATTR_TYPEDOC, "Entreprise");
			jsonColumn.put(GlobalVariable.ATTR_EDDOC_ID, "000016306092_20150302150719");

			Json json = new Json();
			json.setData(mapper.writeValueAsString(jsonColumn));
			document.setJson(json);
			eddmManager.update(document, envirTeste);

		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
			fail();
		} catch (CimutMetierException e) {
			// 
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void EddmEmpty() throws JsonProcessingException {

		try {

			Document document = new Document();
			document.setId("9916_20141021111946_00087.pdf");
			document.setTypeDocument(GlobalVariable.TYPE_PARTENAIRE);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonColumn = mapper.createObjectNode();
			jsonColumn.put(GlobalVariable.ATTR_ID_PROF, "290000017|ETAB|0000");
			jsonColumn.put(GlobalVariable.ATTR_TYPE_DOSSIER, GlobalVariable.TYPE_PARTENAIRE + " - truc");
			jsonColumn.put(GlobalVariable.ATTR_ID_ORGANISME, "8080");
			jsonColumn.put(GlobalVariable.ATTR_TUTELLE, "8080");
			jsonColumn.put(GlobalVariable.ATTR_TYPEDOC, GlobalVariable.TYPE_PARTENAIRE);

			Json json = new Json();
			json.setData(mapper.writeValueAsString(jsonColumn));
			document.setJson(json);
			EddmManager eddmManager = new EddmManager();
			fr.cimut.ged.entrant.beans.appelmetier.Eddm eddm = eddmManager.createEmpty();

			System.out.println(eddm.getDocId());
			System.out.println(eddm.getPartId());
			System.out.println(eddm.getTypPartId());
			System.out.println(eddm.getPartNiv());
		} catch (Exception e) {
			// 
			e.printStackTrace();
			fail();
		} finally {
			MetierManager.closeAll();
		}
	}

	//@Test
	public void EddmDelete() throws JsonProcessingException {
		String eddoc_ID = "000024938037_20160615201203";
		try {
			EddmManager eddmManager = new EddmManager();
			eddmManager.remove(eddoc_ID, "9997", "VAL");
		} catch (CimutConfException e) {
			e.printStackTrace();
		} catch (CimutMetierException e) {
			// 
			e.printStackTrace();
		}

	}

	//@Test
	public void Eddm() throws JsonProcessingException {

		try {

			Document document = new Document();
			document.setId("8080_20151021111946_00018.pdf");
			//document.setTypedocument(GlobalVariable.TYPE_ENTREPRISE);
			document.setTypeDocument(GlobalVariable.TYPE_PARTENAIRE);
			ObjectMapper mapper = new ObjectMapper();
			ObjectNode jsonColumn = mapper.createObjectNode();
			jsonColumn.put(GlobalVariable.ATTR_ID_PROF, "290000017|ETAB|0000");
			//jsonColumn.put(GlobalVariable.ATTR_ID_ENTREPRISE,"0001|Q|00");
			// jsonColumn.put(GlobalVariable.ATTR_CLASSE_ENTRPRISE,"S");
			// jsonColumn.put(GlobalVariable.ATTR_NUM_ADHERENT,"427760");
			jsonColumn.put(GlobalVariable.ATTR_TYPE_DOSSIER, GlobalVariable.TYPE_ENTREPRISE + " - test Unitaire (jUnit)");
			jsonColumn.put(GlobalVariable.ATTR_ID_ORGANISME, "8080");
			jsonColumn.put(GlobalVariable.ATTR_TUTELLE, "8080");
			jsonColumn.put(GlobalVariable.ATTR_TYPEDOC, document.getTypeDocument());

			Json json = new Json();
			json.setData(mapper.writeValueAsString(jsonColumn));
			document.setJson(json);

			EddmManager eddmManager = new EddmManager();

			fr.cimut.ged.entrant.beans.appelmetier.Eddm eddm = eddmManager.create(document, new Metier(), "REC");
			System.out.println("EDDOCID " + eddm.getDocId());
			eddm = eddmManager.create(document, new Metier(), "RECV");
			//			System.out.println(eddm.getPartId());
			//			System.out.println(eddm.getTypPartId());
			//			System.out.println(eddm.getPartNiv());
			//System.out.println("idEntreprise "+eddm.getIdentreprise());
			//System.out.println("sys idEntreprise "+eddm.getIdSystemEntreprise());
			System.out.println("EDDOCID " + eddm.getDocId());
			eddm = eddmManager.create(document, new Metier(), "REC");
			System.out.println("EDDOCID " + eddm.getDocId());
			//System.out.println(eddm.getIdentreprise());
			//			System.out.println(eddm.getEtat());
		} catch (Exception e) {
			// 
			e.printStackTrace();
			fail();
		} finally {
			MetierManager.closeAll();
		}
	}

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		System.out.println("");
		this.before = new Date();

		Properties props = System.getProperties();
		props.setProperty("fr.cimut.util.path.properties", "src/test/resources/");
		props.setProperty("fr.cimut.ged.entrante.environnement", "INTV3");
		System.setProperties(props);

	}

	@After
	public void terminate() {
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

}
