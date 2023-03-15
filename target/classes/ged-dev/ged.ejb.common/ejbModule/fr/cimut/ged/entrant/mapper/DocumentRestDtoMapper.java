package fr.cimut.ged.entrant.mapper;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Json;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentInfoComp;
import fr.cimut.ged.entrant.dto.DocumentInfoCompDto;
import fr.cimut.ged.entrant.dto.DocumentRestDto;
import fr.cimut.ged.entrant.dto.JsonDto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface DocumentRestDtoMapper {


	DocumentRestDto documentToDto (Document document);

    @Mappings({
    	@Mapping(target = "dtcreate", dateFormat = "yyyyMMddHHmmss"),
    	@Mapping(target = "dtconsdc", dateFormat = "yyyyMMdd"),
    	@Mapping(target = "dtarchdc", dateFormat = "yyyyMMdd"),
    	@Mapping(target = "dtDebutValidite", dateFormat = "yyyyMMdd"),
        @Mapping(target = "dtFinValidite", dateFormat = "yyyyMMdd"),
        @Mapping(target = "docMongo", ignore = true),
        @Mapping(target = "type", ignore = true)
    })
    Document dtoToDocument (DocumentRestDto documentRestDto);

    DocumentInfoCompDto documentInfoToDto (DocumentInfoComp documentInfoComp);

    Json dtoToJson (JsonDto jsonDto);
    
    JsonDto jsonToDto (Json json);

    DocumentInfoComp dtoToDocumentInfo (DocumentInfoCompDto documentInfoCompDto);

    List<DocumentRestDto> listOfDocumentsToDto(List<Document> documents);

    List<Document> listOfDtosToDocuments(List<DocumentRestDto> documents);
}
