package fr.cimut.ged.entrant.service;

import com.mongodb.BasicDBObject;
import fr.cimut.ged.entrant.beans.*;
import fr.cimut.ged.entrant.beans.db.Categorie;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.ids.EditicId;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentDto;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentInfoComp;
import fr.cimut.ged.entrant.dao.AssiaClaims;
import fr.cimut.ged.entrant.dao.DocumentDao;
import fr.cimut.ged.entrant.dao.TypeDao;
import fr.cimut.ged.entrant.dao.starwebdao.TechnicalRestDao;
import fr.cimut.ged.entrant.dto.*;
import fr.cimut.ged.entrant.exceptions.*;
import fr.cimut.ged.entrant.mapper.DocumentRestDtoMapper;
import fr.cimut.ged.entrant.mapper.EddocMapper;
import fr.cimut.ged.entrant.mapper.PaginationMapper;
import fr.cimut.ged.entrant.mongo.Manager;
import fr.cimut.ged.entrant.mongo.MongoManagerFactory;
import fr.cimut.ged.entrant.utils.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.joda.time.DateTime;
import org.mapstruct.factory.Mappers;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;
import java.util.regex.Pattern;

import static fr.cimut.ged.entrant.beans.TypeContexte.EXTRANET;

@Stateless(mappedName = "Document")
//@Interceptors({ DebugServiceInterceptor.class })
public class DocumentService {

	private static final String MSG_INTEGRATION_EN_COURS = "Une Intégration est en cours";

	@EJB
	DocumentDao daoDocEdt;

	// On utilise directement le TypeDao eu lieu du service
	// car celui-ci pointe directement vers la base de données Editique.
	//
	// Justification:
	// Les objets récupérés via TypeService ne sont pas tous inclus dans la session hibernate et
	// peuvent provenir d'une autre base de données

	@EJB
	TypeDao daoType;

	// Pour récupérer les paramètres de type uniquement
	@EJB
	TypeService typeService;

	@EJB
	Metier metier;

	@EJB
	DocumentMongoService documentMongoService;

	// semaphore pour la gestion des upload de Zip
	private static final Semaphore SEMAPHORE_UPLOAD_FILE = new Semaphore(1);

	private static final String ERROR_MSG_CALLDAO = "Erreur lors de l'appel à starwebDao";

	private static final Logger LOGGER = Logger.getLogger(DocumentService.class);

	private static final Pattern TYPE_MODELE_COURRIER = Pattern.compile("^[0-9]{4}L[RPT]{1}[0-9]{4}$");

	private static final String NON_DEFINI = "non defini";


	// liste des id Mongo des flag "intégration en cours". Permet de re-tenter un suppression en cas d'échec lors de l'appel initial.
	// Cette liste contient normalement 0 ou 1 élément, plus si il y a eu un problème de suppression côté Mongo
	private List<String> idDocIntegEnCoursList = new ArrayList<String>();

	public List<String> insertBDDEdt(DocumentRestDto[] documentRestDtos) throws GedeException {
		List<String> idsInserted = new ArrayList<String>();
		DocumentRestDtoMapper documentRestDtoMapper = Mappers.getMapper(DocumentRestDtoMapper.class);
		
		List<Document> documents = documentRestDtoMapper.listOfDtosToDocuments(Arrays.asList(documentRestDtos));

		//initialisation de la map des type de manière groupée codeTypeDocument => type persisté
		// traitement réalisé pour optimiser les performances : gain constaté le 11/12 (listing des type + desactivation slush : 3 secondes de traitement au lieu de 60)
		Set<String> typeDocuments = new HashSet<String>();
		Map<String, Type> mapTypeDocuments = new HashMap<String, Type>();
		for (Document doc : documents) {
			typeDocuments.add(getCorrespondingTypeValue(doc.getTypeDocument()));
		}

		// gestion des type inconu
		boolean allowInconnuType = GlobalVariable.allowTypeDocumentInconnu();
		if (allowInconnuType) {
			// on ajoute le type inconnu
			typeDocuments.add(Type.CODE_INCONNU);
		}

		// recherche des type correspondant en base
		List<Type> typesToSearch = daoType.listTypeByCodeList(typeDocuments);

		// on construit la map d'accès rapide typeDocument => typePersisté
		if (CollectionUtils.isNotEmpty(typesToSearch)) {
			for (Type type : typesToSearch) {
				mapTypeDocuments.put(type.getCode(), type);
			}
		}

		// partcours effectif des document pour intégration
		for (Document doc : documents) {
			Type persistedType = mapTypeDocuments.get(getCorrespondingTypeValue(doc.getTypeDocument()));
			if (null == persistedType && allowInconnuType) {
				persistedType = mapTypeDocuments.get(Type.CODE_INCONNU);
			}

			createAndLinkToType(doc, persistedType, false);
			idsInserted.add(doc.getId());
		}
		// ici on flush tout pour s'assurer de la bonne gestion des erreurs
		// le flush a été préalablement desactivé pour les insert de document car trop couteux en terme de perf lors de traitements en masse

		daoDocEdt.flush();
		return idsInserted;

	}

	/**
	 * equivalence du type de document et gestion du modele de courrier
	 * 
	 * @param typeDocument
	 * @return
	 */
	private String getCorrespondingTypeValue(String typeDocument) {
		String typeDocToSearch = null;
		if (TYPE_MODELE_COURRIER.matcher(typeDocument).find()) {
			// gestion spécifique du type modele de courrier
			// les modeles de courrier doivent être rattachés sour le type modele de courrier
			typeDocToSearch = Type.CODE_MODELE_COURRIER;

			// TODO cache applicatif sur le type persisité modele de courrier
			// ca cas arrive souvent, ca voudrait le coup de s'affranchir de la requete de recherche a chaque fois

		} else {
			typeDocToSearch = typeDocument;
		}
		return typeDocToSearch;
	}

	/**
	 * méthode de création de document : elle s'occupe également de retrouver le bon idType a rattacher en fonction du
	 * code si celui ci n'a pas été fourni
	 * @param env
	 * @param documentDto
	 * @param allowTypeInconnu
	 * @param allowOnlyIdoc
	 * @return
	 * @throws GedeException
	 */
	public Document create(String env, Document documentDto, boolean allowTypeInconnu, boolean allowOnlyIdoc) throws GedeException {
		// si le Json du document est vide alors on le set a null sinon l'insert en base plante.
		// a supprimer lorsque l'on arretera de sérialiser des docmongo dans doc.Json sans référencement dans mongo
		// cf TODO refondre toutes les valorisations du type document.setJson() pour passer sur du document.getDocMongo()
		if (documentDto.getJson() != null && documentDto.getJson().getData() == null) {
			documentDto.setJson(null);
		}

		String typeDocToSearch = getCorrespondingTypeValue(documentDto.getTypeDocument());

		List<Type> list = daoType.listTypeByCriteria(null, typeDocToSearch);
		Type typePersisted = GedeUtils.getUniqueInListIfExists(list);

		// ne vérifier intégrable IDOC, que si nécessaire
		boolean integrableIdoc = fetchIsIntegrableIdoc(env, documentDto, allowOnlyIdoc, typePersisted);

		if (null != typePersisted) {
			if (allowOnlyIdoc && !integrableIdoc) {
				throw new GedeCommonException("Le type de document " + typePersisted.getLibelle() + " n'est pas autorisé pour les intégrations IDOC");
			}
		} else {
			// normalement il faut peter une exception mais dans le cas de la dev et de la recette, toutes les entitées n'ont pas été créées
			if (allowTypeInconnu) {
				List<Type> listTypeInconnu = daoType.listTypeByCriteria(null, Type.CODE_INCONNU);
				typePersisted = GedeUtils.getUniqueInListIfExists(listTypeInconnu);
			} else {
				throw new NotFoundException("Type de document inconnu non autorisé : " + documentDto.getTypeDocument());
			}
		}
		Document createdDocument = createAndLinkToType(documentDto, typePersisted);
		TechnicalRestDao.tryUpdateDocumentTypeFk(documentDto.getCmroc(), env, createdDocument);

		return createdDocument;
	}

	private boolean fetchIsIntegrableIdoc(String env, Document documentDto, boolean allowOnlyIdoc, Type typePersisted) throws GedeException {
		boolean integrableIdoc = false;

		// TODO: Récupération l'ensemble des paramètres de type via une seul requête

		// lors d'une intégration qui ne concerne pas IDOC pas besoin de récupérer cette information
		// en effet l'appel à un endpoint rest est trop couteux
		if (allowOnlyIdoc) {
			TypeParamsDto ocTypeParams = typeService.getOrganismTypeParametersByTypeId(documentDto.getCmroc(), env, typePersisted.getId());
			integrableIdoc = Boolean.TRUE.equals(ocTypeParams.getIntegrableIdoc());
		}
		return integrableIdoc;
	}

