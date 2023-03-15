package ejb;

import java.util.Date;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.mongo.InteractionMongo;

public class SearchTest {
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
		InteractionMongo inter = new InteractionMongo();
		HashMap<String, String> parameters = new HashMap<String, String>();
		String result = null;
		/*	public static final String ATTR_OC = "ORGANISME";
		
		//attributs d une recherche
		public static final String ATTR_DATE_CREATION = "DATE_CREATION";
		public static final String ATTR_DATE_CREATION_D = "DATE_CREATION_D";
		public static final String ATTR_DATE_CREATION_F = "DATE_CREATION_F";
		public static final String ATTR_TYPE_COURRIER = "TYPE_COURRIER";
		public static final String ATTR_TYPE_DOSSIER = "TYPE_DOSSIER";
		public static final String ATTR_ORGANISME = "ORGANISME";
		public static final String ATTR_TUTELLE = "TUTELLE";
		public static final String ATTR_STATUS = "STATUS";
		*/
		parameters.put("DATE_CREATION_D", "06012011");
		parameters.put("DATE_CREATION_F", "10052016");
		//parameters.put("TYPE_DOSSIER", "REGEX_.*TP.*");

		//parameters.put("TYPE_DOSSIER", "TP - Radios");
		parameters.put("ID_ORGANISME", "9911");
		parameters.put("PAGESIZE", "100");
		//5472fad9e4b0eb0003de9bbe
		//5472fb89e4b0eb0003deec40
		parameters.put("PAGENUM", "0");

		try {

			result = inter.getList(envirTeste, parameters);

		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
		}

		System.out.println(result);

		/*
		parameters.put("DATE_CREATION_D", "20140701");
		parameters.put("DATE_CREATION_F", "20141001");
		parameters.put("TYPE_DOSSIER", "REGEX_.*TP.*");
		try {
			
			result = inter.getList(parameters);
			
		} catch (CimutMongoDBException e) {
			// 
			e.printStackTrace();
		}
		
		System.out.println(result);
		*/
	}

	//@Test
	@SuppressWarnings("unused")
	public void benchSearchDocument() {
		InteractionMongo inter = new InteractionMongo();

		String result = null;
		int timer1 = 0;
		int timer2 = 0;
		long time = 0;
		int nbBoucle = 1000;

		for (int i = 0; i < nbBoucle; i++) {
			HashMap<String, String> parameters = new HashMap<String, String>();
			parameters.put("DATE_CREATION_D", "20140201");
			parameters.put("DATE_CREATION_F", "20140301");
			parameters.put("TYPE_DOSSIER", "TP - Radios");
			parameters.put("ID_ORGANISME", "9916");
			parameters.put("PAGESIZE", "100");
			parameters.put("PAGENUM", "3");

			time = new Date().getTime();
			try {
				result = inter.getList(envirTeste, parameters);
			} catch (CimutMongoDBException e) {
				e.printStackTrace();
			}
			timer1 += (new Date().getTime() - time);

			parameters.put("DATE_CREATION_D", "20140701");
			parameters.put("DATE_CREATION_F", "20141001");
			parameters.put("TYPE_DOSSIER", "REGEX_.*TP.*");
			time = new Date().getTime();
			try {
				result = inter.getList(envirTeste, parameters);
			} catch (CimutMongoDBException e) {
				e.printStackTrace();
			}
			timer2 += (new Date().getTime() - time);

		}

		System.out.println("\n##############################################");
		System.out.println("############  BENCH RESULT       #############");
		System.out.println("##############################################");
		System.out.println("#TIME1 : " + (timer1 / nbBoucle) + " ms                               #");
		System.out.println("#TIME2 : " + (timer2 / nbBoucle) + " ms                               #");
		System.out.println("##############################################\n");
	}

}
