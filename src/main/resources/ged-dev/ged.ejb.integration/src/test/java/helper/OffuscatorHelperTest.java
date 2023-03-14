package helper;

import java.util.Date;

import org.junit.After;
import org.junit.Before;

import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.utils.OffuscatorHelper;

public class OffuscatorHelperTest {
	private Date before;

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

	//@Test
	public void offuscate() {
		System.out.println(OffuscatorHelper.offuscateFolder("2013"));
		System.out.println(OffuscatorHelper.offuscateFolder("019"));
		System.out.println(OffuscatorHelper.offuscateFolder("8080"));
		try {
			System.out.println(OffuscatorHelper.offuscateId("9916_20141021111946_00008.pdf"));
		} catch (CimutFileException e) {
			// 
			e.printStackTrace();
		}
	}
}
