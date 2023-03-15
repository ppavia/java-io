package fr.cimut.ged.entrant.interrogation.rest;

import fr.cimut.ged.entrant.beans.*;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.dto.DocumentRestDto;
import fr.cimut.ged.entrant.dto.DocumentsFilters;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.interrogation.rest.aspect.RestRequestInterceptor;
import fr.cimut.ged.entrant.service.DocumentService;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.GedeIdHelper;
import org.apache.log4j.Logger;
import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.*;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author spare
 */
@Stateless
@Interceptors({ RestRequestInterceptor.class })
@Path("/document")
public class DocumentEndpoint extends EndpointAbstract {

	public static final Logger logger = Logger.getLogger(DocumentEndpoint.class);

	protected static final String AUTHORIZATION = "Authorization";
	protected static final String ID = "id";
	protected static final String CONTEXTE = "contexte";
	protected static final String CMROC = "cmroc";
	protected static final String ENV = "env";
	protected static final String CODE_PACK = "codePack";
	protected static final String CODE_PRODUIT = "codeProduit";
	protected static final String CODE_GARANTIE = "codeGarantie";
	protected static final String NUM_PAGE = "page";
	protected static final String SIZE = "size";
	protected static final String TYPE_CODE = "typeCode";
	@EJB
	protected DocumentService documentService;

