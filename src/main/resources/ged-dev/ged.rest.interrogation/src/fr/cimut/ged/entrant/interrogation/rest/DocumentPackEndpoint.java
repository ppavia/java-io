package fr.cimut.ged.entrant.interrogation.rest;

import fr.cimut.ged.entrant.beans.SearchDocResponse;
import fr.cimut.ged.entrant.beans.TypeContexte;
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
@Path("/packs")
public class DocumentPackEndpoint extends DocumentEndpoint {

    @Path("/{id}/garanties/{id}/produits/{codeProduit}/documents")
    @GET
    @Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    public Response searchDocumentPack(
            @HeaderParam(AUTHORIZATION) String authKey,
            @PathParam(CODE_PACK) Long codePack,
            @PathParam(CODE_GARANTIE) Long codeGarantie,
            @PathParam(CODE_PRODUIT) Long codeProduit,
            @QueryParam(CONTEXTE) TypeContexte contexte,
            @QueryParam(CMROC) String cmroc,
            @QueryParam(ENV) String envir)
            throws GedeException {
        SearchDocResponse responseSearch = documentService.searchDocStarwebByGarantie(codeGarantie, contexte, cmroc, envir);
        return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
    }
}
