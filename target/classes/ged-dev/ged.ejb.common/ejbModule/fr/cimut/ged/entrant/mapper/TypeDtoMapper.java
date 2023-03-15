package fr.cimut.ged.entrant.mapper;

import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypeDto;
import fr.cimut.ged.entrant.dto.TypeDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(uses=IsoDateMapper.class)
public interface TypeDtoMapper {

	@Mappings({
			/* CATEGORY */
			@Mapping(target = "categorie.id", source = "category.id"),
			@Mapping(target = "categorie.code", source =  "category.code"),
			@Mapping(target = "categorie.libelle", source = "category.libelle"),
			@Mapping(target = "categorie.userMaj", source = "category.userMaj"),
			@Mapping(target = "categorie.dateMaj", source = "category.dateMaj"),

			/* TYPE */
			@Mapping(target = "id", source = "idType"),

			/* PARAMS */
			@Mapping(target = "visibleExtranet", source = "params.visibleExtranet"),
			@Mapping(target = "dureeVisibleExtranet", source = "params.dureeVisibleExtranet"),
			@Mapping(target = "userMaj", source = "userMaj"),
			@Mapping(target = "dateMaj", source = "dateMaj"),
			@Mapping(target = "paramsUserMaj", source = "params.userMaj"),
			@Mapping(target = "paramsDateMaj", source = "params.dateMaj"),
			@Mapping(target = "notifExtranet", source = "params.notifExtranet"),
			@Mapping(target = "integrableIdoc", source = "params.integrableIdoc")
	})
	TypeDto daoTypeConfigToTypeDto(ResponseTypeDto item);

	List<TypeDto> daoTypeConfigsToTypesDto(List<ResponseTypeDto> items);


	TypeDto typeToTypeDto(Type item);

	List<TypeDto> typesToTypesDto(List<Type> items);

	/**
	 * Convertir le type dans l'autre sens pour une raison de compatibilité avec le vieux code
	 *
	 * Note: Attention un object crée de cette façon ne fait pas partie de la session hibernate
	 *
	 * @param item
	 * @deprecated
	 * @return
	 */
	Type typeDtoToType(TypeDto item);
}