	/**
	 * lien d'un document sans flush base
	 * 
	 * @param documentDto
	 * @param typePersisted
	 * @return
	 * @throws GedeException
	 */
	private Document createAndLinkToType(Document documentDto, Type typePersisted) throws GedeException {
		return createAndLinkToType(documentDto, typePersisted, true);
	}

	/**
	 * creation du document lié a leur entité type
	 * 
	 * @param documentDto
	 * @param typePersisted
	 * @return
	 * @throws CimutDocumentException
	 * @throws GedeCommonException
	 * @throws CimutConfException
	 * @throws CimutFileException
	 */
	private Document createAndLinkToType(Document documentDto, Type typePersisted, boolean andFlush) throws GedeException {
		if (null == typePersisted) {
			throw new NotFoundException("impossible de retouver le type de document lié au code " + documentDto.getTypeDocument());
		}
		DocumentHelper.checkValid(documentDto);
		documentDto.setDtcreate(new Date());
		// persiste en base
		documentDto.setType(typePersisted);
		Document documentPersisted = daoDocEdt.create(documentDto, andFlush);

		return documentPersisted;
	}

	public boolean existsDocByIdType(Long idType) {
		return daoDocEdt.countDocByIdType(idType) > 0;
	}

	public Document update(Document docDto) throws CimutDocumentException, CimutFileException, CimutConfException, GedeCommonException {
		DocumentHelper.checkValid(docDto);
		return daoDocEdt.update(docDto);
	}



	public Document get(String id) {
		return daoDocEdt.get(id);
	}

	public Document getByEddocId(String eddocId) {
		return daoDocEdt.getByEddocId(eddocId);
	}

	public void delete(String id) {
		daoDocEdt.delete(id);
	}


	public SearchFilteredDocsDto searchDocStarwebByAssure(
			Long sAssure,
			TypeContexte contexte,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters) throws GedeException {
		// recherche des documents starweb
		WrapperDocumentsStarweb wrapperDocumentsStarweb = searchPaginatedDocAdherentStarweb(sAssure, cmroc, envir, page, size, filters);
		return buildResponseRechercheDocuments(wrapperDocumentsStarweb, cmroc, contexte);
	}
	public SearchFilteredDocsDto searchDocStarwebByAssureWithRang(
			Long sAssure,
			TypeContexte contexte,
			String cmroc,
			String envir,
			int page,
			int size,
			Integer rang,
			DocumentsFilters filters) throws GedeException {
		// recherche des documents starweb
		WrapperDocumentsStarweb wrapperDocumentsStarweb = searchPaginatedDocAdherentWithRangStarweb(sAssure, cmroc, envir, page, size, rang, filters);
		return buildResponseRechercheDocuments(wrapperDocumentsStarweb, cmroc, contexte);
	}
	public SearchFilteredDocsDto searchDocStarwebByEntreprise(
			Long idEntreprise,
			TypeContexte contexte,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters)
			throws GedeException {
		// recherche des documents starweb
		WrapperDocumentsStarweb wrapperDocumentsStarweb = searchPaginatedDocEntrepriseStarweb(idEntreprise, cmroc, envir, page, size, filters);
		return buildResponseRechercheDocuments(wrapperDocumentsStarweb, cmroc, contexte);
	}
	public SearchFilteredDocsDto searchDocStarwebBySection(Long idSection,
			TypeContexte contexte,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters)
			throws GedeException {
		// recherche des documents starweb
		WrapperDocumentsStarweb wrapperDocumentsStarweb = searchPaginatedDocSectionStarweb(idSection, cmroc, envir, page, size, filters);
		return buildResponseRechercheDocuments(wrapperDocumentsStarweb, cmroc, contexte);
	}
	public SearchFilteredDocsDto searchDocStarwebByBeneficiaire(
			Long sBenef,
			TypeContexte contexte,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters) throws GedeException {
		// recherche des documents starweb
		WrapperDocumentsStarweb wrapperDocumentsStarweb = searchPaginatedDocBeneficiaireStarweb(sBenef, cmroc, envir, page, size, filters);
		return buildResponseRechercheDocuments(wrapperDocumentsStarweb, cmroc, contexte);
	}

	// TODO - voir fonctionnement du pack garantie et produit ???
	public SearchDocResponse searchDocStarwebByGarantie(Long idGarantie, TypeContexte contexte, String cmroc, String envir) {
		return new SearchDocResponse();
	}

	/**
	 * Récupère les documents de la base éditique correspondants à la liste des documents starweb passés en paramètre.
	 *
	 * @param listDocsStarweb
	 * @return
	 */
	private List<Document> searchEddoc(List<DocumentDto> listDocsStarweb) {
		List<Document> eddocs = new ArrayList<Document>();

		// formattage des ids des docs starweb pour interrogation base editique
		Map<Long, EditicId> eddocDataIds = GedeIdHelper.getEditicIds(listDocsStarweb);
		// recherche des documents de la base editique
		eddocs = getEddocsByIds(new ArrayList<Long>(eddocDataIds.keySet()));

		return eddocs;
	}

	private SearchFilteredDocsDto buildResponseRechercheDocuments(WrapperDocumentsStarweb wrapperDocumentsStarweb, String cmroc, TypeContexte contexte)
			throws CimutDocumentException {
		SearchFilteredDocsDto response = new SearchFilteredDocsDto();
		EddocMapper eddocMapper = Mappers.getMapper(EddocMapper.class);
		PaginationMapper paginationMapper = Mappers.getMapper(PaginationMapper.class);
		List<Document> processedDocuments = new ArrayList<Document>();

		// recherche des documents eddoc en fonction des documents starweb trouvés
		List<Document> eddocs = searchEddoc(wrapperDocumentsStarweb.getDocuments());

		switch (contexte) {
			case STARWEB:
				processedDocuments = processRechercheDocumentsGeneral(wrapperDocumentsStarweb.getDocuments(), eddocs, cmroc);
				break;
			case EXTRANET:
				processedDocuments = processRechercheDocumentsForExtranet(wrapperDocumentsStarweb.getDocuments(), eddocs);
				break;
			default :
				throw new CimutDocumentException(String.format("le contexte %s d'appel au service n'est pas valide", contexte.toString()));
		}

		applyOcParams(processedDocuments);

		response.setEddocs(eddocMapper.listOfDocumentsToDto(processedDocuments));


		response.setPaginationResponse(paginationMapper.pageToDto(wrapperDocumentsStarweb.getPage()));
		return response;
	}

	private void applyOcParams(List<Document> processedDocuments) {
		for (Document doc : processedDocuments) {
			if (doc.getInfoStarweb() == null || doc.getType() == null) {
				continue;
			}

			// prendre le paramétrage OC lorsque possible

			if (doc.getInfoStarweb().getTypeVisibleExtranet() != null) {
				doc.getType().setVisibleExtranet(doc.getInfoStarweb().getTypeVisibleExtranet());
			}

			if (doc.getInfoStarweb().getTypeDureeVisibleExtranet() != null) {
				doc.getType().setDureeVisibleExtranet(doc.getInfoStarweb().getTypeDureeVisibleExtranet());
			}
		}
	}

	private List<Document> processRechercheDocumentsGeneral (List<DocumentDto> docsStarweb, List<Document> pEddocs, String cmroc) {
		// conversion de la liste de documents editique en map, avec la clé = idstar_tsstar
		Map<String, Document> eddocs = DocumentHelper.convertListEddocToMap(pEddocs);

		List<Document> docListBasedOnStarwebResult = new ArrayList<Document>();
		// complète les informations de chaque document starweb avec les informations du document équivalent editique
		// si le document n'existe pas dans la base éditique, on complète les informations du document starweb par des informations factices.
		for (DocumentDto docStarweb: docsStarweb) {
			String idStarwebWithoutZero = docStarweb.getEddocIdWithoutZero();
			Document erdRefDoc = eddocs.get(idStarwebWithoutZero);
			if (erdRefDoc == null) {
				erdRefDoc = buildFacticeErdRefDoc(idStarwebWithoutZero, cmroc);
			}
			erdRefDoc = selectOneOfDuplicatedEddocsWithDifferentRank(docStarweb, erdRefDoc);
			docListBasedOnStarwebResult.add(erdRefDoc);
		}

		return docListBasedOnStarwebResult;
	}

	/**
	 * Recherche de documents starweb spécifique à l'extranet
	 * @param docsStarweb
	 * @param eddocs
	 * @return
	 */
	private List<Document> processRechercheDocumentsForExtranet (List<DocumentDto> docsStarweb, List<Document> eddocs) {
		// récupération des documents eddoc uniquement visibles pour l'extranet
		List<Document> docsVisiblesPourExtranet = new ArrayList<Document>(eddocs);

		// traitements spécifiques aux documents de l'extranet
		return searchDocsForExtranet(docsStarweb, docsVisiblesPourExtranet, new Date());
	}


