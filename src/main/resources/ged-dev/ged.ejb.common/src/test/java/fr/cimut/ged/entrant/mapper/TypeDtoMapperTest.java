package fr.cimut.ged.entrant.mapper;

import fr.cimut.ged.entrant.beans.db.Categorie;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.db.TypeEntrantSortant;
import fr.cimut.ged.entrant.beans.starwebdao.ResponseCategoryDto;
import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypeDto;
import fr.cimut.ged.entrant.beans.starwebdao.ResponseTypeParamsDto;
import fr.cimut.ged.entrant.dto.CategoryDto;
import fr.cimut.ged.entrant.dto.TypeDto;
import org.junit.Test;
import org.mapstruct.factory.Mappers;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class TypeDtoMapperTest {

	@Test
	public void it_can_map_type_dao_response_to_type_dto() throws ParseException {
		//GIVEN
		IsoDateMapper isoDateMapper = new IsoDateMapper();

		ResponseTypeDto item = new ResponseTypeDto();
		item.setIdType(1556l);
		item.setDateMaj("2020-02-12T13:55:50+0000");
		item.setUserMaj("Eric");
		item.setCode("NOTICE");
		item.setDureeArchivage(10);
		item.setDureePurge(12);
		item.setLibelle("notice");
		item.setTypeEntrantSortant("E");

		ResponseCategoryDto category = new ResponseCategoryDto();
		category.setId(1234L);
		category.setDateMaj("2022-04-29T14:56:57+0000");
		category.setUserMaj("jean");
		category.setCode("TOUS");
		category.setLibelle("Tous");
		item.setCategory(category);

		ResponseTypeParamsDto params = new ResponseTypeParamsDto();
		params.setDateMaj("2020-04-29T14:56:57+0000");
		params.setUserMaj("Pierric");
		params.setDureeVisibleExtranet(16);
		params.setIntegrableIdoc(true);
		params.setNotifExtranet(true);
		params.setVisibleExtranet(true);
		item.setParams(params);

		TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);

		//WHEN
		TypeDto typeDto = mapper.daoTypeConfigToTypeDto(item);

		//THEN
		assertEquals(typeDto.getId(), item.getIdType());
		assertEquals(typeDto.getDureeArchivage(), item.getDureeArchivage());
		assertEquals(typeDto.getDureePurge().intValue(),  item.getDureePurge());
		assertEquals(typeDto.getDateMaj(), isoDateMapper.asDate(item.getDateMaj()));
		assertEquals(typeDto.getUserMaj(), item.getUserMaj());

		assertEquals(typeDto.getDureeVisibleExtranet(), params.getDureeVisibleExtranet());
		assertEquals(typeDto.isIntegrableIdoc(), params.getIntegrableIdoc().booleanValue());
		assertEquals(typeDto.isNotifExtranet(), params.getNotifExtranet().booleanValue());
		assertEquals(typeDto.isVisibleExtranet(), params.getVisibleExtranet().booleanValue());
		assertEquals(typeDto.getParamsDateMaj(), isoDateMapper.asDate(params.getDateMaj()));
		assertEquals(typeDto.getParamsUserMaj(), params.getUserMaj());

		assertEquals(typeDto.getCategorie().getId(), item.getCategory().getId());
		assertEquals(typeDto.getCategorie().getDateMaj(), isoDateMapper.asDate(item.getCategory().getDateMaj()));
		assertEquals(typeDto.getCategorie().getUserMaj(), item.getCategory().getUserMaj());
		assertEquals(typeDto.getCategorie().getCode(), item.getCategory().getCode());
		assertEquals(typeDto.getCategorie().getLibelle(), item.getCategory().getLibelle());
	}

	@Test
	public void it_can_map_type_to_type_dto()  {
		//GIVEN
		Type item = new Type();
		item.setId(12456l);
		item.setDateMaj(new Date());
		item.setUserMaj("Eric");
		item.setCode("NOTICE");
		item.setDureeArchivage(10);
		item.setDureePurge(12);
		item.setLibelle("notice");
		item.setTypeEntrantSortant(TypeEntrantSortant.SORTANT);

		Categorie category = new Categorie();
		category.setId(1234L);
		category.setDateMaj(new Date());
		category.setUserMaj("jean");
		category.setCode("TOUS");
		category.setLibelle("Tous");
		item.setCategorie(category);

		item.setDureeVisibleExtranet(16);
		item.setIntegrableIdoc(true);
		item.setNotifExtranet(true);
		item.setVisibleExtranet(true);
		TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);

		//WHEN
		TypeDto typeDto = mapper.typeToTypeDto(item);

		//THEN
		assertEquals(typeDto.getId(), item.getId());
		assertEquals(typeDto.getDureeArchivage(), item.getDureeArchivage());
		assertEquals(typeDto.getDureePurge(),  item.getDureePurge());
		assertEquals(typeDto.getDateMaj(), item.getDateMaj());
		assertEquals(typeDto.getUserMaj(), item.getUserMaj());

		assertEquals(typeDto.getDureeVisibleExtranet(), item.getDureeVisibleExtranet());
		assertEquals(typeDto.isIntegrableIdoc(), item.isIntegrableIdoc());
		assertEquals(typeDto.isNotifExtranet(), item.isNotifExtranet());
		assertEquals(typeDto.isVisibleExtranet(), item.isVisibleExtranet());

		assertEquals(typeDto.getCategorie().getId().longValue(), item.getCategorie().getId());
		assertEquals(typeDto.getCategorie().getDateMaj(), item.getCategorie().getDateMaj());
		assertEquals(typeDto.getCategorie().getUserMaj(), item.getCategorie().getUserMaj());
		assertEquals(typeDto.getCategorie().getCode(), item.getCategorie().getCode());
		assertEquals(typeDto.getCategorie().getLibelle(), item.getCategorie().getLibelle());

		assertNull(typeDto.getParamsUserMaj());
		assertNull(typeDto.getParamsDateMaj());
	}

	@Test
	public void it_can_map_type_dto_to_type()  {
		//GIVEN
		TypeDto item = new TypeDto();
		item.setId(12456l);
		item.setDateMaj(new Date());
		item.setUserMaj("Eric");
		item.setCode("NOTICE");
		item.setDureeArchivage(10);
		item.setDureePurge(12);
		item.setLibelle("notice");

		CategoryDto category = new CategoryDto();
		category.setId(1234L);
		category.setDateMaj(new Date());
		category.setUserMaj("jean");
		category.setCode("TOUS");
		category.setLibelle("Tous");
		item.setCategorie(category);

		item.setDureeVisibleExtranet(16);
		item.setIntegrableIdoc(true);
		item.setNotifExtranet(true);
		item.setVisibleExtranet(true);

		TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);

		//WHEN
		Type type = mapper.typeDtoToType(item);

		// THEN
		assertEquals(type.getId(), item.getId());
		assertEquals(type.getDureeArchivage(), item.getDureeArchivage());
		assertEquals(type.getDureePurge(),  item.getDureePurge());
		assertEquals(type.getDateMaj(), item.getDateMaj());
		assertEquals(type.getUserMaj(), item.getUserMaj());

		assertEquals(type.getDureeVisibleExtranet(), item.getDureeVisibleExtranet());
		assertEquals(type.isIntegrableIdoc(), item.isIntegrableIdoc());
		assertEquals(type.isNotifExtranet(), item.isNotifExtranet());
		assertEquals(type.isVisibleExtranet(), item.isVisibleExtranet());

		assertEquals(type.getCategorie().getId(), item.getCategorie().getId().longValue());
		assertEquals(type.getCategorie().getDateMaj(), item.getCategorie().getDateMaj());
		assertEquals(type.getCategorie().getUserMaj(), item.getCategorie().getUserMaj());
		assertEquals(type.getCategorie().getCode(), item.getCategorie().getCode());
		assertEquals(type.getCategorie().getLibelle(), item.getCategorie().getLibelle());
	}
}