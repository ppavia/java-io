package helper;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.MimeUtility;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fr.cimut.ged.entrant.utils.DateHelper;

public class dateHelperTest {

	private Date before;

	// ENV
	// //-Dfr.cimut.editique.purge.time.interval=3

	@Before
	public void setUp() throws Exception {
		System.out.println("###################################################");
		System.out.println("Start                                             #");
		System.out.println("###################################################");

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
	public void formatDate() {
		try {
			DateTime date = new DateTime();
			SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			assertTrue(format.format(date.toDate()).matches("^\\d{2}/\\d{2}/\\d{4}\\s\\d{2}:\\d{2}$"));
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	//@Test
	public void substringTest() throws ParseException {

		//filename = MimeUtility.decodeText(bodyPart.getFileName());

		//if (filename.indexOf(".") > 0) {
		String contentType = "application/pdf;\n		name=\"=?UTF-8?Q?note_d'honoraires_acquitt=c3=a9e-_ost=c3=a9opathe_Marine.p?=\n	 =?UTF-8?Q?df?=\"";

		Pattern regex = Pattern.compile("name=\"(=\\?[A-Za-z0-8-]+\\?[^\"]+\\?=)\"", Pattern.DOTALL);
		Matcher regexMatcher = regex.matcher(contentType);
		if (regexMatcher.find()) {
			try {
				String output = "";
				for (String iterable_element : regexMatcher.group(1).split("\\n")) {
					output += MimeUtility.decodeWord(iterable_element.trim());
				}
				System.out.println(output);

			} catch (javax.mail.internet.ParseException e) {
				// 
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// 
				e.printStackTrace();
			}
		}

		//System.out.println(contentType);
		//String filename = Normalizer.normalize("utf-8''%6E%6F%74%65%20%64%27%68%6F%6E%6F%72%61%69%72%65%73%20",
		//		Normalizer.Form.NFC);
		//System.out.println("filename : " + filename);
		//}

		//		String str ="3| ";
		//		
		//		if (str.matches("^\\d+\\|[A-Za-z\\s]+$")){
		//			System.out.println("YES");
		//		}else{
		//			System.out.println("NO");
		//		}

		//		String str = "012012012_9923232323";
		//		if (str.contains("_98")){
		//			System.out.println("YES");
		//		}else{
		//			System.out.println("NO");
		//		}

		//SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");
		//System.out.println("9880814027045".matches("^\\d{13}$"));
	}

	//@Test
	public void utcToLocalTest() throws ParseException {
		String format = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
		String formatOut = "yyyy/MM/dd HH:mm";
		String gmtTimestr = "2014-10-19T22:00:00.000Z";
		DateFormat df = new SimpleDateFormat(format);
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date gmtTime = df.parse(gmtTimestr);
		DateFormat dfOut = new SimpleDateFormat(formatOut);
		dfOut.setTimeZone(TimeZone.getTimeZone("America/Buenos_Aires"));
		String localTime = dfOut.format(gmtTime);
		System.out.println(localTime);
		dfOut.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
		localTime = dfOut.format(gmtTime);
		System.out.println(localTime);
	}

	//@Test
	public void getTimeIntoLocalTest() {

		try {
			String date = "2014-10-19T22:00:00.000Z";
			System.out.println(DateHelper.toLocalTime(date));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	//@Test
	@SuppressWarnings("unused")
	public void getTimeJodaTest() {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
		String date = "20141020";
		DateTime dt = formatter.parseDateTime(date);
		DateTimeFormatter fmt = ISODateTimeFormat.dateTimeNoMillis();

		try {
			System.out.println(DateHelper.convert(date));
		} catch (ParseException e) {
			// 
			e.printStackTrace();
			fail();
		}

	}

	//@Test
	public void getTimeCodeTest() {
		try {
			System.out.println(DateHelper.convert("20150217"));
			System.out.println(DateHelper.convert("20141017"));
		} catch (ParseException e) {
			// 
			e.printStackTrace();
			fail();
		}
	}
}
