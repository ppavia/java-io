package document;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import org.junit.After;
import org.junit.Before;

public class DocumentHelperTest {
	private Date before;

	private static final String home = "C:\\gedEntrante\\";

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
	public void listXmlFile() {
		System.out.println("listXmlFile");
		String path = home + "transcodage\\out";
		File dir = new File(path);
		File[] files = dir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".xml");
			}
		});
		assertTrue(files.length > 0);
	}

}
