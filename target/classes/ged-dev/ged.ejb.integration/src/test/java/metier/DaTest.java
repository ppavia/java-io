package metier;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cimut.ged.entrant.appelmetier.SudeManager;
import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.appelmetier.MetierManager;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.appelmetier.Sude;
import fr.cimut.ged.entrant.beans.appelmetier.SudeNote;
import fr.cimut.ged.entrant.beans.appelmetier.SudeNoteDocument;
import fr.cimut.ged.entrant.beans.appelmetier.DocumentConverter;
import fr.cimut.ged.entrant.beans.appelmetier.Eddm;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.mail.MailerHelper;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.DocumentHelper;

public class DaTest {

	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	//private static EntityManagerFactory emf;
	//private static EntityManager em;

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

		// Initialisation de la persistence
		//emf = Persistence.createEntityManagerFactory("MyPersistence");
		//em = emf.createEntityManager();

	}

	//@Test
	@SuppressWarnings("unused")
	public void createFromInjection() {

		String strippedContent = "";
		FileInputStream is = null;

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(0);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		document.setDtCreate(date);

		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setPriorite("2");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		String dummy = "B\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nF\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nS";
		dummy += "\n\n\n\n\n\n\n\n\nZ\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nA\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\nZ\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nZ\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nG\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nZ\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nG\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nE\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nF\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nZ\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nZ\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nB\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";
		dummy += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nE\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\nA";

		//String dummy = "&lt;p&gt;Bonjour,&lt;br /&gt;Ma demande concerne votre r&amp;eacute;ponse suite &amp;agrave; mon message du 14/06/2016&lt;br /&gt;&amp;quot;INFORMATIONS GARANTIE SANTE&lt;br /&gt;Bonjour,&lt;br /&gt;Suite &amp;agrave; votre demande, je vous informe que vous aviez souscrit &amp;agrave; une mutuelle compl&amp;eacute;mentaire SMEB'2 (&amp;agrave; 20 euros par mois) &amp;agrave; compter du 01/12/2015. Finalement vous l'avez r&amp;eacute;sili&amp;eacute; &amp;agrave; compter du 31/01/2016 car vous aviez d&amp;eacute;j&amp;agrave; une mutuelle compl&amp;eacute;mentaire avec vos parents.&amp;nbsp;&lt;br /&gt;En parall&amp;egrave;le, vous avez souscrit &amp;eacute;galement &amp;agrave; une assurance universitaire jusqu'au 30/09/2016.&lt;br /&gt;Vous avez donc cotis&amp;eacute; pour deux mois de mutuelle d'un montant total de 40 euros puis la cotisation &amp;agrave; l'assurance universitaire s'&amp;eacute;l&amp;egrave;ve &amp;agrave; 24 euros pour l'ann&amp;eacute;e, d'o&amp;ugrave; une cotisation totale de 64 euros.&lt;br /&gt;Si vous avez eu des soins durant la p&amp;eacute;riode du 01/12/15 au 31/01/2016, je vous invite &amp;agrave; nous faire parvenir vos factures acquitt&amp;eacute;es et/ou vos d&amp;eacute;comptes &amp;eacute;manant de votre nouvelle caisse de s&amp;eacute;curit&amp;eacute; sociale.&lt;br /&gt;Vous en souhaitant bonne r&amp;eacute;ception.&lt;br /&gt;Cordialement,&lt;br /&gt;Service Relation Clients&amp;quot;&lt;br /&gt;&lt;br /&gt;L'assurance universitaire ne prend-elle pas en charges les frais m&amp;eacute;dicales relatifs &amp;agrave; une visite m&amp;eacute;dicale chez un m&amp;eacute;decin g&amp;eacute;n&amp;eacute;raliste? D'autre part, pourquoi mon attestation de droit n'est-elle donc pas valable jusqu'au 30/09/2016 sur votre site alors que sur le document qui m'a &amp;eacute;t&amp;eacute; transmis par mail c'est le cas?&lt;br /&gt;&lt;br /&gt;Merci de votre attention,&lt;/p&gt; &lt;p&gt;Cordialement,&lt;/p&gt; &lt;p&gt;&amp;nbsp;&lt;/p&gt; &lt;p&gt;Arthur Fleurisson&lt;/p&gt;";

		//for (int i = 0; i < 1; i++) {

		try {

			document.setCommentaire(dummy);
			Sude da_ = da.create(ruleDa, document, null, envirTeste);
			System.out.println(da_.getId());

		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();

		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// }

	}

	//@Test
	public void createFromMail() {

		String strippedContent = "";
		FileInputStream is = null;

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(0);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		;
		document.setDtCreate(date);

		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setPriorite("2");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			File[] files = new File("src/test/resources/mails/").listFiles();
			for (int i = 0; i < files.length; i++) {

				if (!files[i].getName().endsWith(".eml")) {
					continue;
				}
				if (!"guillaume.longTexte.eml".equals(files[i].getName())) {
					continue;
				}
				try {
					// cas ou piece jointe inline dans text/plain
					is = new FileInputStream("src/test/resources/mails/" + files[i].getName());
					Message message = new MimeMessage(null, is);
					//List<fr.cimut.ged.entrant.beans.db.Document> documents = MailerHelper.getPiecesJointes(message, "9970");
					String sender = MailerHelper.getFromTo(message);
					Map<String, String> contents = MailerHelper.getMessage(message);
					String subject = MailerHelper.getSubject(message);
					strippedContent = MailerHelper.getStrippedContent(contents.get("content"), sender, subject);
					//System.out.println(strippedContent);
				} catch (Exception e) {
					System.out.println(e.getMessage());
					//throw e;
				} finally {
					try {
						if (is != null) {
							is.close();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				document.setCommentaire(strippedContent);
				Sude daa = da.create(ruleDa, document, null, envirTeste);
				System.out.println("SUDE ID GENERATED : " + daa.getId());
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void getDa() {

		SudeManager daManager;
		try {
			daManager = new SudeManager();
			List<String> listId = Arrays.asList("321997289");

			for (String string : listId) {
				Sude da = daManager.get(string, "8080", envirTeste);
				System.out.println(da.getId());
				assertTrue(da.getId() != null);
			}

		} catch (CimutConfException e) {
			e.printStackTrace();
			fail();
		} catch (CimutMetierException e) {
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * Cree et renvoie un nouvelle EDDOC_ID
	 * 
	 * @param json
	 * @return
	 * @throws CimutConfException
	 * @throws CimutMetierException
	 */
	private fr.cimut.ged.entrant.beans.db.Document getNewDoc(fr.cimut.ged.entrant.beans.mongo.DocumentMongo json)
			throws CimutConfException, CimutMetierException {

		fr.cimut.ged.entrant.beans.db.Document document = new fr.cimut.ged.entrant.beans.db.Document();
		document = DocumentHelper.setDefaultValue(document, envirTeste);
		Eddm eddm = null;
		EddmManager eddmManager = new EddmManager();

		try {

			String id = String.valueOf(1000 * Math.random());
			id = id.substring(0, id.indexOf("."));
			document.setId("identifiant" + id + ".pdf");
			document.setLibelle("identifiant" + id);
			Json json_ = new Json();
			json.addAttribute("EDDM_LIBELLE", document.getLibelle());
			json_.setData(new ObjectMapper().writeValueAsString(json));
			json_.setOrganisme(json.getCmroc());
			json_.setId(document.getId());
			document.setCmroc(json.getCmroc());
			document.setJson(json_);
			if (json.getTypeEntiteRattachement() != null) {
				eddm = eddmManager.create(document, new Metier(), envirTeste);
			} else {
				document.setTypeDocument("");
				eddm = eddmManager.createEmpty(document, envirTeste);
			}
			System.out.println(eddm.getDocId());
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		} finally {
			//eddmManager.remove(eddm.getDocId(), cmroc);
			//MetierManager.closeAll();
		}
		return document;
	}

	//@Test
	public void DaAssure() throws JsonProcessingException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(10);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		;
		document.setDtCreate(date);

		document.setCommentaire(
				"Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
						+ " Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
						+ " Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum"
						+ " Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum");
		//	document.setNom("YCLON");
		//	document.setPrenom("Guillaume");
		//document.setCodePostal("35700");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		//document.setTypeDossier("Libelle");
		document.setPriorite("2");
		//document.setNumAdherent("0312035790");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);
		// document.setAssuInsee("2280129232021");

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			fr.cimut.ged.entrant.beans.db.Document docu = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);

			DocumentConverter converter = new DocumentConverter();
			//			fr.cimut.ged.entrant.beans.appelmetier.Da beanDa = converter.toDa(ruleDa, document,
			//					Arrays.asList(docu, docu2));
			fr.cimut.ged.entrant.beans.appelmetier.Sude beanDa = converter.toSude(ruleDa, document, null);

			System.out.println(beanDa.getAddress1());
			System.out.println(beanDa.getAddress2());
			System.out.println(beanDa.getAddress3());
			//System.out.println(beanDa.getAssure());
			System.out.println(beanDa.getDateDemande());
			System.out.println(beanDa.getDateReception());
			System.out.println(beanDa.getObjDemande());
			System.out.println(beanDa.getOrganisme());
			System.out.println(beanDa.getPriorite());
			System.out.println(beanDa.getdTypeSupId());
			System.out.println(beanDa.getEntiteId());
			System.out.println(beanDa.getEntiteNom());
			System.out.println(beanDa.getTypeDmaId());

			SudeNote noteDa = beanDa.getNote();
			System.out.println("\t" + noteDa.getType());
			System.out.println("\t" + noteDa.getLibelle());
			System.out.println("\t" + noteDa.getSens());

			for (SudeNoteDocument doc : noteDa.getDocuments()) {
				System.out.println("------------------------------");
				System.out.println("\t\t" + doc.getEddocId());
				System.out.println("\t\t" + doc.getExtension());
				System.out.println("\t\t" + doc.getFileName());
			}
			Sude daa = da.create(ruleDa, document, Arrays.asList(docu, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());

		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaSansTexte() throws JsonProcessingException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			e2.printStackTrace();
			fail();
		}
		DateTime date = new DateTime();
		date = date.minusDays(10);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		;
		document.setDtCreate(date);
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setPriorite("2");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {
			fr.cimut.ged.entrant.beans.db.Document docu = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);
			Sude daa = da.create(ruleDa, document, Arrays.asList(docu, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();

		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaSansPiecesJointes() throws JsonProcessingException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(10);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		;
		document.setDtCreate(date);
		document.setCommentaire(
				"A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z A B C D E F G H I J K L M N O P Q R S T U V W X Y Z");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setPriorite("2");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {
			Sude daa = da.create(ruleDa, document, null, envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();

		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaSansRien() throws JsonProcessingException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(10);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		;
		document.setDtCreate(date);
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setPriorite("2");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {
			Sude daa = da.create(ruleDa, document, null, envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaAssureAccuseReception() throws JsonProcessingException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(10);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PERSONNE);
		;
		document.setDtCreate(date);
		document.setCommentaire("Test unitaire GEdEntrante auto DA creation II");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setTypeDocument("Libelle");
		document.setPriorite("2");
		document.setAssuInsee("2600450502297");
		document.setTypeDocument(Type.CODE_MAIL_DEMATERIALISE);
		document.addAttribute("ACCUSE_RECEPTION", "1");
		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			fr.cimut.ged.entrant.beans.db.Document docu = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);

			Sude daa = da.create(ruleDa, document, Arrays.asList(docu, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());

		} catch (Exception e1) {
			fail();
			e1.printStackTrace();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaSection() throws JsonProcessingException, CimutMetierException, CimutFileException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(1);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.ENTREPRISE);
		document.setIdEntreprise("0001|Q|00");
		document.setDtCreate(date);
		document.setCommentaire("Test unitaire GEdEntrante auto DA creation");
		document.setNom("YCLON");
		document.setPrenom("Guillaume");
		document.addAttribute("ADRESSE2", "14 rue de saint laurent");
		document.setCodePostal("35700");
		document.setVille("Rennes");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setId("1203162893761k.jpg");
		document.setTypeDocument("Libelle");
		document.setPriorite("2");
		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			fr.cimut.ged.entrant.beans.db.Document docu1 = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);
			Sude daa = da.create(ruleDa, document, Arrays.asList(docu1, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());

		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaStru() throws JsonProcessingException, CimutMetierException, CimutFileException {
		String cmroc = "9916";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(1);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.ENTREPRISE);
		document.setIdEntreprise("0001|J|01");
		document.setDtCreate(date);
		document.setCommentaire("Test unitaire GEdEntrante auto DA creation");
		document.setNom("YCLON");
		document.setPrenom("Guillaume");
		document.addAttribute("ADRESSE2", "14 rue de saint laurent");
		document.setCodePostal("35700");
		document.setVille("Rennes");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setId("1203162893761k.jpg");
		document.setTypeDocument("Libelle");

		document.setPriorite("2");
		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			fr.cimut.ged.entrant.beans.db.Document docu1 = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);

			Sude daa = da.create(ruleDa, document, Arrays.asList(docu1, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());

		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	//@Test
	public void DaUnaffect() throws JsonProcessingException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(1);
		DocumentMongo document = new DocumentMongo();
		document.setDtCreate(date);
		document.setCommentaire("Test unitaire GEdEntrante auto DA creation");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setTypeDocument("Libelle");
		document.setPriorite("2");

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			fr.cimut.ged.entrant.beans.db.Document docu = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);
			Sude daa = da.create(ruleDa, document, Arrays.asList(docu, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());

		} catch (Exception e1) {
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void DaPart() throws JsonProcessingException, CimutMetierException, CimutFileException {

		String cmroc = "8080";

		SudeManager da = null;
		try {
			da = new SudeManager();
		} catch (CimutConfException e2) {
			// 
			e2.printStackTrace();
			fail();
		}

		DateTime date = new DateTime();
		date = date.minusDays(1);
		DocumentMongo document = new DocumentMongo();
		document.setTypeEntiteRattachement(TypeEntite.PARTENAIRE);
		document.setIdProf("101000024|PS|0000");
		document.setDtCreate(date);
		document.setCommentaire("Test unitaire GEdEntrante auto DA creation");
		document.setNom("YCLON");
		document.setPrenom("Guillaume");
		document.addAttribute("ADRESSE2", "14 rue de saint laurent");
		document.setCodePostal("35700");
		document.setVille("Rennes");
		document.setCmroc(cmroc);
		document.setTutelle(cmroc);
		document.setId("1203162893761k.jpg");
		document.setTypeDocument("Libelle");

		document.setPriorite("2");

		RuleDa ruleDa = new RuleDa();
		ruleDa.setId("337632");
		ruleDa.setName("8080");
		ruleDa.setSupport("EXT");
		ruleDa.setSujet("Objet de la demande");
		ruleDa.setType("FAS");

		try {

			fr.cimut.ged.entrant.beans.db.Document docu1 = getNewDoc(document);
			fr.cimut.ged.entrant.beans.db.Document docu2 = getNewDoc(document);

			Sude daa = da.create(ruleDa, document, Arrays.asList(docu1, docu2), envirTeste);
			System.out.println("SUDE ID GENERATED : " + daa.getId());

		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("MESSAGE : " + e1.getMessage() + "*");
			fail();
		} finally {
			try {
				MetierManager.closeAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//@Test
	public void deleteDa() {
		SudeManager da = null;
		try {
			da = new SudeManager();
			try {
				da.remove("85", "9997", envirTeste);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				fail();
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@After
	public void terminate() {
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("###################################################");
		System.out.println("");
		System.out.println("");
	}

}
