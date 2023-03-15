package ejb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.service.Recherche;

public class SearchParamTest {
	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();
	}

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("END                                               #");
		System.out.println("###################################################");
	}

	//@Test
	public void searchDocument() {
		Recherche ejb = new Recherche();

		Map<String, String> search = new HashMap<String, String>();
		search.put("id", "TYPE_DOSSIER");
		search.put("q", "é");
		search.put("page", "1");
		search.put("pageSize", "10");

		try {
			Parameter param = ejb.getParameters(envirTeste, "9916", search);
			System.out.println(param.getList().toString());
		} catch (Exception e) {
			// 
			e.printStackTrace();
		}

	}

}
