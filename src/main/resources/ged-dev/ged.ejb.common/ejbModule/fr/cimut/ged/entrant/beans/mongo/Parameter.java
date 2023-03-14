package fr.cimut.ged.entrant.beans.mongo;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class Parameter implements Serializable, GenericMgDbBean {

	private static final long serialVersionUID = 1L;

	/** Clé de l'ID dans le flux JSON */
	private static final String ID_FIELD = "_id";

	/** Format du CMROC */
	private static final Pattern CMROC_PATTERN = Pattern.compile("^\\d{4}$");

	@JsonProperty(ID_FIELD)
	private String id;

	private List<String> list = new ArrayList<String>();

	/** CMROC représenté par cette instance de Parameter */
	@JsonIgnore
	private String cmroc;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> arrayList) {
		this.list = arrayList;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	/**
	 * Classe de sérialisation des {@link Parameter}s en JSON
	 * 
	 * @author pgarel
	 */
	public static class ParameterSerializer extends StdSerializer<Parameter> {

		public ParameterSerializer() {
			super(Parameter.class);
		}

		@Override
		public void serialize(Parameter parameter, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
			jgen.writeStartObject();
			jgen.writeStringField(ID_FIELD, parameter.getId());
			jgen.writeArrayFieldStart(parameter.getCmroc());
			for (String value : parameter.getList()) {
				jgen.writeStringField(parameter.getCmroc(), value);
			}
			jgen.writeEndArray();
			jgen.writeEndObject();
		}

	}

	/**
	 * Classe de désérialisation des JSON représentant des {@link Parameter}s
	 * 
	 * @author pgarel
	 */
	public static class ParameterDeserializer extends StdDeserializer<Parameter> {

		private static final long serialVersionUID = 1L;

		private String cmroc;

		public ParameterDeserializer(String cmroc) {
			super(Parameter.class);
			this.cmroc = cmroc;
		}

		@Override
		public Parameter deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
			if (jp.getCurrentToken() != JsonToken.START_OBJECT) {
				throw new IOException("invalid start marker");
			}

			// Construction du Parameter avec une liste de valeurs initialisée à vide
			Parameter parameter = new Parameter();
			List<String> values = new ArrayList<String>();
			parameter.setList(values);

			// Parcours l'ensemble des champs du JSON
			while (jp.nextToken() != JsonToken.END_OBJECT) {
				String fieldname = jp.getCurrentName();
				jp.nextToken();

				// Champ ID
				if (ID_FIELD.equals(fieldname)) {
					parameter.setId(jp.getText());
				}

				// Champ CMROC
				else if (CMROC_PATTERN.matcher(fieldname).matches()) {
					if (fieldname.equals(cmroc)) {
						// Si le CMROC est celui recherché, on récupère les valeurs
						parameter.setCmroc(fieldname);
						if (jp.getCurrentToken() != JsonToken.START_ARRAY) {
							throw new IOException("invalid start marker");
						}
						// Ajout des valeurs au Paramètre pour le CMROC
						while (jp.nextToken() != JsonToken.END_ARRAY) {
							values.add(jp.getText());
						}
					} else {
						// Sinon, on boucle jusqu'au CMROC suivant
						while (jp.nextToken() != JsonToken.END_ARRAY) {
						}
					}
				}
			}
			jp.close();

			return parameter;
		}

	}

}
