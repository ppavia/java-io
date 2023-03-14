package fr.cimut.ged.entrant.dao.starwebdao;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.ids.EditicId;
import fr.cimut.ged.entrant.beans.starwebdao.SimpleUpdateResponseDto;
import fr.cimut.ged.entrant.dao.DocumentDao;
import fr.cimut.ged.entrant.dto.DocumentTypeModificationDto;
import fr.cimut.ged.entrant.dto.technical.BulkFkUpdateDto;
import fr.cimut.ged.entrant.dto.technical.DocumentTypeUpdateDto;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;
import fr.cimut.ged.entrant.utils.GedeIdHelper;
import fr.cimut.ged.entrant.utils.GedeUtils;
import fr.cimut.ged.entrant.utils.RestClientUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static fr.cimut.ged.entrant.utils.GedeIdHelper.getEddocIdFromIds;
import static fr.cimut.ged.entrant.utils.GedeIdHelper.getExactEddocIdFromIds;
import static fr.cimut.ged.entrant.utils.GlobalVariable.getStarwebDaoWsUrl;
import static fr.cimut.ged.entrant.utils.RestClientUtils.encodeURIComponent;

/**
 * Endpoint Workaround pour mettre à jour les Fk des documents vers les types
 *
 * TODO: Supprimer lorsque la FK sera renseignée à l'insertion
 */
public class TechnicalRestDao {

	private static final Logger LOGGER = Logger.getLogger(TechnicalRestDao.class);

	private TechnicalRestDao() {
	}

	public static void tryUpdateDocumentTypeFk(String cmroc, String envdir, Document document) {

		Map<String, String> headerParams;
		String url;

		try {
			Long typeId = document.getType().getId();
			DocumentTypeModificationDto typeModificationDto = new DocumentTypeModificationDto();
			typeModificationDto.setTypeId(typeId.toString());

			String env = EnvironnementHelper.determinerEnvironnement(envdir);
			headerParams = GedeUtils.buildHeaderForStarwebDao(cmroc, env);

			String eddocId = getExactEddocIdFromIds(document.getIdstar(), document.getTsstar());
			url = getStarwebDaoWsUrl() + "/document/" +  encodeURIComponent(eddocId)+ "/_type";

			SimpleUpdateResponseDto simpleUpdateResponseDto = RestClientUtils.executePostRequest(url, headerParams, SimpleUpdateResponseDto.class,
					typeModificationDto);
			LOGGER.debug("fk update success: "+simpleUpdateResponseDto.getSuccess());
		} catch (Exception e) {
			LOGGER.warn("Impossible de synchroniser la FK document/type côté Starweb: "+document.getId(), e);
		}
	}

	// Résoudre les FK document->type NULL pour tout l'OC (page par page)
	public static boolean reconciliationDocumentTypeFk(DocumentDao docDao, String cmroc, String envdir, Integer page, Integer size)
			throws Exception {
		List<String> documentIds = fetchUntypedDocumentIds(cmroc, envdir, page, size);
		if (documentIds.isEmpty()) {
			return false;
		}
		LOGGER.info("process "+documentIds.size()+" documents");
		int numSkip = 0;

		BulkFkUpdateDto updatesDto = new BulkFkUpdateDto();
		updatesDto.setUpdates(new ArrayList<DocumentTypeUpdateDto>());

		for (final String starwebId : documentIds) {
			String eddocId = starwebId.trim();
			EditicId editicId;
			try {

				editicId= GedeIdHelper.convertIdStarwebToEditic(eddocId);
			} catch (NumberFormatException nfe) {
				numSkip++;
				continue;
			}


			if (editicId.getIdStar() == 0l && editicId.getTsStar() == 0l) {
				// pas dans l'éditique certain
				numSkip++;
				continue;
			}

			Document document = docDao.getByEddocId(eddocId);
			if (document == null) {
				numSkip++;
				continue;
			}

			Type type = document.getType();
			if (type == null) {
				numSkip++;
				continue;
			}
			long typeId = type.getId();

			DocumentTypeUpdateDto singleUpdateDto = new DocumentTypeUpdateDto();
			singleUpdateDto.setDocumentId(starwebId);
			singleUpdateDto.setTypeId(Long.toString(typeId, 10));

			updatesDto.getUpdates().add(singleUpdateDto);
		}

		LOGGER.info("update "+(size - numSkip)+" documents");
		postBulkFkUpdate(cmroc, envdir, updatesDto);

		return true;
	}

	public static void postBulkFkUpdate(String cmroc, String envdir, BulkFkUpdateDto updatesDto) throws Exception {

		Map<String, String> headerParams;
		String url;

		String env = EnvironnementHelper.determinerEnvironnement(envdir);
		headerParams = GedeUtils.buildHeaderForStarwebDao(cmroc, env);
		url = getStarwebDaoWsUrl() + "/document/_type-bulk";

		RestClientUtils.executePostRequest(url, headerParams, updatesDto);
	}

	public static List<String> fetchUntypedDocumentIds(String cmroc, String envdir, Integer page, Integer size) throws Exception {
		Map<String, String> headerParams;
		String url;

		String env = EnvironnementHelper.determinerEnvironnement(envdir);
		headerParams = GedeUtils.buildHeaderForStarwebDao(cmroc, env);
		url = getStarwebDaoWsUrl() + "/document/_type-missing?page="+page+"&size="+size;

		return RestClientUtils.executeGetListRequest(url, headerParams,
				String.class);
	}
}