	/**
	 * @deprecated Garder pour la non régression
	 * récuperation des documents adhérents avec filtre sur la visibilité extranet
	 * 
	 * @param sAssure
	 * @param contexte
	 * @param envir
	 * @param cmroc
	 * @param filters
	 * @return
	 * @throws GedeException
	 */
	public SearchDocResponse searchDocStarweb(Long sAssure, Long idEntreprise, Long idSection, TypeContexte contexte, String cmroc, String envir,
			DocumentsFilters filters)
			throws GedeException {
		SearchDocResponse response = new SearchDocResponse();
		boolean isDocAdherent = (null != sAssure);
		boolean isDocEntreprise = (null != idEntreprise && null == idSection);
		boolean isDocSection = (null != idSection);
		if (isDocAdherent || isDocEntreprise || isDocSection) {
			List<DocumentDto> docStarwebList = new ArrayList<DocumentDto>();
			// appel de starweb Dao pour récuperer les identifiants de documents liés à l'adhérent dans la base starweb
			if (isDocAdherent) {
				docStarwebList = searchDocAdherentStarweb(sAssure, cmroc, envir, filters);
			} else if (isDocEntreprise) {
				docStarwebList = searchDocEntrepriseStarweb(idEntreprise, cmroc, envir, filters);
			} else if (isDocSection) {
				docStarwebList = searchDocSectionStarweb(idSection, cmroc, envir, filters);
			}
			// filtre technique on prend en compte que les 1000 derniers documents
			//			if (CollectionUtils.size(docStarwebList) > 1000) {
			//				docStarwebList = docStarwebList.subList(0, 1000);
			//			}

			if (CollectionUtils.isNotEmpty(docStarwebList)) {
				List<String> eddocList = GedeIdHelper.getIdsStarwebWithoutZero(docStarwebList);

				// recuperation des documents avec les informations de visibilité extranet / durée
				Collection<Document> erdRefDocList = listDocByEddocIdList(eddocList);

				LOGGER.info(String.format("Nombre de documents dans la liste : %s", erdRefDocList.size()));

				// sur les environnements hors prod les documents peuvent être connus de la base starweb mais pas de la base editique, il faut quand même les ramener
				Date now = new Date();
				if (EXTRANET == contexte) {
					// pour l'extranet on fait le filtre dans ce sens en se basant sur la liste provenant de la base editique
					// et on ajoute les infos complémentaires (rangs de rattachement, libelle star)
					List<Document> docsForExtranet = searchDocsForExtranet(docStarwebList, erdRefDocList, now);
					applyOcParams(docsForExtranet);
					response.setDocuments(docsForExtranet);
				} else {
					// on parcourt plutot les docs issus de starweb et on ajoute les infos provenant de la base editique si possible
					List<Document> docListBasedOnStarwebResult = new ArrayList<Document>();
					for (DocumentDto docStar : docStarwebList) {
						// ce document existe t-il dans la base editique ?
						// oui on le prend
						String idDoc = docStar.getEddocIdWithoutZero();
						Document erdRefDoc = GedeIdHelper.getMatchingEddocInrErdRefDoc(idDoc, erdRefDocList);
						if (null == erdRefDoc) {
							erdRefDoc = buildFacticeErdRefDoc(idDoc, cmroc);
						}
						erdRefDoc = selectOneOfDuplicatedEddocsWithDifferentRank(docStar, erdRefDoc);
						docListBasedOnStarwebResult.add(erdRefDoc);
					}
					response.setDocuments(docListBasedOnStarwebResult);
				}
			}
		}
		return response;
	}

	/**
	 * HL-47374 - Problème de doublon pour 2 documents identique mais avec rang différent.
	 * Un même document existe pour le rang 1 et le rang 11, mais comme en base on a un seul document,
	 * la liste erdRefDocList est chargé avec une seul occurence du-dit document. Donc lorsque'on fait le mapping
	 * docStar - erdRefDoc, pour le premier document pas de problème, pour le second, on récupère le même objet erdRefDoc
	 * qui à déjà eu un setInfoStarweb. Le second appel écrase donc ce premier setInfoStarweb, et on se retrouve avec
	 * deux documents dans la liste docListBasedOnStarwebResult au rang 11.
	 * On fix en contrôlant le rang et en clonant erdRefDoc si nécessaire
	 */
	private Document selectOneOfDuplicatedEddocsWithDifferentRank(DocumentDto docStar, Document erdRefDoc) {
		if (erdRefDoc.getInfoStarweb() != null) {
			erdRefDoc = Document.clone(erdRefDoc);
		}
		erdRefDoc.setInfoStarweb(docStar.getInfoComp());
		return erdRefDoc;
	}

	private List<Document> searchDocsForExtranet(List<DocumentDto> docStarwebList, Collection<Document> erdRefDocList, Date now) {

		List<Document> docsForExtranet = new ArrayList<Document>();
		List<Document> docToRemove = new ArrayList<Document>();
		for (Document doc : erdRefDocList) {
			DocumentInfoComp infoStarweb = GedeIdHelper.getInfoComplementaireForEddocId(doc.getEddocId(), docStarwebList);
			doc.setInfoStarweb(infoStarweb);
			// En contexte extranet on filtre les document hors période de validité
			if ((doc.getDtDebutValidite() != null && doc.getDtDebutValidite().after(now))
					|| (doc.getDtFinValidite() != null && doc.getDtFinValidite().before(now))) {
				docToRemove.add(doc);
			}
		}
		erdRefDocList.removeAll(docToRemove);
		docsForExtranet.addAll(erdRefDocList);
		return docsForExtranet;
	}

	private Document buildFacticeErdRefDoc(String idDoc, String cmroc) {
		Document erdRefDoc;
		// 	non on le recrée en le simulant
		erdRefDoc = new Document();
		try {
			long[] idTs = GedeIdHelper.splitEddocId(idDoc);
			erdRefDoc.setIdstar(idTs[0]);
			erdRefDoc.setTsstar(idTs[1]);
		} catch (CimutDocumentException e) {
			LOGGER.error("Erreur dans la récupération de l'id eddoc fournit en paramètre", e);
		}

		Type type = new Type();
		Categorie categorie = new Categorie();
		type.setCode(NON_DEFINI);
		type.setLibelle(NON_DEFINI);
		categorie.setCode(NON_DEFINI);
		categorie.setLibelle(NON_DEFINI);
		type.setCategorie(categorie);
		erdRefDoc.setType(type);
		erdRefDoc.setCmroc(cmroc);
		return erdRefDoc;
	}





	/**
	 * 
	 * @param codePack
	 * @param codeProduit
	 * @param contexte
	 * @param cmroc
	 * @param envir
	 * @return
	 * @throws CimutMongoDBException
	 */
	public SearchDocResponse searchDocProduitPackGarantie(String codePack, String codeGarantie, String codeProduit, TypeContexte contexte,
			String cmroc, String envir) throws GedeException {

		SearchDocResponse response = new SearchDocResponse();
		if (StringUtils.isNotBlank(codePack) || StringUtils.isNotBlank(codeProduit) || StringUtils.isNotBlank(codeGarantie)) {
			// recherche des documents en base Mongo
			Collection<DocumentMongo> listDocMongo = documentMongoService.listDocumentMongoPackGarantie(cmroc, codeGarantie, codeProduit, codePack, envir);

			// formatte les id
			List<String> eddocList = GedeIdHelper.extractEddocIdFromDocMongo(listDocMongo);
			if (CollectionUtils.isNotEmpty(eddocList)) {
				// recuperation des documents avec les informations de visibilité extranet / durée
				Collection<Document> erdRefDocList = listDocByEddocIdList(eddocList);

				//filtre des documents non visible si contexte extranet
				filtreDocByContexte(erdRefDocList, contexte);

				Date now = new Date();
				if (EXTRANET == contexte) {
					// pour l'extranet on fait le filtre dans ce sens en se basant sur la liste provenant de la base editique
					// et on ajoute les infos complémentaires (rangs de rattachement, libelle star)
					List<Document> docToRemove = new ArrayList<Document>();
					for (Document doc : erdRefDocList) {
						// En contexte extranet on filtre les document hors période de validité
						if ((doc.getDtDebutValidite() != null && doc.getDtDebutValidite().after(now))
								|| (doc.getDtFinValidite() != null && doc.getDtFinValidite().before(now))) {
							docToRemove.add(doc);
						}
					}
					erdRefDocList.removeAll(docToRemove);
				}
				// ajout dans la réponse
				response.setDocuments(erdRefDocList);
			}

		}

		return response;
	}


