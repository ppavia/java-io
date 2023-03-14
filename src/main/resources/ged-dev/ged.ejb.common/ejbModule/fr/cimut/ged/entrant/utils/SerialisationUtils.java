package fr.cimut.ged.entrant.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

/**
 * surcouche de serialisation tarificateur, afin de facilement switcher d'un implémentation à une autre
 * 
 * @author jlebourgocq
 *
 */
public class SerialisationUtils {

	private static final ObjectMapper mapper = new ObjectMapper();
	static {
		// accept des date sans timestamp
		//		mapper.registerModule(new JavaTimeModule());
		// serialisation des dates sans timestamp
		//		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		// pas de serialisation des null
		mapper.setSerializationInclusion(Include.NON_NULL);
		// pas d'erreur si des propriétés sont en trop dans le flux json
		// évite de faire JsonIgnoreUnknown sur toutes les classes
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		//		mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
	}

	private SerialisationUtils() {

	}

	public static <T> T readValue(String json, Class<T> valueType) throws IOException, JsonParseException, JsonMappingException {
		return mapper.readValue(json, valueType);
	}

	public static <T> T readValue(String json, TypeReference<T> typeReference) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, typeReference);
	}

	public static String writeValueAsString(Object value) throws JsonProcessingException {
		return mapper.writeValueAsString(value);
	}

	/**
	 * @deprecated Eviter la récuperation de l'instance mapper pour permettre de débrancher vers une autre
	 *             implémentation json facilement
	 */
	@Deprecated
	public static ObjectMapper getMapperInstance() {
		return mapper;
	}

	public static Map<String, String> readValueToMapStringString(String jsonParam) throws JsonParseException, JsonMappingException, IOException {
		TypeReference<Map<String, String>> typeRef = new TypeReference<Map<String, String>>() {
		};
		return mapper.readValue(jsonParam, typeRef);
	}

	public static Map<String, Object> readValueToMap(String jsonParam) throws JsonParseException, JsonMappingException, IOException {
		TypeReference<Map<String, Object>> typeRef = new TypeReference<Map<String, Object>>() {
		};
		return mapper.readValue(jsonParam, typeRef);
	}

	public static TypeFactory getTypeFactory() {
		return mapper.getTypeFactory();
	}

}
