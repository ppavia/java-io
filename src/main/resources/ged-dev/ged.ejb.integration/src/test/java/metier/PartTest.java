package metier;

import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.After;
import org.junit.Before;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.cimut.ged.entrant.appelmetier.MetierManager;
import fr.cimut.ged.entrant.appelmetier.PartManager;
import fr.cimut.ged.entrant.beans.appelmetier.Part;

public class PartTest {

	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	//@Test
	public void get() throws JsonProcessingException {
		try {
			PartManager partManager = new PartManager();
			Part part = partManager.get("290000017|ETAB|0000", "9970", envirTeste);
			System.out.println(part.getCodePostal());
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
