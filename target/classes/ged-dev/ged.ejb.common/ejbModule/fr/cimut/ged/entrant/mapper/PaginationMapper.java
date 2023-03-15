package fr.cimut.ged.entrant.mapper;

import fr.cimut.ged.entrant.dto.Page;
import fr.cimut.ged.entrant.dto.PaginationDto;
import org.mapstruct.Mapper;

@Mapper
public interface PaginationMapper {
    PaginationDto pageToDto (Page page);

    Page dtoToPage (PaginationDto paginationDto);
}