	/**
	 * @deprecated pas besoin de checker le contexte !! vu que c'est un traitement dédié à l'extranet.
	 * filtre des documents selon le contexte extranet par exemple (visible ?)
	 * 
	 * @param docList
	 * @param contexte
	 */
	private void filtreDocByContexte(Collection<Document> docList, TypeContexte contexte) {
		//si on est dans un contexte extranet alors on doit vérifier la visibilité du document et sa durée de rétention

		if (EXTRANET == contexte) {
			for (Iterator<Document> iter = docList.iterator(); iter.hasNext();) {
				final Document doc = iter.next();
				if (!doc.isVisibleExtranetConsideringTypeAndDate()) {
					iter.remove();
				}
			}
		}

	}

	/**
	 * recuperation des documents en base editique
	 * 
	 * @param eddocList
	 * @return
	 */
	private Collection<Document> listDocByEddocIdList(List<String> eddocList) {
		List<Document> docEdtList = null;
		if (CollectionUtils.isNotEmpty(eddocList)) {
			// pour contrainte systeme on ne peut pas depasser plus de 1000 documents et en plus l'interet est limité 
			// filtre technique on prend en compte que les 1000 derniers documents
			if (CollectionUtils.size(eddocList) > 1000) {
				eddocList = eddocList.subList(0, 1000);
			}

			List<Long> idstars = new ArrayList<Long>();
			List<Long> tsstars = new ArrayList<Long>();

			for (String eddocId : eddocList) {
				if (StringUtils.contains(eddocId, "_")) {
					// 	on transforme les listes de string en listes en long (supprime les éventuels 0 paddés à gauche)
					String[] compoundId = eddocId.split("_");
					idstars.add(new Long(compoundId[0]));
					tsstars.add(new Long(compoundId[1]));

				}
			}

			if (CollectionUtils.isNotEmpty(idstars)) {
				// appel à la dao
				docEdtList = daoDocEdt.listDocByEddocIdList(idstars, tsstars);

				deleteDuplicateDocEntries(docEdtList);
			}
		}

		return GedeUtils.listEmptyIfNull(docEdtList);
	}

	private List<Document> getEddocsByIds (List<Long> editicIds) {
		List<Document> documentsEditiques = new ArrayList<Document>();
		if (CollectionUtils.isNotEmpty(editicIds)) {
			// appel à la dao
			documentsEditiques = daoDocEdt.selectEddocsByIdstar(editicIds);

			deleteDuplicateDocEntries(documentsEditiques);
		}
		return documentsEditiques;
	}

	/**
	 * Cette méthode et son usage sont conservés pour le moment (2021-03-28)
	 * @param docEdtList
	 */
	private void deleteDuplicateDocEntries(final List<Document> docEdtList) {
		if (CollectionUtils.isNotEmpty(docEdtList)) {
			List<String> listDocIDFound = new ArrayList<String>();

			// il peut y avoir des doublons dans ce cas on garde seulement l'occurence la plus récente
			for (Iterator<Document> iter = docEdtList.listIterator(); iter.hasNext(); ) {
				final Document doc = iter.next();
				String currentKey = String.valueOf(doc.getIdstar()) + "_" + String.valueOf(doc.getTsstar());

				// alimentation du HashMap
				if (listDocIDFound.contains(currentKey)) {
					// supprime le doublon, on conserve le premier, le plus recent puis il y a un order by e.dtcreated DESC
					iter.remove();
				} else {
					// initialise le point d'entree
					listDocIDFound.add(currentKey);
				}
			}
		}
	}

	// ---------------- APPEL StarwebDao AVEC PAGINATION
	private WrapperDocumentsStarweb searchPaginatedDocAdherentStarweb(
			Long sAssure,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters) throws GedeException {
		String path = "/adherents/" + sAssure;
		return callPaginatedDocumentsServiceDao(path, cmroc, envir, page, size, filters);
	}
	
	private WrapperDocumentsStarweb searchPaginatedDocAdherentWithRangStarweb(
			Long sAssure,
			String cmroc,
			String envir,
			int page,
			int size,
			Integer rang,
			DocumentsFilters filters) throws GedeException {
		String path = "/adherents/" + sAssure;
		return callPaginatedDocumentsServiceDao(path, cmroc, envir, page, size, rang, filters);
	}

	private WrapperDocumentsStarweb searchPaginatedDocEntrepriseStarweb(
			Long idEntreprise,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters) throws GedeException {
		String path = "/entreprises/" + idEntreprise;
		return callPaginatedDocumentsServiceDao(path, cmroc, envir, page, size, filters);
	}

	private WrapperDocumentsStarweb searchPaginatedDocSectionStarweb(
			Long idSection,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters) throws GedeException {
		String path = "/sections/" + idSection;
		return callPaginatedDocumentsServiceDao(path, cmroc, envir, page, size, filters);
	}
	
	private WrapperDocumentsStarweb searchPaginatedDocBeneficiaireStarweb(
			Long sBenef,
			String cmroc,
			String envir,
			int page,
			int size,
			DocumentsFilters filters) throws GedeException {
		String path = "/beneficiaires/" + sBenef;
		return callPaginatedDocumentsServiceDao(path, cmroc, envir, page, size, filters);
	}

	/**
	 * Appel à StarwebDao pour récupérer les documents avec pagination
	 * @param path
	 * @param cmroc
	 * @param envir
	 * @return
	 * @throws GedeException
	 */
	private WrapperDocumentsStarweb callPaginatedDocumentsServiceDao(String path, String cmroc, String envir, int page, int size, DocumentsFilters filters) throws GedeException {
		Map<String, String> headerParams = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		String target = buildGetDocumentsUrl(path, cmroc, page, size, null, filters);
		return executeRequestGetDocuments(target, headerParams);
	}
	
	/**
	 * Appel à StarwebDao pour récupérer les documents avec pagination en fonction du rang bénéficiare
	 * @param path
	 * @param cmroc
	 * @param envir
	 * @return
	 * @throws GedeException
	 */
	private WrapperDocumentsStarweb callPaginatedDocumentsServiceDao(String path, String cmroc, String envir, int page,
			int size, Integer rang, DocumentsFilters filters) throws GedeException {
		Map<String, String> headerParams = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		String target = buildGetDocumentsUrl(path, cmroc, page, size, rang, filters);
		return executeRequestGetDocuments(target, headerParams);
	}
	
	private String buildGetDocumentsUrl (String path, String cmroc, int page, int size, Integer rang, DocumentsFilters filters) throws CimutConfException {
		StringBuilder target = new StringBuilder();
		target.append(GlobalVariable.getStarwebDaoWsUrl())
				.append(path)
				.append("/documents");

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

		// le service peut être appelé avec ou sans la pagination
		if (page != 0 && size != 0) {
			parameters.add(new BasicNameValuePair("page", Integer.toString(page)));
			parameters.add(new BasicNameValuePair("size", Integer.toString(size)));
		}
		if (rang != null) {
			parameters.add(new BasicNameValuePair("rang", Integer.toString(rang)));
		}

		if (filters.getVisibleExtranet() != null) {
			String visibleExtranet = Boolean.toString(filters.getVisibleExtranet());
			parameters.add(new BasicNameValuePair("visibleExtranet", visibleExtranet));
		}

		if (filters.getTypeCode() != null) {
			parameters.add(new BasicNameValuePair("typeCode", filters.getTypeCode()));
		}

		return parameters.isEmpty() ? target.toString() :
				target.toString() + '?' + URLEncodedUtils.format(parameters, "utf-8");
	}
	
	private WrapperDocumentsStarweb executeRequestGetDocuments (String target, Map<String, String> headerParams) throws GedeCommonException {
		WrapperDocumentsStarweb wrapperDocuments;
		try {
			wrapperDocuments = RestClientUtils.executeGetRequest(
					target,
					headerParams,
					WrapperDocumentsStarweb.class);
		} catch (IOException e) {
			throw new GedeCommonException(ERROR_MSG_CALLDAO, e);
		}
		return wrapperDocuments;
	}
	// ---------------- END APPEL StarwebDao AVEC PAGINATION

	/**
	 * Recuperation des documents connus de starweb via appel à starwebDao
	 * @param sAssure
	 * @param cmroc
	 * @param envir
	 * @return
	 * @throws GedeException
	 */
	private List<DocumentDto> searchDocAdherentStarweb(Long sAssure, String cmroc, String envir, DocumentsFilters filters) throws GedeException {
		return callGetDocumentStarwebDao("/adherent/" + Long.toString(sAssure), cmroc, envir, filters);
	}

	private List<DocumentDto> searchDocEntrepriseStarweb(Long idEntreprise, String cmroc, String envir, DocumentsFilters filters) throws GedeException {
		return callGetDocumentStarwebDao("/entreprise/" + Long.toString(idEntreprise), cmroc, envir, filters);
	}

