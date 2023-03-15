package ejb;

import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;

public class StatsTest {
	private Date before;

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("#   START                                         #");
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
		InteractionMongo inter = new InteractionMongo();
		HashMap<String, String> parameters = new HashMap<String, String>();
		String result = null;
		System.out.println("Spym");
		parameters.put("STATS_TYPE", "TYPE_DOSSIER");
		parameters.put("ID_ORGANISME", "9916");

		try {

			result = inter.getStats(envirTeste, parameters);

		} catch (Exception e) {
			System.out.println("erreur");
			System.out.println(e);
		}
		System.out.println("#   TYPE_COURRIER                                       #");
		System.out.println(result);

		//parameters.put("PAGESIZE", "100");
		//5472fad9e4b0eb0003de9bbe
		//5472fb89e4b0eb0003deec40
		//parameters.put("PAGENUM", "3");
		parameters.put("STATS_TYPE", "TYPE_DOSSIER");
		parameters.put("STATS_FILTER", "TYPE_COURRIER");
		parameters.put("STATS_FILTER_VAL", "Personne");
		try {

			result = inter.getStats(envirTeste, parameters);

		} catch (Exception e) {
			System.out.println("erreur");
			System.out.println(e);
		}
		System.out.println("#   TYPE_DOSSIER                                       #");
		System.out.println(result);
		parameters.put("DATE_CREATION_D", "20140201");
		parameters.put("DATE_CREATION_F", "20141101");
		parameters.put("STATS_TYPE", "GESTIONNAIRE");
		try {

			result = inter.getStats(envirTeste, parameters);

		} catch (Exception e) {
			System.out.println("erreur");
			System.out.println(e);
		}
		System.out.println("#   GESTIONNAIRE                                       #");
		System.out.println(result);

		//NOM_DE_FAMILLE
		parameters.put("STATS_FILTER", "TYPE_COURRIER");
		parameters.put("STATS_FILTER_VAL", "Personne");
		try {

			result = inter.getStats(envirTeste, parameters);

		} catch (Exception e) {
			System.out.println("erreur");
			System.out.println(e);
		}
		System.out.println("#   GESTIONNAIRE+FILTRE                                       #");
		System.out.println(result);

	}

	//@Test
	@SuppressWarnings("unused")
	public void benchSearchDocument() {
		InteractionMongo inter = new InteractionMongo();

		String result = null;
		int timer1 = 0;
		int timer2 = 0;
		int timer3 = 0;
		int timer4 = 0;
		long time = 0;
		int nbBoucle = 10;

		for (int i = 0; i < nbBoucle; i++) {
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("STATS_TYPE", "TYPE_COURRIER");
			parameters.put("ID_ORGANISME", "9916");
			time = new Date().getTime();
			try {
				result = inter.getStats(envirTeste, parameters);
			} catch (CimutMongoDBException e) {
				e.printStackTrace();
			}
			timer1 += (new Date().getTime() - time);

			parameters.put("STATS_TYPE", "TYPE_DOSSIER");
			time = new Date().getTime();
			try {
				result = inter.getStats(envirTeste, parameters);
			} catch (CimutMongoDBException e) {
				e.printStackTrace();
			}
			timer2 += (new Date().getTime() - time);

			parameters.put("DATE_CREATION_D", "20141001");
			parameters.put("DATE_CREATION_F", "20141101");
			parameters.put("STATS_TYPE", "GESTIONNAIRE");
			time = new Date().getTime();
			try {
				result = inter.getStats(envirTeste, parameters);
			} catch (CimutMongoDBException e) {
				e.printStackTrace();
			}
			timer3 += (new Date().getTime() - time);

			parameters.put("STATS_FILTER", "TYPE_COURRIER");
			parameters.put("STATS_FILTER_VAL", "Personne");
			time = new Date().getTime();
			try {
				result = inter.getStats(envirTeste, parameters);
			} catch (CimutMongoDBException e) {
				e.printStackTrace();
			}
			timer4 += (new Date().getTime() - time);
		}

		System.out.println("\n##############################################");
		System.out.println("############  BENCH RESULT       #############");
		System.out.println("##############################################");
		System.out.println("#TIME1 : " + (timer1 / nbBoucle) + " ms                              #");
		System.out.println("#TIME2 : " + (timer2 / nbBoucle) + " ms                              #");
		System.out.println("#TIME3 : " + (timer3 / nbBoucle) + " ms                              #");
		System.out.println("#TIME4 : " + (timer4 / nbBoucle) + " ms                              #");
		System.out.println("##############################################\n");
	}
}
