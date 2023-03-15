package fr.cimut.ged.entrant.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.joda.time.DateTime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class BsonDateSerializer extends JsonSerializer<DateTime> {

	private void serializeContents(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
		jgen.writeFieldName("$date");
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'000Z'");
		// On se centre sur l'UTC pour éviter les problèmes de timezone avec Mongo
		formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String formattedDate = formatter.format(value.toDate());
		jgen.writeString(formattedDate);
	}

	@Override
	public void serialize(DateTime value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		jgen.writeStartObject();
		serializeContents(value, jgen, provider);
		jgen.writeEndObject();
	}
}