	private List<DocumentDto> searchDocSectionStarweb(Long idSection, String cmroc, String envir, DocumentsFilters filters) throws GedeException {
		return callGetDocumentStarwebDao("/section/" + Long.toString(idSection), cmroc, envir, filters);
	}

	/**
	 * appel a starwebDao
	 * 
	 * @param suffixUrl
	 * @param cmroc
	 * @param envir
	 * @return
	 * @throws GedeException
	 */
	private List<DocumentDto> callGetDocumentStarwebDao(String suffixUrl, String cmroc, String envir, DocumentsFilters filters) throws GedeException {
		Map<String, String> headerParam = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		List<DocumentDto> docs;
		StringBuilder target = new StringBuilder();
		target.append(GlobalVariable.getStarwebDaoWsUrl())
				.append("/document")
				.append(suffixUrl);

		List<BasicNameValuePair> parameters = new ArrayList<BasicNameValuePair>();

		if (filters.getVisibleExtranet() != null) {
			String visibleExtranet = Boolean.toString(filters.getVisibleExtranet());
			parameters.add(new BasicNameValuePair("visibleExtranet", visibleExtranet));
		}

		if (filters.getTypeCode() != null) {
			parameters.add(new BasicNameValuePair("typeCode", filters.getTypeCode()));
		}

		// Construction de l'url cible avec paramètres lorsque nécessaire
		String url = parameters.isEmpty() ? target.toString() :
				target.toString() + '?' + URLEncodedUtils.format(parameters, "utf-8");

		try {
			docs = RestClientUtils.executeGetListRequest(
					url,
					headerParam,
					DocumentDto.class);
		} catch (IOException e) {
			throw new GedeCommonException(ERROR_MSG_CALLDAO, e);
		}
		return docs;
	}

	private void callDeleteDocumentsStarwebDao(String id, String cmroc, String envir) throws GedeException {
		Map<String, String> headerParam = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		try {
			RestClientUtils.executeDeleteRequest(GlobalVariable.getStarwebDaoWsUrl() + "/document/delete/" + id, headerParam);
		} catch (IOException e) {
			throw new GedeCommonException(ERROR_MSG_CALLDAO, e);
		}
	}
	
	public void callUpdateDocumentInfoStarwebDao(String id, String libelle, String cmroc, String envir) throws GedeException, UnsupportedEncodingException {
		Map<String, String> headerParam = GedeUtils.buildHeaderForStarwebDao(cmroc, envir);
		Map<String, String> parametres = new HashMap<String, String>();
		parametres.put("libelle",libelle);
		try {
			RestClientUtils.executePostRequest(GlobalVariable.getStarwebDaoWsUrl() + "/documents/" + id, headerParam, parametres);
		} catch (IOException e) {
			throw new GedeCommonException(ERROR_MSG_CALLDAO, e);
		}
	}




	public HistoriqueUploadIdoc integMassif(byte[] bytes, String env, String cmroc, String user, String zipName) throws GedeCommonException {
		// On trim le zipName, IE ramène tout le chemin du fichier i.e. C:\....\fichier.zip
		zipName = FileHelper.trimBackSlashs(zipName);
		HistoriqueUploadIdoc resultIntegration = new HistoriqueUploadIdoc();
		File tempZipDir = null;
		// ne pas permettre deux executions de cette méthode en simultané (toute mutuelle confondue)
		String idDocIntegEnCours = UUID.randomUUID().toString();
		if (SEMAPHORE_UPLOAD_FILE.tryAcquire()) {
			try {
				// 1ère action : ajouter une ligne dans Mongo pour marquer l'intégration comme "en cours",
				// à retourner avec l'appel à l'endpoint listeHitoIDOC
				flushAndInsertMongoIntegEnCours(env, cmroc, user, zipName, idDocIntegEnCours);
				UploadMassifResult uploadMassifResult = DocumentHelper.getDocumentsFromZipByteArray(bytes, env, cmroc, daoType);
				// On récupère le dossier d'extraction temporaire pour la suppression en fin de méthode.
				// On a forcément un rapport d'intégration ici, il correspond au fichier d'index original. Une exception est lancée sinon
				tempZipDir = uploadMassifResult.getRapportIntegration().getParentFile();
				Map<DocumentUploadMassifWrapper, File> documentToUploadMap = uploadMassifResult.getDocumentToUploadMap();
				Map<Integer, UploadStatut> uploadStatutMap = uploadMassifResult.getUploadStatutMap();
				resultIntegration.setIntegrationTotal(new Long(uploadStatutMap.size()));
				resultIntegration.setIntegrationOK(0l);
				// On compte les demandes d'intégrations qui on un statut KO avant même l'intégration unitaire 
				long statutIntegrationKO = 0l;
				for (Entry<Integer, UploadStatut> entry : uploadStatutMap.entrySet()) {
					if (!entry.getValue().isOk()) {
						statutIntegrationKO++;
					}
				}
				resultIntegration.setIntegrationKO(statutIntegrationKO);
				int i = 0;
				for (Entry<DocumentUploadMassifWrapper, File> entry : documentToUploadMap.entrySet()) {
					try {
						LOGGER.info("Début d'intégration unitaire");
						integUnitaire(entry.getKey().getDocument(), entry.getValue(), env, user, true);
						resultIntegration.setIntegrationOK(resultIntegration.getIntegrationOK() + 1);
						LOGGER.info("Fin d'intégration unitaire");
					} catch (Exception e) {
						LOGGER.error("Problème lors d'une intégration unitaire pour l'upload massif", e);
						UploadStatut uploadStatut = new UploadStatut();
						uploadStatut.setOk(false);
						// On récupère le message d'erreur en enlenvant la classe de l'exception lancée originellement
						String errorMessage = e.toString();
						if (errorMessage.contains(":")) {
							errorMessage = errorMessage.split(":")[1];
							if (e.toString().contains("CimutMetierException")) {
								errorMessage = "Problème dans l'appel au MOS : " + errorMessage;
							}
						}
						uploadStatut.setErrorMessage("Problème technique lors de la sauvegarde en GED : " + errorMessage);
						uploadMassifResult.getUploadStatutMap().put(entry.getKey().getRowNum(), uploadStatut);
						resultIntegration.setIntegrationKO(resultIntegration.getIntegrationKO() + 1);
					}
					i++;
					// MAJ de l'intégration en cours tout les 10 documents traités
					if (i % 10 == 0) {
						updateMongoIntegEnCours(env, cmroc, resultIntegration, idDocIntegEnCours);
					}
				}
				// Préparation du rapport d'intégration et archivage
				LOGGER.info("Préparation et sauvegarde du rapport d'intégration");
				ByteArrayOutputStream rapportOutputStream = DocumentHelper.prepareRapportIntegration(uploadMassifResult.getRapportIntegration(),
						uploadMassifResult);
				String idEddmRapport = null;
				if (rapportOutputStream != null) {
					Document rapportIntegrationDocument = DocumentHelper.generateRapportIntegrationDocument(env, cmroc,
							resultIntegration.getIntegrationTotal(), resultIntegration.getIntegrationOK(), resultIntegration.getIntegrationKO(), user,
							zipName);
					// XXX Economiser une sauvegarde en disque en créant directement le fichier de rapport au bon endroit
					File rapportFile = new File(File.separator + "tmp" + File.separator + "rapportIntegrationIDOC.xlsx");
					FileOutputStream rapportFileOutputStream = null;
					try {
						rapportFileOutputStream = new FileOutputStream(rapportFile);
						rapportFileOutputStream.write(rapportOutputStream.toByteArray());
						rapportFileOutputStream.flush();
						idEddmRapport = integUnitaire(rapportIntegrationDocument, rapportFile, env, "IDOC", false);
						resultIntegration.setIdDocHistorisation(idEddmRapport);
					} catch (Exception e) {
						LOGGER.error("Problème lors de la sauvegarde en GED du rapport d'intégration", e);
					}
					finally {
						if (rapportFileOutputStream != null) {
							rapportFileOutputStream.close();
						}
					}

				} else {
					LOGGER.error("Problème lors de la génération du rapport d'intégration : rapportOutputStream null");
				}
				LOGGER.info("Fin de préparation du rapport d'intégration");

				if (uploadMassifResult.getRapportIntegration() != null) {
					resultIntegration.setIdDocHistorisation(idEddmRapport);
				} else {
					resultIntegration.setIdDocHistorisation("Problème lors de la génération du rapport d'intégration : échec de la génération");
				}
				resultIntegration.setDateUpload(new Date());
				resultIntegration.setUploadUser(user);
				resultIntegration.setZipName(zipName);
				LOGGER.debug("Suppression du repertoire d'extraction");
				FileUtils.forceDelete(tempZipDir);
				LOGGER.debug("Suppression OK");
			} catch (Exception e1) {
				// Si c'est une exception qu'on connait on la laisse se propager telle quelle
				if ((e1 instanceof GedeCommonException)) {
					throw (GedeCommonException) e1;
				}
				LOGGER.error("Problème innatendu lors de l'intégration en masse de document", e1);
				throw new GedeCommonException("Erreur technique lors de l'intégration des documents : " + e1.getMessage(), e1);
			} finally {
				removeMongoIntegEnCours(env, cmroc, idDocIntegEnCours);
				SEMAPHORE_UPLOAD_FILE.release();
			}
		} else {
			throw new GedeCommonException("Un import est déjà en cours. Merci de réessayer plus tard.");
		}
		return resultIntegration;
	}

