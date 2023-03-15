package fr.cimut.ged.entrant.dao.starwebdao;

import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypesDto;
import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypeDto;
import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypeParamsDto;
import fr.cimut.ged.entrant.dao.AssiaClaims;
import fr.cimut.ged.entrant.dao.TypeReadQueries;
import fr.cimut.ged.entrant.dto.TypeFilters;
import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.dto.TypeParamsDto;
import fr.cimut.ged.entrant.dto.TypeParamsModificationDto;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.exceptions.RuntimeGedeException;
import fr.cimut.ged.entrant.mapper.TypeDtoMapper;
import fr.cimut.ged.entrant.mapper.TypeParamsDtoMapper;
import fr.cimut.ged.entrant.utils.GedeUtils;
import fr.cimut.ged.entrant.utils.RestClientUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.mapstruct.factory.Mappers;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.io.IOException;
import java.util.*;

import static fr.cimut.ged.entrant.utils.GlobalVariable.getStarwebDaoWsUrl;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Client REST pour l'API /type de StarwebDAO
 */
@Stateless(mappedName = "TypeRestDao")
@LocalBean
public class TypeRestDao implements TypeReadQueries {

	public static final String ERROR_MSG_PREPARE_DAO = "Error lors de la préparation de l'appel à StarwebDAO";
	public static final String ERROR_MSG_CALL_DAO = "Erreur lors de l'appel à StarwebDAO";
	public static final String URI_ENCODED_PIPE = "%7C"; // sinon Tomcat n'aime pas recevoir directement un |

	@Override
	public List<TypeDto> list(AssiaClaims claims, Long idCategorie) {
		if (null == idCategorie) {
			return emptyList();
		}

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		addOptionalStringParameter(parameters, "category", idCategorie.toString());

		return fetchTypes(claims, parameters);
	}

	@Override
	public List<TypeDto> listAll(AssiaClaims claims) {
		return fetchTypes(claims, new ArrayList<BasicNameValuePair>());
	}

	@Override
	public List<TypeDto> listTypeByCriteria(AssiaClaims claims, Long idCategorie, String codeType) {
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

		if (null != idCategorie) {
			addOptionalStringParameter(parameters, "category", idCategorie.toString());
		}

		if (null != codeType) {
			addOptionalStringParameter(parameters, "exactCodeTypes", withoutUnsafeChars(codeType));
		}

		return fetchTypes(claims, parameters);
	}

	@Override
	public List<TypeDto> listTypeByCodeList(AssiaClaims claims, Set<String> typeDocuments) {
		if (null == typeDocuments || typeDocuments.isEmpty()) {
			return emptyList();
		}

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		addOptionalStringParameter(parameters, "exactCodeTypes", StringUtils.join(URI_ENCODED_PIPE, withoutUnsafeChars(typeDocuments)));

		return fetchTypes(claims, parameters);
	}

	@Override
	public String getLibelleByCode(AssiaClaims claims, String typeDocument) {
		if (isBlank(typeDocument)) {
			return "";
		}
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();
		addOptionalStringParameter(parameters, "exactCodeTypes", withoutUnsafeChars(typeDocument));

		List<TypeDto> result = fetchTypes(claims, parameters);
		return result.isEmpty() ? "" : result.get(0).getLibelle();
	}

	@Override
	public Long countTypeWithCode(AssiaClaims claims, String codeType) {
		throw new UnsupportedOperationException("Non implémenté ici");
	}

	@Override
	public List<TypeDto> listAllByPageAndSize(AssiaClaims claims, Integer pageNumber, Integer pageSize, String code, String libelle) {

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

		if (pageNumber != null && pageSize != null) {
			addOptionalStringParameter(parameters, "page", Integer.toString(pageNumber, 10));
			addOptionalStringParameter(parameters, "size", Integer.toString(pageSize, 10));
		}

		if (!isBlank(code)) {
			addOptionalStringParameter(parameters, "codeType", code);
		}

		if (!isBlank(libelle)) {
			addOptionalStringParameter(parameters, "libelleType", libelle);
		}

		return fetchTypes(claims, parameters);
	}

	@Override
	public List<TypeDto> search(AssiaClaims claims, TypeFilters filters) {
		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

		addOptionalBooleanParameter(parameters, "isIDOC", filters.getIdoc());
		addOptionalBooleanParameter(parameters, "isVisibleExtranet", filters.getVisibleExtranet());
		addOptionalStringParameter(parameters, "codeType", filters.getCodeType());
		addOptionalStringParameter(parameters, "libelleType", filters.getLibelleType());
		addOptionalStringParameter(parameters, "category", filters.getCategory());

		return fetchTypes(claims, parameters);
	}

	@Override
	public TypeDto get(AssiaClaims claims, Long id) {
		return getTypeById(claims, id);
	}

	@Override
	public TypeDto get(AssiaClaims claims, String id) {
		return getTypeById(claims, Long.parseLong(id,10));
	}

