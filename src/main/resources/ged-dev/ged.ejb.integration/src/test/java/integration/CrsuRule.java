/**
 * @author gyclon
 */
package integration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;

public class CrsuRule {

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

	//@Test
	public void generateSql() {
		BufferedReader br = null;
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader("C:\\tmp\\org_enti_ppo.txt"));
			while ((sCurrentLine = br.readLine()) != null) {
				List<String> list = Arrays.asList(sCurrentLine.split("\\s+"));
				String string = "insert into HASUDE_RULES (P_HASUDE_ID_S, HASUDE_NAME, HASUDE_PRIORITE, HASUDE_ACTIF, HASUDE_HAORGA_ID, HASUDE_HAENTI_ID, HASUDE_UTIMAJ, HASUDE_DT_MAJ, HASUDE_AFFECTATION, HASUDE_CANAL) values (S_HASUDE.nextval,'DEFAUT EXTRANET', 100, 1, "
						+ list.get(2) + " , " + list.get(3)
						+ ", 'cimut', sysdate,'{\"TYPDMA_ID\":{\"id\":\"EXT\",\"text\":\"RC / Extranet / Administratif\"}}','[{\"id\":\"EXT\",\"text\":\"Extranet\"},{\"id\":\"EXA\",\"text\":\"extranet automatique\"}]');";
				System.out.println(string);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
