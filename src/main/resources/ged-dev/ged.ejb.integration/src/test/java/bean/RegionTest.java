/**
 * @author gyclon
 */
package bean;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cimut.ged.entrant.beans.mongo.Region;

public class RegionTest {

	private Date before;

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

	@Test
	public void getRegion() {
		System.out.println("getRegion");
		assertTrue(Region.getRegion("97100").equals("Outre-mer"));
		assertTrue(Region.getRegion("97200").equals("Outre-mer"));
		assertTrue(Region.getRegion("97300").equals("Outre-mer"));
		assertTrue(Region.getRegion("97400").equals("Outre-mer"));
		assertTrue(Region.getRegion("97600").equals("Outre-mer"));
		assertTrue(Region.getRegion("95000").equals("Île-de-France"));
		assertTrue(Region.getRegion("1000").equals("Rhône-Alpes"));
	}

}