	private List<TypeDto> fetchTypes(AssiaClaims claims, List<? extends NameValuePair> queryParams) {

		Map<String, String> headerParams;
		String url;

		try {
			headerParams = GedeUtils.buildHeaderForStarwebDao(claims.getCmroc(), claims.getEnv());
			url = getStarwebDaoWsUrl() + "/types";

			if (!queryParams.isEmpty()) {
				url += '?' + URLEncodedUtils.format(queryParams, "utf-8");
			}
		} catch (CimutConfException e) {
			throw new RuntimeGedeException(ERROR_MSG_PREPARE_DAO, e);
		}

		try {
			ResponseTypesDto responseDto = RestClientUtils.executeGetRequest(url, headerParams,
					ResponseTypesDto.class);

			TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);
			return mapper.daoTypeConfigsToTypesDto(responseDto.getTypes());
		} catch (IOException e) {
			throw new RuntimeGedeException(ERROR_MSG_CALL_DAO, e);
		}
	}

	private TypeDto getTypeById(AssiaClaims claims, Long typeId) {

		Map<String, String> headerParams;
		String url;

		try {
			headerParams = GedeUtils.buildHeaderForStarwebDao(claims.getCmroc(), claims.getEnv());
			url = getStarwebDaoWsUrl() + "/types/" + typeId;

		} catch (CimutConfException e) {
			throw new RuntimeGedeException(ERROR_MSG_PREPARE_DAO, e);
		}

		try {
			ResponseTypeDto responseDto = RestClientUtils.executeGetRequest(url, headerParams,
					ResponseTypeDto.class);

			TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);
			return mapper.daoTypeConfigToTypeDto(responseDto);
		} catch (IOException e) {
			throw new RuntimeGedeException(ERROR_MSG_CALL_DAO, e);
		}
	}

	public TypeParamsDto getOrganismTypeParametersByTypeId(AssiaClaims claims, Long typeId)
			throws GedeException {

		Map<String, String> headerParams;
		String url;

		try {
			headerParams = GedeUtils.buildHeaderForStarwebDao(claims.getCmroc(), claims.getEnv());
			url = getStarwebDaoWsUrl() + "/types/" + typeId + "/parameters";

		} catch (CimutConfException e) {
			throw new RuntimeGedeException(ERROR_MSG_PREPARE_DAO, e);
		}

		try {
			ResponseTypeParamsDto responseTypeParamsDto = RestClientUtils.executeGetRequest(url, headerParams,
					ResponseTypeParamsDto.class);

			return toTypeParamsDto(responseTypeParamsDto);
		} catch (Exception e) {
			throw new GedeCommonException(ERROR_MSG_CALL_DAO, e);
		}
	}

	public TypeParamsDto saveOrganismTypeParameters(AssiaClaims claims, String user, Long typeId, TypeParamsModificationDto typeParamsModificationDto)
			throws GedeException {

		Map<String, String> headerParams;
		String url;

		try {
			headerParams = GedeUtils.buildHeaderForStarwebDao(claims.getCmroc(), claims.getEnv(), user);
			url = getStarwebDaoWsUrl() + "/types/" + typeId + "/parameters";

		} catch (CimutConfException e) {
			throw new RuntimeGedeException(ERROR_MSG_PREPARE_DAO, e);
		}

		try {
			ResponseTypeParamsDto responseTypeParamsDto = RestClientUtils.executePostRequest(url, headerParams,
					ResponseTypeParamsDto.class, typeParamsModificationDto);

			return toTypeParamsDto(responseTypeParamsDto);
		} catch (Exception e) {
			throw new GedeCommonException(ERROR_MSG_CALL_DAO, e);
		}
	}

	private Set<String> withoutUnsafeChars(Set<String> values) {
		Set<String> result = new HashSet<String>();
		for (String value: values) {
			if (isBlank(value)) {
				continue;
			}
			String cleanedValue = withoutUnsafeChars(value);
			result.add(cleanedValue);
		}
		return result;
	}

	/**
	 * Supprimer les charactères utilisés pour l'encodage d'une liste et le like SQL
	 * @param value
	 * @return
	 */
	private String withoutUnsafeChars(String value) {
		String cleanedValue = StringUtils.remove(value, '|');
		cleanedValue = StringUtils.remove(cleanedValue, '%');
		return cleanedValue;
	}

	private TypeParamsDto toTypeParamsDto(ResponseTypeParamsDto responseDto) {
		TypeParamsDtoMapper mapper = Mappers.getMapper(TypeParamsDtoMapper.class);

		return mapper.daoTypeParamsToDto(responseDto);
	}

	private void addOptionalBooleanParameter(List<BasicNameValuePair> parameters, String name, Boolean value) {
		if (null == value) {
			return;
		}
		parameters.add(new BasicNameValuePair(name, Boolean.toString(value)));
	}

	private void addOptionalStringParameter(List<BasicNameValuePair> parameters, String name, String value) {
		if (null == value) {
			return;
		}
		parameters.add(new BasicNameValuePair(name, value));
	}

}
