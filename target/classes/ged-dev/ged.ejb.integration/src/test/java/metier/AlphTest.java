package metier;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.cimut.ged.entrant.appelmetier.AlphManager;
import fr.cimut.ged.entrant.appelmetier.MetierManager;
import fr.cimut.ged.entrant.beans.appelmetier.Alph;

public class AlphTest {

	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	//@Test
	public void getBySassu() throws JsonProcessingException {
		try {
			AlphManager alphManager = new AlphManager();
			Alph alph = alphManager.get("30967", "9970", envirTeste);
			System.out.println(alph.getsAssu());
			System.out.println(alph.getInsee());
			System.out.println(alph.getCodePostal());
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
	public void getByInsee() throws JsonProcessingException {
		try {
			AlphManager alphManager = new AlphManager();
			Alph alph = alphManager.get("1740244109175", "9970", envirTeste);
			System.out.println(alph.getsAssu());
			System.out.println(alph.getInsee());
			System.out.println(alph.getCodePostal());
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
