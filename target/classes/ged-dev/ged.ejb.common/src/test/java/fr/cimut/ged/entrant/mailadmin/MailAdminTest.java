package fr.cimut.ged.entrant.mailadmin;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;

import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMailException;
import fr.cimut.ged.entrant.mail.CompteMail;
import fr.cimut.ged.entrant.mail.CompteMailManager;
import fr.cimut.ged.entrant.service.CompteMailFileService;

public class MailAdminTest {

	//@Test
	public void testMailBoxesConnection() throws CimutConfException {

		System.setProperty("fr.cimut.util.path.properties", "T:\\jboss\\jboss-as-7.1.0.Final\\standalone\\configuration\\");

		List<String> koMailboxList = new ArrayList<String>();
		CompteMailFileService service = new CompteMailFileService();
		List<CompteMail> comptes = service.getAllComptesMailFromFiles();
		for (CompteMail compte : comptes) {
			try {
				CompteMailManager manager = new CompteMailManager(compte);
			} catch (CimutMailException e) {
				koMailboxList.add(compte.getEmail());
			}
		}
		Assert.assertTrue("Impossible de se connecter aux boîtes suivantes : " + koMailboxList.toString(), koMailboxList.isEmpty());
	}

	//@Test
	public void testMailBoxConnection() throws CimutConfException {
		//String email = "teci@groupemgc.fr";
		String email = "sude@acorismutuelles.fr";
		//String email = "thomas.sorreau-ext@cimut.fr";
		try {
			testMailBoxConnection(email);
		} catch (CimutMailException e) {
			Assert.fail("Impossible de se connecter à la boite : " + email);
		}
	}

	public static CompteMailManager testMailBoxConnection(String email) throws CimutConfException, CimutMailException {
		System.setProperty("fr.cimut.util.path.properties", "T:\\jboss\\jboss-as-7.1.0.Final\\standalone\\configuration\\");
		CompteMailFileService service = new CompteMailFileService();
		CompteMail compte = service.getCompteMailFromFileByEmail(email);
		return new CompteMailManager(compte, true);
	}

}
