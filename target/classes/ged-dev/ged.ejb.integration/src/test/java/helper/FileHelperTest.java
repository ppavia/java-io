package helper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutXmlException;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.FileHelper;

public class FileHelperTest {
	private Date before;

	private static final String home = "C:\\gedEntrante\\";

	/** Nom de l'environnement utilisé pour les tests */
	private String envirTeste = "INTV3";

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

	@Test
	public void replaceExtension() {
		String originalExtension = "machin.pdf";
		assertTrue("machin".equals(originalExtension.substring(0, originalExtension.lastIndexOf("."))));
		assertTrue(".pdf".equals(originalExtension.substring(originalExtension.lastIndexOf("."))));
	}

	@Test
	public void getTypeMime() {
		try {
			assertTrue("application/msword".equals(FileHelper.getTypeMime("toto.dOc")));
			assertTrue("application/vnd.ms-excel".equals(FileHelper.getTypeMime("toto.xlS")));
			assertTrue("application/pdf".equals(FileHelper.getTypeMime("toto.pDf")));
			assertTrue("image/jpeg".equals(FileHelper.getTypeMime("toto.JPEG")));
			assertTrue("image/png".equals(FileHelper.getTypeMime("toto.png")));
			assertTrue("application/vnd.ms-outlook".equals(FileHelper.getTypeMime("toto.msg")));
		} catch (CimutFileException e) {
			// 
			e.printStackTrace();
		}
	}

	//@Test
	public void encodeUTF8() {
		String path = "C:\\gedEntrante\\integration\\in\\new\\9916_20141021111946_00008.xml";

		try {
			Document docu = DocumentHelper.toDocument(new File(path), "9916_20141021111945_00000.pdf", envirTeste, null, null, null);
			System.out.println(docu.getJson().getData());

		} catch (CimutXmlException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (CimutDocumentException e) {
			// 
			e.printStackTrace();
		} catch (IOException e) {
			// 
			e.printStackTrace();
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
		}
	}

	//@Test
	public void loadXmlFile() {
		String path = home + "integration/errors/";
		File dir = new File(path);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});

		for (int i = 0; i < files.length; i++) {
			Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
			try {
				System.out.println(files[i]);
				map = FileHelper.loadXmlFile(files[i]);
			} catch (CimutXmlException e) {
				System.out.println(e.getMessage());
				//e.printStackTrace();
				assertTrue(false);
			}

			System.out.println(map.toString());
		}

		assertTrue(true);

	}

	//@Test
	public void logFileTest() {

		String file = "C:/tmp/test.log";
		try {
			FileHelper.logFile("ceci est un simple Test ", file);
		} catch (CimutFileException e) {
			// 
			e.printStackTrace();
		}
		assertTrue(new File(file).exists());
	}

	//@Test
	public void listXmlFile() {

		Map<String, List<File>> map = null;
		try {
			map = FileHelper.listXmlFile(envirTeste);
		} catch (CimutFileException e) {
			e.printStackTrace();
			fail();
		} catch (CimutConfException e) {
			// 
			e.printStackTrace();
			fail();
		}

		for (String key : map.keySet()) {

			System.out.println(map.get(key).get(0).getAbsoluteFile() + " " + map.get(key).get(1).getAbsoluteFile());

		}

	}
}
