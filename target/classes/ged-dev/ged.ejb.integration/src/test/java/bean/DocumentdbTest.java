package bean;

import org.junit.Test;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.utils.DocumentHelper;

public class DocumentdbTest {

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

	@Test
	public void add() throws CimutConfException {
		Document document = new Document();
		DocumentHelper.setDefaultValue(document, envirTeste);

	}
}
