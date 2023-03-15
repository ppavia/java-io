package jpa;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

public class JpaTest {

	private Date before;
	private static EntityManagerFactory emf;
	private static EntityManager em;

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("START                                             #");
		System.out.println("###################################################");
		this.before = new Date();

		// Initialisation de la persistence
		emf = Persistence.createEntityManagerFactory("MyTest");
		em = emf.createEntityManager();

	}

	@After
	public void terminate() {
		System.out.println("###################################################");
		System.out.println("time : " + ((new Date().getTime() - this.before.getTime())) + " Msecs");
		System.out.println("END                                               #");
		System.out.println("###################################################");
		// Fermeture de la persistence
		if (em != null) {
			em.close();
		}
		if (emf != null) {
			emf.close();
		}
	}

	//@Test
	public void equalTest() {

		fr.cimut.ged.entrant.beans.db.Document doc = em.find(fr.cimut.ged.entrant.beans.db.Document.class, "SUDE_20160308_043750_8.msg");
		System.out.println(doc.getId());

	}

}
