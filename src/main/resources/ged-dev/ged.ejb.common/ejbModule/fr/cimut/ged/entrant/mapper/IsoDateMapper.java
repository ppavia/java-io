package fr.cimut.ged.entrant.mapper;

import org.apache.commons.lang3.time.DateUtils;

import javax.xml.bind.DatatypeConverter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 * ISO8601 Date Mapper
 */
public class IsoDateMapper {

	public String asString(Date date) {
		if(date == null) {
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		return  DatatypeConverter.printDate(calendar);
	}
	public Date asDate(String iso8601string) throws ParseException {
		if(iso8601string == null) {
			return null;
		}

		return DateUtils.parseDate(iso8601string, new String[] {"yyyy-MM-dd'T'HH:mm:ssZ"});
	}
}
