package fr.cimut.ged.entrant.dao;

import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.dto.TypeFilters;

import java.util.List;
import java.util.Set;

/**
 * Accès en lecture au référentiel de types
 */
public interface TypeReadQueries {

	List<TypeDto> listTypeByCriteria(AssiaClaims claims, Long idCategorie, String codeType);

	List<TypeDto> listTypeByCodeList(AssiaClaims claims, Set<String> typeDocuments);

	String getLibelleByCode(AssiaClaims claims, String typeDocument);

	Long countTypeWithCode(AssiaClaims claims, String codeType);

	List<TypeDto> listAllByPageAndSize(AssiaClaims claims, Integer page, Integer pageSize, String code, String libelle);

	List<TypeDto> list(AssiaClaims claims, Long idCategorie);

	List<TypeDto> listAll(AssiaClaims claims);

	List<TypeDto> search(AssiaClaims claims, TypeFilters filters);

	TypeDto get(AssiaClaims claims, Long id);

	TypeDto get(AssiaClaims claims, String id);
}
