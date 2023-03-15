package fr.cimut.ged.entrant.interrogation.rest;

import fr.cimut.ged.entrant.beans.TypeContexte;
import fr.cimut.ged.entrant.dto.DocumentsFilters;
import fr.cimut.ged.entrant.dto.SearchFilteredDocsDto;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Endpoint pour la gestion du chargement des documents.
 */
@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/sections")
public class DocumentSectionEndpoint extends DocumentEndpoint {

    @Path("/{id}/documents")
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response searchDocumentSection(
            @HeaderParam(AUTHORIZATION) String authKey,
            @PathParam(ID) Long idSection,
            @QueryParam(CONTEXTE) TypeContexte contexte,
            @QueryParam(CMROC) String cmroc,
            @QueryParam(ENV) String envir,
            @QueryParam(NUM_PAGE) int page,
            @QueryParam(SIZE) int size,
            @QueryParam(TYPE_CODE) String typeCode)
            throws GedeException {
        DocumentsFilters filters = new DocumentsFilters(contexte, typeCode);

        SearchFilteredDocsDto responseSearch = documentService.searchDocStarwebBySection(idSection, contexte, cmroc, envir, page, size, filters);
        return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }
}