	/**
	 * 
	 * @param document
	 * @param fileToSave
	 * @param env
	 * @param demandeur
	 * @return eddocId
	 * @throws GedeException
	 */
	private String integUnitaire(Document document, File fileToSave, String env, String demandeur, boolean allowOnlyIDOCDocument)
			throws GedeException {
		String environnement = EnvironnementHelper.determinerEnvironnement(env);
		// fichier sur le disque
		DocumentHelper.saveFile(document, fileToSave);
		String state = "file_saved";

		String eddocId;
		try {
			// ref fonctionnel
			eddocId = creatFunctional(document, environnement, demandeur);
			state = "file_ref";

			// ref edt
			create(environnement, document, false, allowOnlyIDOCDocument);

			//rollBack
		} catch (Exception e) {
			// rollback fichier sur disque
			try {
				DocumentHelper.deleteFile(document);
			} catch (Exception e1) {
				throw new GedeCommonException("RollBack integUnitaire, fichier orphelin.", e1);
			}
			if (state.equals("file_ref")) {
				// rollback ref fonctionnel
				try {
					deleteFunctional(document, environnement);
				} catch (Exception e2) {
					throw new GedeCommonException("RollBack integUnitaire, document orphelin.", e2);
				}
			}
			throw new GedeCommonException("RollBack integUnitaire.", e);
		}

		return eddocId;
	}

	/**
	 * crée un document en BDD fontionnelle (Starweb/mongo) et valorise l'idstar/tsstar
	 * @param document
	 * @param envir
	 * @param demandeur
	 * @return
	 * @throws GedeException
	 */
	private String creatFunctional(Document document, String envir, String demandeur) throws GedeException {
		TypeEntite typeEntite = document.getDocMongo().getTypeEntiteRattachement();

		switch (typeEntite) {
		case PERSONNE:
		case ENTREPRISE:
		case SECTION:
		case PARTENAIRE:
			metier.getEddmManager().create(document, metier, envir, demandeur);
			break;
		case SUDE:
			metier.getEddmManager().createNLinkToSude(document, envir, metier, demandeur);
			break;
		case PACK:
		case GARANTIE:
		case RAPPORT_INTEGRATION_IDOC:
			GedeIdHelper.setNewIdstarTstar(document);
			documentMongoService.insertMongo(envir, document.getCmroc(), document, demandeur);
			break;
		default:
			throw new GedeCommonException("Type de dossier non supporté");
		}
		return GedeIdHelper.getEddocId(document);
	}

	private void deleteFunctional(Document document, String envir)
			throws CimutMetierException, CimutConfException, GedeCommonException, CimutDocumentException {

		switch (document.getDocMongo().getTypeEntiteRattachement()) {
		case PERSONNE:
		case ENTREPRISE:
		case SECTION:
		case PARTENAIRE:
		case SUDE:
			metier.getEddmManager().remove(GedeIdHelper.getEddocId(document), document.getCmroc(), envir);
			break;
		case PACK:
		case GARANTIE:
		case RAPPORT_INTEGRATION_IDOC:
			documentMongoService.removeMongo(envir, document.getCmroc(), document.getId());
			break;
		default:
			throw new GedeCommonException("Type de dossier non supporté");
		}
	}

	public List<HistoriqueUploadIdoc> listHistoIdocByCmroc(String cmroc, String envir) throws GedeException {
		// Petit flush pour nettoyer la liste en cas de pépin ayant eu lieu pendant une intégration
		flushMongoIntegEnCours(envir, cmroc);
		List<HistoriqueUploadIdoc> result = new ArrayList<HistoriqueUploadIdoc>();
		if (StringUtils.isNotBlank(cmroc)) {
			// Recherche des documents en base Mongo, seulement les rapport d'intégration terminées
			Collection<DocumentMongo> listDocMongo = listDocumentMongoRapportIntegration(cmroc, envir, false);
			for (DocumentMongo docMongo : listDocMongo) {
				HistoriqueUploadIdoc histoUpload = new HistoriqueUploadIdoc();
				histoUpload.setDateUpload(docMongo.getDtIntegration().toDate());
				histoUpload.setIdDocHistorisation(docMongo.getEddocId());
				histoUpload.setIntegrationKO(docMongo.getIntegrationsKO());
				histoUpload.setIntegrationOK(docMongo.getIntegrationsOK());
				histoUpload.setIntegrationTotal(docMongo.getIntegrationsTotale());
				histoUpload.setUploadUser(docMongo.getUser());
				histoUpload.setZipName(docMongo.getZipName());
				result.add(histoUpload);
			}
			// Nouvelle recherche QUE sur les intégration en cours. Ces flags doivent être ajouté en fin de liste
			// sinon l'IHM ne comprendra pas qu'une intégration est en cours lors de cet appel
			listDocMongo = listDocumentMongoRapportIntegration(cmroc, envir, true);
			for (DocumentMongo docMongo : listDocMongo) {
				HistoriqueUploadIdoc histoUpload = new HistoriqueUploadIdoc();
				histoUpload.setDateUpload(docMongo.getDtIntegration().toDate());
				histoUpload.setIdDocHistorisation(docMongo.getEddocId());
				histoUpload.setIntegrationKO(docMongo.getIntegrationsKO());
				histoUpload.setIntegrationOK(docMongo.getIntegrationsOK());
				histoUpload.setIntegrationTotal(docMongo.getIntegrationsTotale());
				histoUpload.setUploadUser(docMongo.getUser());
				histoUpload.setZipName(docMongo.getZipName());
				result.add(histoUpload);
			}
		}
		return result;
	}

	public Collection<DocumentMongo> listDocumentMongoRapportIntegration(String cmroc, String envir, boolean getTagEnCours) throws GedeException {
		String environnement = EnvironnementHelper.determinerEnvironnement(envir);
		Manager<DocumentMongo> managerDoc = MongoManagerFactory.getDocumentManager(environnement, cmroc, documentMongoService.getMongoClient());
		BasicDBObject query = new BasicDBObject();

		// La nullité du CMROC est controlée en amont de l'appel
		if (StringUtils.isBlank(cmroc)) {
			return new ArrayList<DocumentMongo>();
		}
		query.append(GlobalVariable.ATTR_ID_ORGANISME, cmroc);
		query.append(GlobalVariable.ATTR_TYPE_COURRIER, TypeEntite.RAPPORT_INTEGRATION_IDOC.getText());
		if (getTagEnCours) {
			query.append(GlobalVariable.ATTR_EDDOC_ID, MSG_INTEGRATION_EN_COURS);
		} else {
			query.append(GlobalVariable.ATTR_EDDOC_ID, new BasicDBObject("$ne", MSG_INTEGRATION_EN_COURS));
		}
		return managerDoc.list(query);
	}

	public void testAddNotSude(String envir, String cmroc, String user, Document document)
			throws CimutMetierException, CimutConfException, CimutFileException, GedeCommonException, CimutDocumentException {
		metier.getSudeManager().addNoteAndPj(envir, cmroc, "IDOC - " + user, document);
	}

	private void flushMongoIntegEnCours(String env, String cmroc) {
		List<String> idDocIntegEnCoursToRemoveList = new ArrayList<String>();
		if (idDocIntegEnCoursList.size() > 0) {
			for (String idDoc : idDocIntegEnCoursList) {
				idDocIntegEnCoursToRemoveList.add(removeMongoIntegEnCours(env, cmroc, idDoc));
			}
			for (String idDocToRemove : idDocIntegEnCoursToRemoveList) {
				idDocIntegEnCoursList.remove(idDocToRemove);
			}
		}
	}