	@Path("/referencement")
	@POST
	@Consumes(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response referencementDocument(@HeaderParam("Authorization") String authKey, DocumentRestDto[] docsJson) throws GedeException {
		List<String> listIdinserted = documentService.insertBDDEdt(docsJson);
		return Response.ok("[{" + listIdinserted.size() + "}]", MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();

	}

	@Path("/adherent")
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response searchDocumentAdherent(@HeaderParam("Authorization") String authKey, @QueryParam("sAssure") Long sAssu,
			@QueryParam("contexte") TypeContexte contexte, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir,
			@QueryParam("typeCode") String typeCode)
			throws GedeException {
		DocumentsFilters filters = new DocumentsFilters(contexte, typeCode);
		SearchDocResponse responseSearch = documentService.searchDocStarweb(sAssu, null, null, contexte, cmroc, envir, filters);
		return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@Path("/entreprise")
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response searchDocumentEntreprise(@HeaderParam("Authorization") String authKey, @QueryParam("idEntreprise") Long idEntreprise,
			@QueryParam("contexte") TypeContexte contexte, @QueryParam("cmroc") String cmroc, @QueryParam("env") String envir,
			@QueryParam("typeCode") String typeCode)
			throws GedeException {
		DocumentsFilters filters = new DocumentsFilters(contexte, typeCode);
		SearchDocResponse responseSearch = documentService.searchDocStarweb(null, idEntreprise, null, contexte, cmroc, envir,
				filters);
		return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@Path("/section")
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response searchDocumentSection(@HeaderParam("Authorization") String authKey, @QueryParam("idEntreprise") Long idEntreprise,
			@QueryParam("idSection") Long idSection, @QueryParam("contexte") TypeContexte contexte, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @QueryParam("typeCode") String typeCode)
			throws GedeException {
		DocumentsFilters filters = new DocumentsFilters(contexte, typeCode);
		SearchDocResponse responseSearch = documentService.searchDocStarweb(null, idEntreprise, idSection, contexte, cmroc, envir,
				filters);
		return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@Path("/garantie")
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response searchDocumentGarantie(@HeaderParam("Authorization") String authKey, @QueryParam("codePack") String codePack,
			@QueryParam("codeGarantie") String codeGarantie, @QueryParam("contexte") TypeContexte contexte, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir)
			throws GedeException {
		SearchDocResponse responseSearch = documentService.searchDocProduitPackGarantie(codePack, codeGarantie, null, contexte, cmroc, envir);
		return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@Path("/pack")
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response searchDocumentPack(@HeaderParam("Authorization") String authKey, @QueryParam("codePack") String codePack,
			@QueryParam("codeProduit") String codeProduit, @QueryParam("contexte") TypeContexte contexte, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir)
			throws GedeException {
		SearchDocResponse responseSearch = documentService.searchDocProduitPackGarantie(codePack, null, codeProduit, contexte, cmroc, envir);
		return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@Path("/historiqueIDOC")
	@GET
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response getHistoIdoc(@HeaderParam("Authorization") String authKey,
			@QueryParam("cmroc") String cmroc, @QueryParam("env") String env) throws GedeException {
		List<HistoriqueUploadIdoc> responseSearch = documentService.listHistoIdocByCmroc(cmroc, env);
		return Response.ok(responseSearch).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@POST
	@Path("/upload-massif")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response uploadZipFile(@HeaderParam("Authorization") String authKey, @QueryParam("filename") String filename,
			@MultipartForm UploadFileRequest req, @QueryParam("user") String user,
			@QueryParam("cmroc") String cmroc, @QueryParam("env") String env)
			throws GedeException {
		HistoriqueUploadIdoc feedback = documentService.integMassif(req.getFileData(), env, cmroc, user, filename);
		return Response.ok(feedback).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@POST
	@Path("/upload-unitaire")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
	public Response uploadFile(@HeaderParam("Authorization") String authKey,
			@MultipartForm UploadFileRequest req, @QueryParam("user") String user,
			@QueryParam("cmroc") String cmroc, @QueryParam("env") String env, @QueryParam("filename") String filename)
			throws GedeException {
		documentService.integUnitaire(req, env, cmroc, user, filename);
		return Response.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@GET
	@Path("/launch-integration-file")
	public Response uploadFile(@HeaderParam("Authorization") String authKey, @QueryParam("user") final String user,
			@QueryParam("cmroc") final String cmroc, @QueryParam("env") final String env)
			throws GedeException {
		// Lancement de l'intégration de masse via le file system de manière asynchrone
		Thread t = new Thread() {
			@Override
			public void run() {
				documentService.integFileSystem(env, cmroc, user);
			}
		};
		t.start();
		return Response.ok().build();
	}

	@GET
	@Path("/launch-fk-reconciliation")
	public Response reconciliationFkType(@HeaderParam("Authorization") String authKey, @QueryParam("user") final String user,
			@QueryParam("cmroc") final String cmroc, @QueryParam("env") final String env)
			throws GedeException {
		// Lancement de l'intégration de masse via le file system de manière asynchrone
		logger.info("Début de la réconciliation des foreign key document -> type (Exécutée par "+user+")");
		Thread t = new Thread() {
			@Override
			public void run() {
				documentService.reconciliationFkType(env, cmroc, user);
			}
		};
		t.start();
		return Response.ok().build();
	}

	@GET
	@Path("/test-add-note-sude")
	public Response testAddNoteSude(@HeaderParam("Authorization") String authKey, @QueryParam("user") String user, @QueryParam("cmroc") String cmroc,
			@QueryParam("env") String envir, @QueryParam("RECLAM_ID") String idSude)
			throws GedeException {

		Document testDocument = new Document();
		testDocument = DocumentHelper.setDefaultValue(testDocument, envir);
		testDocument.setId(DocumentHelper.generateFinalName("test-integ-unitaire", ".png"));
		testDocument.setCmroc(cmroc);
		testDocument.setDtcreate(new Date());
		testDocument.setMimeType("image/png");
		testDocument.setTypeDocument("test-integ-unitaire");
		testDocument.setJson(null);
		GedeIdHelper.setIdstarTstar(testDocument, "000049642759_20191212164459");
		testDocument.getDocMongo().setTypeEntiteRattachement(TypeEntite.SUDE);
		testDocument.getDocMongo().setSudeId(idSude);

		documentService.testAddNotSude(envir, cmroc, user, testDocument);
		String feedback = "OK";
		return Response.ok("[{" + feedback + "}]", MediaType.APPLICATION_JSON)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@DELETE
	@Path("/delete")
	public Response deleteDocument(@HeaderParam("Authorization") String authKey,
			@QueryParam("cmroc") String cmroc, @QueryParam("contexte") TypeContexte contexte,
			@QueryParam("env") String env, @QueryParam("documentIds") String documentIds)
			throws GedeException {
		String reponse = documentService.deleteList(documentIds, env, cmroc);
		if (reponse.length() > 0) {
			return Response.serverError().entity(reponse).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
		}
		return Response.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

	@PUT
	@Path("/update")
	public Response updateDocumentFile(@HeaderParam("Authorization") String authKey,
			@MultipartForm UploadFileRequest req, @QueryParam("cmroc") String cmroc, 
			@QueryParam("env") String env, @QueryParam("documentId") String documentId)
			throws GedeException, IOException {
		documentService.replaceFile(req.getFileData(), documentId, env, cmroc);
		return Response.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON + "; charset=UTF-8").build();
	}

}
