package fr.cimut.ged.entrant.interrogation.rest;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import fr.cimut.ged.entrant.dto.TypeDto;
import fr.cimut.ged.entrant.mapper.TypeDtoMapper;
import org.json.JSONException;
import org.json.JSONObject;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.exceptions.NotFoundException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.DocumentService;
import fr.cimut.ged.entrant.service.TypeService;
import org.mapstruct.factory.Mappers;

@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/documents")
public class DocumentContoller {
	
	@EJB
	protected DocumentService documentService;
	
	@EJB
	protected TypeService typeService;
	
	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateDocumentData(@HeaderParam("Authorization") String authKey,@PathParam("id") String documentId, String json,
			 @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir)
			throws GedeException, IOException, JSONException, ParseException {
		
		Document document = documentService.getByEddocId(documentId);
		if(document != null ) {
			TypeDto newDocType = null;
			JSONObject data = new JSONObject(json);
			String DEFAULT_PATTERN = "yyyyMMdd";
			SimpleDateFormat formatter = new SimpleDateFormat(DEFAULT_PATTERN);
			formatter.setLenient(false);
			String typeCode = (data.has("typeCode")) ? (String) data.get("typeCode"):null;
			String libelle = (data.has("libelle")) ? (String) data.get("libelle") : null;
			Date dtDebutValidite = null;			
			Date dtFinValidite = null;
			try {

				if(data.has("dtDebutValidite") ) {
					if(data.get("dtDebutValidite").equals("")) {
						dtDebutValidite = null;
					} else {
						dtDebutValidite = formatter.parse(data.get("dtDebutValidite").toString());
					}
				}
				
				if(data.has("dtFinValidite")) {
					if(data.get("dtFinValidite").equals("")) {
						dtFinValidite = null;
					} else {
						dtFinValidite = formatter.parse(data.get("dtFinValidite").toString());
					}
				}
				
			} catch (ParseException pe ) {
				return Response.status(Response.Status.BAD_REQUEST).entity("Les dates doivent être au format  AAAAMMJJ").build();
			}
				
			if(typeCode != null) {
					newDocType = typeService.getTypeByCode(typeCode,null, null);
					if(newDocType == null) {
						return Response.status(Response.Status.BAD_REQUEST).entity("Le type "+ typeCode +" n'éxiste pas").build();

					}
			
			}
			if(libelle != null) {
				try {
					documentService.updateInfoDocumentStarwebDao(documentId, libelle, envir, cmroc);
				}catch (GedeException e) {
					return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
				}
				document.setLibelle(libelle);
			}
			
			if(dtDebutValidite != null && dtFinValidite != null && dtDebutValidite.after(dtFinValidite)) {
				return Response.status(Response.Status.BAD_REQUEST).entity("La date de début de validité doit être antérieur a la date de fin de validité ").build();
			}  
			
			if(data.has("dtDebutValidite")) {
				document.setDtDebutValidite(dtDebutValidite);
			}
			 
			if(data.has("dtFinValidite")) {
				document.setDtFinValidite(dtFinValidite);
			}	

			if(typeCode != null) {
				TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);

				// Deprecated = Si DocumentController avait une séparation Dao/Dto on n'aurait pas à faire cette conversion ici
				document.setType(mapper.typeDtoToType(newDocType));
			}
			
			return Response.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").entity(document).build();
		} else {
			throw new NotFoundException("Le document " + documentId + " n'existe pas");
		}	
	}

}
