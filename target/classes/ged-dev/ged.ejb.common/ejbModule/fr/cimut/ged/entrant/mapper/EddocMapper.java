package fr.cimut.ged.entrant.mapper;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentInfoComp;
import fr.cimut.ged.entrant.dto.DocumentInfoCompDto;
import fr.cimut.ged.entrant.dto.EddocDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper
public interface EddocMapper {


    EddocDto documentToDto (Document document);

    @Mappings({
            @Mapping(target = "docMongo", ignore = true),
            @Mapping(target = "json", ignore = true)
    })
    Document dtoToDocument (EddocDto eddocDto);

    DocumentInfoCompDto documentInfoToDto (DocumentInfoComp documentInfoComp);

    DocumentInfoComp dtoToDocumentInfo (DocumentInfoCompDto documentInfoCompDto);

    List<EddocDto> listOfDocumentsToDto(List<Document> documents);

    List<Document> listOfDtosToDocuments(List<EddocDto> documents);
}
