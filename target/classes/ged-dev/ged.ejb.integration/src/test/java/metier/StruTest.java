package metier;

import static org.junit.Assert.fail;

import java.util.Date;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.appelmetier.MetierManager;
import fr.cimut.ged.entrant.appelmetier.StruManager;
import fr.cimut.ged.entrant.beans.appelmetier.Stru;

public class StruTest {

	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	//@Test
	public void getBySirenNic() {
		try {
			StruManager struManager = new StruManager();
			System.out.println("getBySirenNic");

			//, STRU_NIC_ALPH:, TYPSTRU_ID:ETAB,
			Stru stru = struManager.get("317238061|00038", "9970", envirTeste);
			//Stru stru = struManager.get("317238061|00038", "9970", envirTeste);
			System.out.println(stru.getId());
			System.out.println(stru.getNumInterne());
			System.out.println(stru.getType());
			System.out.println(stru.getClasse());
			System.out.println(stru.getSiren());

			if (!stru.getId().equals("0000362340")) {
				fail();
			}

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
	public void getSection1() {
		try {
			StruManager struManager = new StruManager();
			System.out.println("getSection");
			Stru stru = struManager.get("1||00", "4631", envirTeste);
			System.out.println(stru.getId());
			System.out.println(stru.getNumInterne());
			System.out.println(stru.getType());
			System.out.println(stru.getClasse());
			System.out.println(stru.getSiren());
			//
			if (!stru.getId().equals("0000000676")) {
				fail();
			}

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
	public void getSection() {
		try {
			StruManager struManager = new StruManager();
			System.out.println("getSection");
			Stru stru = struManager.get("1|Q|00", "8080", envirTeste);
			System.out.println(stru.getId());
			System.out.println(stru.getNumInterne());
			System.out.println(stru.getType());
			System.out.println(stru.getClasse());
			System.out.println(stru.getSiren());

			if (!stru.getId().equals("0005551920")) {
				fail();
			}

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
	public void get() {
		try {

			//0047400745 0001 S 777461385 00026 
			StruManager struManager = new StruManager();
			Stru stru = struManager.get("0001|Q", "8080", envirTeste);
			//Stru stru = struManager.get("0001|Q|00", "8080", envirTeste);
			//Stru stru = struManager.get("0000695520", "8080", envirTeste);

			System.out.println("get");

			System.out.println(stru.getId());
			System.out.println(stru.getNumInterne());
			System.out.println(stru.getType());
			System.out.println(stru.getClasse());
			System.out.println(stru.getSiren());

			if (!stru.getId().equals("0000362340")) {
				fail();
			}

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
