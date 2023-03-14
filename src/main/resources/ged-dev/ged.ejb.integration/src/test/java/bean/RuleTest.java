package bean;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;

public class RuleTest {
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
	public void equalTestXX() {

		Long numAdherant1 = Long.valueOf(null);
		Long numAdherant2 = Long.valueOf("0dfgdgf");
		if (numAdherant1.equals(numAdherant2)) {
			fail();
		}
	}

	@Test
	public void equalTestXXX() {

		Long numAdherant1 = Long.valueOf("0008213645");
		Long numAdherant2 = Long.valueOf("8213645");
		if (!numAdherant1.equals(numAdherant2)) {
			fail();
		}
	}

	@Test
	public void equalTest() {
		System.out.println("equalTest");

		RuleCriteres a = new RuleCriteres();
		a.setId("A");
		a.setParameters(Arrays.asList("a", "b", "c"));
		RuleCriteres b = new RuleCriteres();
		b.setId("B");
		b.setParameters(Arrays.asList("d", "e", "f"));

		RuleCriteres c = new RuleCriteres();
		c.setId("A");
		c.setParameters(Arrays.asList("a", "b", "c"));
		RuleCriteres d = new RuleCriteres();
		d.setId("C");
		d.setParameters(Arrays.asList("d", "e", "f"));

		assertTrue(!a.equals(b));
		assertTrue(a.equals(c));
		assertTrue(!b.equals(d));

		List<RuleCriteres> lA = Arrays.asList(a, b);
		List<RuleCriteres> lB = Arrays.asList(c, d);

		assertTrue(!criteresEquals(lA, lB));

		Rule rule = new Rule();
		Rule previousRule = new Rule();

		rule.setActif(true);
		previousRule.setActif(true);

		rule.setId("A");
		previousRule.setId("B");

		rule.setPriority("2");
		previousRule.setPriority("3");

		rule.setCriteres(lA);
		previousRule.setCriteres(lB);

		RuleDa ra = new RuleDa();
		ra.setId("123456");
		ra.setType("CRC");
		ra.setSujet("blabla");
		ra.setSupport("DIV");
		ra.setName("ASPBTP");
		RuleDa rb = new RuleDa();
		rb.setId("123457");
		rb.setType("CRC");
		rb.setSujet("blablou");
		rb.setSupport("DIV");
		rb.setName("ASPBTP");

		rule.setService(ra);
		previousRule.setService(rb);

		// gestion de l'historique
		StringBuilder outputMsg = new StringBuilder();
		outputMsg.append("Modification");
		if (!criteresEquals(previousRule.getCriteres(), rule.getCriteres())) {
			outputMsg.append(" des criteres de selection, ");
		}
		if (!previousRule.getService().equals(rule.getService())) {
			outputMsg.append(" du parametrage DA : (");

			if (previousRule.getService().getId() == null) {
				outputMsg.append("Suppression de la DA, ");
			} else {

				if (!previousRule.getService().getId().equals(rule.getService().getId())) {
					outputMsg.append("service id:" + previousRule.getService().getId() + " => " + rule.getService().getId() + ", ");
				}
				if (!previousRule.getService().getName().equals(rule.getService().getName())) {
					outputMsg.append("service nom:" + previousRule.getService().getName() + " => " + rule.getService().getName() + ", ");
				}
				if (!previousRule.getService().getSupport().equals(rule.getService().getSupport())) {
					outputMsg.append("support:" + previousRule.getService().getSupport() + " => " + rule.getService().getSupport() + ", ");
				}
				if (!previousRule.getService().getSujet().equals(rule.getService().getSujet())) {
					outputMsg.append("sujet:" + previousRule.getService().getSujet() + " => " + rule.getService().getSujet() + ", ");
				}
				if (!previousRule.getService().getType().equals(rule.getService().getType())) {
					outputMsg.append("type:" + previousRule.getService().getType() + " => " + rule.getService().getType() + ", ");
				}
				outputMsg.deleteCharAt(outputMsg.length() - 2);
				outputMsg.append("), ");
			}
		}
		if (!previousRule.getPriority().equals(rule.getPriority())) {
			outputMsg.append(" de la priorité : " + previousRule.getPriority() + " => " + rule.getPriority() + ", ");
		}
		if (previousRule.isActif() != rule.isActif()) {
			outputMsg.append(((rule.isActif()) ? " Activation" : " Désactivation") + " de la règle, ");
		}

		// do not update if nothing has changed !
		if (outputMsg.toString().equals("Modification")) {
			return;
		}

		outputMsg.deleteCharAt(outputMsg.length() - 2);

		System.out.println(outputMsg);

	}

	private boolean criteresEquals(List<RuleCriteres> a, List<RuleCriteres> b) {
		if (a == null && b == null) {
			return true;
		}
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		if (a.size() != b.size()) {
			return false;
		}

		for (RuleCriteres bs : b) {
			boolean found = false;

			System.out.println(bs.getId() + " " + bs.getParameters());

			for (RuleCriteres as : a) {

				System.out.println("     " + as.getId() + " " + as.getParameters());

				if (bs.equals(as)) {
					found = true;
					System.out.println("FOUND ! " + bs.getId() + " " + as.getId());
					break;
				}
			}
			if (!found) {
				System.out.println("NOT FOUND !");
				return false;
			}
		}
		return true;
	}

}
