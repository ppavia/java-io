package fr.cimut.ged.entrant.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Classe utils permettant d'effectuer les appel REST (GET, POST, DELETE, ...) vers un serveur cible et de traiter les
 * réponses
 * 
 * @author tpiche
 *
 */
public class RestClientUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(RestClientUtils.class);
	private static final String MSG_ERROR_MAPPING_CONSULTATION = "Errreur dans le mapping de l'objet réponse à la requête de consultation";

	private static ObjectMapper objectMapper = new ObjectMapper().configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).setSerializationInclusion(Include.NON_NULL)
			.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

	public static <T> T executeGetRequest(String target, Map<String, String> headerParam, Class<T> resultClass) throws IOException {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(target);
		getRequest.addHeader("accept", "application/json");
		addHeader(getRequest, headerParam);
		HttpResponse response = httpClient.execute(getRequest);
		T result = null;
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		try {
			result = objectMapper.readValue(response.getEntity().getContent(), resultClass);
		} catch (JsonMappingException e) {
			// Retour vide ou incohérent, on ne fait rien
			// on logge a minima
			LOGGER.info(MSG_ERROR_MAPPING_CONSULTATION, e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return result;
	}

	public static <T> List<T> executeGetListRequest(String target, Map<String, String> headerParam, Class<T> resultClass) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(target);
		getRequest.addHeader("accept", "application/json");
		addHeader(getRequest, headerParam);
		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		List<T> result = null;
		JavaType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, resultClass);
		try {
			result = objectMapper.readValue(response.getEntity().getContent(), listType);
		} catch (JsonMappingException e) {
			// Retour vide ou incohérent, on ne fait rien
			// on logge a minima
			LOGGER.info(MSG_ERROR_MAPPING_CONSULTATION, e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return result;
	}

	public static <T> int executePostRequest(String target, Map<String, String> headerParam, T entityToSend) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(target);
		StringEntity entity = new StringEntity(objectMapper.writeValueAsString(entityToSend), "UTF-8");
		postRequest.addHeader("accept", "application/json");
		addHeader(postRequest, headerParam);
		entity.setContentType("application/json");
		postRequest.setEntity(entity);
		HttpResponse response = httpClient.execute(postRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		httpClient.getConnectionManager().shutdown();

		return 200;
	}

	public static <T, R> R executePostRequest(String target, Map<String, String> headerParam, Class<R> resultClass, T entityToSend) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(target);
		StringEntity entity = new StringEntity(objectMapper.writeValueAsString(entityToSend), "UTF-8");
		postRequest.addHeader("accept", "application/json");
		addHeader(postRequest, headerParam);
		entity.setContentType("application/json");
		postRequest.setEntity(entity);
		HttpResponse response = httpClient.execute(postRequest);

		R result = null;
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		try {
			result = objectMapper.readValue(response.getEntity().getContent(), resultClass);
		} catch (JsonMappingException e) {
			// Retour vide ou incohérent, on ne fait rien
			// on logge a minima
			LOGGER.info(MSG_ERROR_MAPPING_CONSULTATION, e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return result;
	}

	public static int executeDeleteRequest(String target, Map<String, String> headerParam) throws IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpDelete deleteRequest = new HttpDelete(target);
		deleteRequest.addHeader("accept", "application/json");
		addHeader(deleteRequest, headerParam);
		HttpResponse response = httpClient.execute(deleteRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new IOException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
		}
		httpClient.getConnectionManager().shutdown();

		return 200;
	}

	private static void addHeader(HttpRequestBase request, Map<String, String> headerParam) {
		if (null != request && headerParam != null) {
			for (Entry<String, String> entry : headerParam.entrySet()) {
				request.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	private static final String HEX = "0123456789ABCDEF";
	public static String encodeURIComponent(String str) throws UnsupportedEncodingException {
		if (str == null) return null;

		byte[] bytes = str.getBytes("UTF-8");
		StringBuilder builder = new StringBuilder(bytes.length);

		for (byte c : bytes) {
			if (c >= 'a' ? c <= 'z' || c == '~' :
					c >= 'A' ? c <= 'Z' || c == '_' :
							c >= '0' ? c <= '9' :  c == '-' || c == '.')
				builder.append((char)c);
			else
				builder.append('%')
						.append(HEX.charAt(c >> 4 & 0xf))
						.append(HEX.charAt(c & 0xf));
		}

		return builder.toString();
	}

}