	private void flushAndInsertMongoIntegEnCours(String env, String cmroc, String user, String zipName, String idDocIntegEnCours) {
		// D'abord on flush les éventuel flags non supprimés si ils existent
		flushMongoIntegEnCours(env, cmroc);
		DocumentMongo docMongo = new DocumentMongo();
		docMongo.setCmroc(cmroc);
		docMongo.setUser(user);
		docMongo.setDtCreate(new DateTime());
		docMongo.setDtIntegration(new DateTime());
		docMongo.setZipName(zipName);
		// On marque l'intération en cours comme un rapport pour qu'elle soit ramassé par l'endpoint qui liste les anciennes intégrations
		docMongo.setTypeEntiteRattachement(TypeEntite.RAPPORT_INTEGRATION_IDOC);
		docMongo.setEddocId(MSG_INTEGRATION_EN_COURS);
		docMongo.setId(idDocIntegEnCours);
		try {
			String environnement = EnvironnementHelper.determinerEnvironnement(env);
			documentMongoService.insertMongo(environnement, cmroc, docMongo);
			// Si OK, on ajoute l'ID à la liste des ID en cours (normalement 0 ou 1 élément)
			idDocIntegEnCoursList.add(idDocIntegEnCours);
		} catch (Exception e) {
			LOGGER.error("Problème lors du flag 'IntégrationIDOCEnCours' dans Mongo pour " + cmroc, e);
		}
	}

	private void updateMongoIntegEnCours(String env, String cmroc, HistoriqueUploadIdoc histo, String idDocIntegEnCours) {
		DocumentMongo integEnCours = getIntegrationIDOCEnCours(env, cmroc, idDocIntegEnCours);
		if (integEnCours != null) {
			integEnCours.setIntegrationsTotale(histo.getIntegrationTotal());
			integEnCours.setIntegrationsOK(histo.getIntegrationOK());
			integEnCours.setIntegrationsKO(histo.getIntegrationKO());
			try {
				String environnement = EnvironnementHelper.determinerEnvironnement(env);
				Manager<DocumentMongo> manager = MongoManagerFactory.getDocumentManager(environnement, cmroc, documentMongoService.getMongoClient());
				manager.update(integEnCours);
			} catch (Exception e) {
				LOGGER.error("Problème lors de la mise à jour du flag 'IntégrationIDOCEnCours' dans Mongo pour " + cmroc, e);
			}
		}
	}

	/**
	 * 
	 * @param env
	 * @param cmroc
	 * @param idDocIntegEnCours
	 * @return l'Id du doc supprimé, la chaîne vide en cas d'échec
	 */
	private String removeMongoIntegEnCours(String env, String cmroc, String idDocIntegEnCours) {
		try {
			DocumentMongo integEnCours = getIntegrationIDOCEnCours(env, cmroc, idDocIntegEnCours);
			String environnement = EnvironnementHelper.determinerEnvironnement(env);
			Manager<DocumentMongo> managerDoc = MongoManagerFactory.getDocumentManager(environnement, cmroc, documentMongoService.getMongoClient());
			if (integEnCours != null) {
				managerDoc.remove(integEnCours.getId());
				return idDocIntegEnCours;
			}
		} catch (Exception e) {
			LOGGER.error("Problème lors de la suppression du flag 'IntégrationIDOCEnCours' dans Mongo pour " + cmroc, e);
		}
		return "";
	}

	private DocumentMongo getIntegrationIDOCEnCours(String env, String cmroc, String idDocIntegEnCours) {
		try {
			if (StringUtils.isNotBlank(cmroc)) {
				String environnement = EnvironnementHelper.determinerEnvironnement(env);
				Manager<DocumentMongo> managerDoc = MongoManagerFactory.getDocumentManager(environnement, cmroc, documentMongoService.getMongoClient());
				return managerDoc.get(idDocIntegEnCours);
			}
		} catch (Exception e) {
			LOGGER.error("Problème lors de la récupération du flag 'IntégrationIDOCEnCours' dans Mongo pour " + cmroc, e);
		}
		return null;
	}

