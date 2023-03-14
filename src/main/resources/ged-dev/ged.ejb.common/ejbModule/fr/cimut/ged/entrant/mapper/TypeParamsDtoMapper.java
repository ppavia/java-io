package fr.cimut.ged.entrant.mapper;

import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypeParamsDto;
import fr.cimut.ged.entrant.dto.TypeParamsDto;
import org.mapstruct.Mapper;

/**
 * Réponse du service DAO vers DTO REST pour les paramètres de type document
 */
@Mapper
public interface TypeParamsDtoMapper {

	TypeParamsDto daoTypeParamsToDto(ResponseTypeParamsDto item);
}
