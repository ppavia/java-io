package ejb;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;

public class ManagerTest {

	private static final String PERSISTENCE_UNIT_NAME = "MyPersistence";
	private EntityManagerFactory factory;
	@SuppressWarnings("unused")
	private EntityManager entityManager;
	private Date before;

	@SuppressWarnings("unused")
	private static final String home = "C:\\gedEntrante\\";

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");
		System.out.println("");

		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		this.entityManager = factory.createEntityManager();

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