	public void integUnitaire(UploadFileRequest integRequest, String env, String cmroc, String user, String filename)
			throws GedeCommonException, CimutFileException {
		LOGGER.info("Début de l'intégration unitaire");
		// Contrôle du type de fichier
		if (!FileHelper.getExtension(filename).equals(".pdf")) {
			throw new GedeCommonException("Le document " + filename + " n'est pas un fichier pdf");
		}
		// On trim le nom du fichier, IE ramène tout le chemin du fichier i.e. C:\....\fichier.pdf
		filename = FileHelper.trimBackSlashs(filename);
		File docFile = null;
		try {
			LOGGER.info("Sauvegarde du fichier reçu dans le reportoire temp");
			// Sauvegarde du fichier en disque obligatoire
			docFile = new File(File.separator + "tmp" + File.separator + UUID.randomUUID().toString() + "_" + filename);
			FileUtils.writeByteArrayToFile(docFile, integRequest.getFileData());
			// Conversion de l'idTypeDocument reçu vers codeTypeDocument i.e. 45772 --> TABLEAU_GARANTIE
			String typeDoc = null;
			try {
				Type type = daoType.get(Long.parseLong(integRequest.getTypeDoc()));
				if (null == type) {
					throw new NotFoundException("Type non trouvé: "+ integRequest.getTypeDoc());
				}
				typeDoc = type.getCode();
			} catch (Exception e) {
				LOGGER.error("Problème lors de la récupération du typeDocument d'id " + integRequest.getTypeDoc(), e);
				throw new GedeCommonException("Problème lors de la récupération du type document d'id " + integRequest.getTypeDoc(), e);
			}

			LOGGER.info("Préparation du document pour " + filename);
			Document document = DocumentHelper.prepareDocIntegUnitaire(env, cmroc, user, integRequest, filename, typeDoc);

			// Contrôle de la taille par page du fichier
			PDDocument doc = null;
			try {
				doc = PDDocument.load(docFile);
			} catch (Exception e) {
				throw new GedeCommonException("Problème de lecture du fichier " + filename);
			}
			document.setNbpage(doc.getNumberOfPages());
			if (document.getNbpage() == 0) {
				document.setNbpage(1);
			}
			doc.close();
			int tailleMax = GlobalVariable.getTailleMaxFichierIdoc();
			if ((docFile.length() / document.getNbpage()) > tailleMax) {
				throw new GedeCommonException("Le document " + filename + " à une taille supérieure à " + tailleMax / 1024 + "ko par page");
			}

			integUnitaire(document, docFile, env, user, true);
			LOGGER.info("Fin de l'intégration unitaire pour le fichier " + filename);
		} catch (Exception e1) {
			// Si c'est une exception qu'on connait on la laisse se propager telle quelle
			if ((e1 instanceof GedeCommonException)) {
				throw (GedeCommonException) e1;
			}
			try {
				// En cas de problème on essaie de supprimer le fichier temporaire
				FileUtils.forceDelete(docFile);
			} catch (IOException e2) {
				LOGGER.error("Impossible de supprimer le fichier temporaire " + docFile);
			}
			LOGGER.error("Problème innatendu lors de l'intégration du document " + filename, e1);
			throw new GedeCommonException("Erreur technique lors de l'intégration du document " + filename + " : " + e1.getMessage(), e1);
		}
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void reconciliationFkType(String env, String cmroc, String user) {
		try {
			LOGGER.warn("Début de la réconciliation des foreign key document -> type (Exécutée par "+user+")");

			int page = 1;
			int batchSize = 500000; // 500K
			int maxOffset =  1000000000; // Eviter une boucle sans fin (ne doit jamais arriver)
			boolean dataAvailable = true;

			while (page * batchSize < maxOffset && dataAvailable) {

				dataAvailable = TechnicalRestDao.reconciliationDocumentTypeFk(daoDocEdt, cmroc, env, page, batchSize);
				page++;
			}
		} catch (Exception e) {
			LOGGER.error("Problème lors de la réconciliation des foreign keys document -> type", e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
	public void integFileSystem(String env, String cmroc, String user) {
		FileOutputStream rapportFileOutputStream = null;
		try {
			LOGGER.warn("Début de l'intégration massive via file system");
			// Repertoire d'input qui simule un repertoire d'exttraction de zip
			String inputFolderPath = GlobalVariable.getIdocFileSystemInputDirectory();
			Integer chunkSize = GlobalVariable.getIdocFileSystemInputChunkSize();
			Integer coolDown = GlobalVariable.getIdocFileSystemInputCoolDownTime();
			UploadMassifResult uploadMassifResult = DocumentHelper.extractDocumentsFromExtractedZip(inputFolderPath, env, cmroc, daoType, false,
					false);
			// On a forcément un rapport d'intégration ici, il correspond au fichier d'index original. Une exception est lancée sinon
			Map<DocumentUploadMassifWrapper, File> documentToUploadMap = uploadMassifResult.getDocumentToUploadMap();
			List<File> fileOkList = new ArrayList<File>();
			List<File> fileKoList = new ArrayList<File>();
			int i = 1;
			for (Entry<DocumentUploadMassifWrapper, File> entry : documentToUploadMap.entrySet()) {
				try {
					LOGGER.info("Début d'intégration unitaire #" + i);
					integUnitaire(entry.getKey().getDocument(), entry.getValue(), env, user, true);
					fileOkList.add(entry.getValue());
					LOGGER.info("Fin d'intégration unitaire #" + i + " : OK");
				} catch (Exception e) {
					LOGGER.error("Problème lors d'une intégration unitaire pour l'upload massif #" + i + " : KO", e);
					UploadStatut uploadStatut = new UploadStatut();
					uploadStatut.setOk(false);
					// On récupère le message d'erreur en enlenvant la classe de l'exception lancée originellement
					String errorMessage = e.toString();
					if (errorMessage.contains(":")) {
						errorMessage = errorMessage.split(":")[1];
						if (e.toString().contains("CimutMetierException")) {
							errorMessage = "Problème dans l'appel au MOS : " + errorMessage;
						}
					}
					uploadStatut.setErrorMessage("Problème technique lors de la sauvegarde en GED : " + errorMessage);
					uploadMassifResult.getUploadStatutMap().put(entry.getKey().getRowNum(), uploadStatut);
					fileKoList.add(entry.getValue());
				}
				if (i % 50 == 0) {
					LOGGER.warn("Integration massive via file System : " + i + "/" + documentToUploadMap.size());
				}
				// On laisse la gede respirer, avec des pauses entre 2 blocs d'appel
				if (i % chunkSize == 0) {
					Thread.sleep(coolDown * 1000);
				}
				i++;
			}
			// Préparation du rapport d'intégration et archivage
			LOGGER.info("Préparation et sauvegarde du rapport d'intégration");
			ByteArrayOutputStream rapportOutputStream = DocumentHelper.prepareRapportIntegration(uploadMassifResult.getRapportIntegration(),
					uploadMassifResult);
			if (rapportOutputStream != null) {
				File rapportFile = new File(inputFolderPath + File.separator + "rapport" + File.separator + "rapportIntegrationFileSystem.xlsx");
				rapportFile.getParentFile().mkdirs();
				rapportFile.createNewFile();
				rapportFileOutputStream = new FileOutputStream(rapportFile);
				rapportFileOutputStream.write(rapportOutputStream.toByteArray());
				rapportFileOutputStream.flush();
			} else {
				LOGGER.error("Problème lors de la génération du rapport d'intégration : rapportOutputStream null");
			}
			LOGGER.info("Fin de préparation du rapport d'intégration");

			LOGGER.debug("Début du déplacement des fichiers traités");
			for (File fileOk : fileOkList) {
				FileUtils.copyFile(fileOk, new File(fileOk.getParent() + File.separator + "OK" + File.separator + fileOk.getName()));
				fileOk.delete();
			}
			for (File fileKo : fileKoList) {
				FileUtils.copyFile(fileKo, new File(fileKo.getParent() + File.separator + "KO" + File.separator + fileKo.getName()));
				fileKo.delete();
			}
			LOGGER.debug("Déplacement OK");
		} catch (Exception e1) {
			// Si c'est une exception qu'on connait on la laisse se propager telle quelle
			LOGGER.error("Problème innatendu lors de l'intégration en masse de document via file system", e1);
		}
		finally {
			if (rapportFileOutputStream != null) {
				try {
					rapportFileOutputStream.close();
				} catch (IOException e) {
					LOGGER.error("Erreur lors dans la fermeture du flux", e);
				}
			}
		}
		LOGGER.warn("Fin de l'intégration massive via file system");
	}

	public String deleteList(String documentIds, String env, String cmroc) {
		String retour = "";
		List<String> documentIdsList = new ArrayList<String>(Arrays.asList(documentIds.split(",")));
		for (String id : documentIdsList) {
			try {
				delete(id, env, cmroc);
			} catch (Exception e) {
				retour = retour + "Problème lors de la suppression du document " + id + ";";
			}
		}
		return retour;
	}

	/**
	 * Suppression d'un document importé via IDOC : - Suppression en base éditique, - Suppression Mongo - Suppression
	 * physique sur le disque
	 * 
	 * @param eddocId
	 * @param env
	 * @param cmroc
	 * @throws GedeException
	 */
	public void delete(String eddocId, String env, String cmroc) throws GedeException {
		Document document = null;
		// Récupération du document
		try {
			document = getByEddocId(eddocId);
		} catch (Exception e) {
			String errorMsg = "Document d'eddocId " + eddocId + " introuvable";
			LOGGER.error(errorMsg, e);
			throw new CimutDocumentException(errorMsg);
		}
		// Suppression en base editique. En cas d'erreur, on rollback et on ne fait rien de plus
		try {
			daoDocEdt.deleteByEddocId(eddocId);
		} catch (Exception e) {
			LOGGER.error("Problème lors de la suppression en BDD du document " + document.getLibelle() + " - " + eddocId, e);
			throw new CimutDocumentException("Problème lors de la suppression du document " + document.getLibelle() + " - " + eddocId);
		}
		// Suppression en base Starweb
		try {
			callDeleteDocumentsStarwebDao(eddocId, cmroc, env);
		} catch (Exception e) {
			LOGGER.error("Problème lors de la suppression en BDD du document via StarwebDao " + document.getLibelle() + " - " + eddocId, e);
			throw new CimutDocumentException("Problème lors de la suppression du document via StarwebDao " + document.getLibelle() + " - " + eddocId);
		}
		// Pour les Pack, Garantie et Rapport d'intégration on à aussi de la donnée à supprimer dans Mongo
		try {
			Manager<DocumentMongo> manager = MongoManagerFactory.getDocumentManager(env, cmroc, documentMongoService.getMongoClient());
			manager.remove(document.getId());
		} catch (Exception e) {
			// Document non existant dans Mongo, on ne fait rien
		}
		// Suppression Physique
		File file = new File(DocumentHelper.getPlanDeClassement(document), document.getId());
		if (!file.exists()) {
			LOGGER.error("Impossible de supprimer le document suivant : " + file.getAbsolutePath() + " - fichier inexistant");
			throw new CimutFileException(
					"Impossible de supprimer le document " + eddocId + " - " + document.getId() + " sur le disque - fichier inexistant");
		}
		if (!(file.canWrite() && file.delete())) {
			LOGGER.error("Impossible de supprimer le document suivant : " + file.getAbsolutePath() + " - fichier existant bloqué");
			throw new CimutFileException("Impossible de supprimer le document " + eddocId + " - " + document.getId() + " sur le disque");
		}
	}
	
	/**
	 * Mise à jour d'un document importé via IDOC : mise à jours du libelle
	 * physique sur le disque
	 * 
	 * @param eddocId
	 * @param env
	 * @param cmroc
	 * @throws GedeException
	 */
	public void updateInfoDocumentStarwebDao(String eddocId, String libelle, String env, String cmroc) throws GedeException {
		try {
			callUpdateDocumentInfoStarwebDao(eddocId, libelle, cmroc, env);
		} catch (Exception e) {
			LOGGER.error("Problème lors de la mise à jour libelle en BDD du document via StarwebDao " + eddocId, e);
			throw new CimutDocumentException("Problème lors de la mise à jour libelle en BDD du document via StarwebDao " + eddocId);
		}
	}

	/**
	 * Méthode permettant de remplacer le fichier physique d'un document existant. On modifie donc seulement le fichier
	 * mais pas les métadonnées sauf la date de MAJ
	 * 
	 * @param data
	 * @param eddocId
	 * @param env
	 * @param cmroc
	 * @throws CimutDocumentException
	 * @throws CimutConfException
	 * @throws CimutFileException
	 * @throws IOException
	 */
	public void replaceFile(byte[] data, String eddocId, String env, String cmroc)
			throws CimutDocumentException, CimutFileException, CimutConfException, IOException {
		Document document = null;
		// Récupération de l'ancien document
		try {
			document = getByEddocId(eddocId);
		} catch (Exception e) {
			LOGGER.error("Document d'eddocId " + eddocId + " introuvable", e);
			throw new CimutDocumentException("Document d'eddocId " + eddocId + " introuvable");
		}
		File file = new File(DocumentHelper.getPlanDeClassement(document), document.getId());
		if (file.exists()) {
			if (file.canWrite()) {
				FileUtils.writeByteArrayToFile(file, data);
			} else {
				LOGGER.error("Impossible de modifier le fichier suivant : " + file.getAbsolutePath() + " - fichier non modifiable");
				throw new CimutFileException("Impossible de modifier le fichier suivant : " + file.getAbsolutePath() + " - fichier non modifiable");
			}
		} else {
			LOGGER.error("Impossible de modifier le fichier suivant : " + file.getAbsolutePath() + " - fichier inexistant");
			throw new CimutFileException("Impossible de modifier le fichier suivant : " + file.getAbsolutePath() + " - fichier inexistant");
		}
	}

}
