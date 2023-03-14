/**
 * @author gyclon
 */
package bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

public class DepartementTest {

	@Test
	public void getDepartement() throws ParseException {
		System.out.println("getDepartement");

		String strDate = "20180704";

		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd").withZone(DateTimeZone.forID("Europe/Paris"));

		DateTime date = formatter.parseDateTime(strDate);
		DateTime dateUTC = date.withZone(DateTimeZone.UTC);
		DateTimeFormatter parser = ISODateTimeFormat.dateTime();

		String ddd = parser.print(dateUTC);
		System.out.println(parser.print(dateUTC));

		DateTimeFormatter formatter_ = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.'000Z'");
		formatter_.withZoneUTC();
		DateTime sss = formatter_.withZoneUTC().parseDateTime(ddd);
		System.out.println(sss);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fff = sdf.format(sss.toDate());
		System.out.println(fff);
	}

}
