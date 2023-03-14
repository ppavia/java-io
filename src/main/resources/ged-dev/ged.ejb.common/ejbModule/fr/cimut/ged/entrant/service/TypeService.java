package fr.cimut.ged.entrant.service;

import fr.cimut.ged.entrant.beans.db.Categorie;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.dao.AssiaClaims;
import fr.cimut.ged.entrant.dao.TypeDao;
import fr.cimut.ged.entrant.dao.starwebdao.TypeRestDao;
import fr.cimut.ged.entrant.dto.*;
import fr.cimut.ged.entrant.exceptions.*;
import fr.cimut.ged.entrant.mapper.TypeDtoMapper;
import fr.cimut.ged.entrant.utils.CommonValidator;
import fr.cimut.ged.entrant.utils.GedeUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.UnauthorizedException;
import org.mapstruct.factory.Mappers;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;
import java.util.List;

import static fr.cimut.ged.entrant.utils.EnvironnementHelper.determinerEnvironnement;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * service de gestion des types
 */
@Stateless(mappedName = "Type")
//@Interceptors({ DebugServiceInterceptor.class })
public class TypeService {

	private static final Logger LOGGER = Logger.getLogger(TypeService.class);
	public static final String MSG_ERR_CANNOT_CREATE_TYPE_WITHOUT_CATEGORY = "Impossible de créer un type sans catégorie";

	@EJB
	TypeDao daoType;

	@EJB
	TypeRestDao typeRestDao;

	@EJB
	CategorieService categorieService;

	@EJB
	DocumentService documentService;

	/** READ QUERIES (DELEGATED TO STARWEB DAO) **/

	public TypeDto getTypeByCode(String code, String cmroc, String envdir) throws GedeException {
		// Permettre d'obtenir aussi les paramètres OC dans le type si un OC est spécifié (cmroc)
		AssiaClaims claims = AssiaClaims.forTenantOrReferential(cmroc, determinerEnvironnement(envdir));

		// Lecture StarwebDAO
		List<TypeDto> list = typeRestDao.listTypeByCriteria(claims, null, code);
		return GedeUtils.getUniqueInListIfExists(list);
	}

	public List<TypeDto> list(Long idCategorie, String cmroc, String envdir) throws CimutConfException {
		// Permettre d'obtenir aussi les paramètres OC dans le type si un OC est spécifié (cmroc)
		AssiaClaims claims = AssiaClaims.forTenantOrReferential(cmroc, determinerEnvironnement(envdir));

		if (null == idCategorie) {
			return typeRestDao.listAll(claims);
		} else {
			return typeRestDao.listTypeByCriteria(claims, idCategorie, null);
		}
	}

	public List<TypeDto> listAll(String cmroc, String envdir) throws CimutConfException {
		// Permettre d'obtenir aussi les paramètres OC dans le type si un OC est spécifié (cmroc)
		AssiaClaims claims = AssiaClaims.forTenantOrReferential(cmroc, determinerEnvironnement(envdir));
		return typeRestDao.listAll(claims);
	}

	public List<TypeDto> search(String cmroc, String envdir, TypeFilters filters) throws CimutConfException {
		// Permettre d'obtenir aussi les paramètres OC dans le type si un OC est spécifié (cmroc)
		AssiaClaims claims = AssiaClaims.forTenantOrReferential(cmroc, determinerEnvironnement(envdir));
		return typeRestDao.search(claims, filters);
	}

	public List<TypeDto> listByPageAndSize(String cmroc, String envdir, Integer page , Integer size, String code,
			String type) throws CimutConfException {
		// Permettre d'obtenir aussi les paramètres OC dans le type si un OC est spécifié (cmroc)
		AssiaClaims claims = AssiaClaims.forTenantOrReferential(cmroc, determinerEnvironnement(envdir));

		if (claims.isReferentialOnly()) {
			// TODO: Tester si la synchro est suffisament efficace pour passer également par DAO
			return daoType.listAllByPageAndSize(page, size, code, type);
		} else {
			return typeRestDao.listAllByPageAndSize(claims, page, size, code, type);
		}
	}

	public TypeParamsDto getOrganismTypeParametersByTypeId(String cmroc, String envir, Long typeId)
			throws GedeException {
		// Pour le moment cette endpoint ne peut être appelé que pour un tenant
		AssiaClaims claims = AssiaClaims.forTenantOnly(cmroc, determinerEnvironnement(envir));
		return typeRestDao.getOrganismTypeParametersByTypeId(claims, typeId);
	}

	/** WRITE QUERIES (EDITIC DATABASE ACCESS) **/

	public TypeDto create(Long idCategorie, Type typeDto, String cmroc) throws GedeException {
		//  verification des champs et verification unicité code
		checkValid(typeDto);

		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);

		// recuperation de la categorie
		Categorie categoriePersisted = categorieService.getByIdAndCheckNotNull(idCategorie);

		typeDto.setDateMaj(new Date());
		Type typePersisted = daoType.create(typeDto);

		typePersisted.setCategorie(categoriePersisted);
		return toTypeDto(typePersisted);
	}

	/**
	 * Création d'un type sans avoir à spécifier toute les informations
	 */
	public TypeDto createWithDefaults(String cmroc, TypeModificationDto data, String user) throws GedeException {
		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);

		String cdType = data.getCode();
		String typeLibelle = data.getLibelle();
		Boolean integrableIdoc = data.getIntegrableIdoc();
		Integer dureePurge = data.getDureePurge();
		TypeModificationCategoryDto categoryDto = data.getCategorie();

		//  verification l'unicité code
		checkCodeExistant(cdType);

		Type type  = new Type();

		// si la catégorie est renseignée
		if (categoryDto != null && !isBlank(categoryDto.getCode())) {

			// sauvegarder le lien vers la catégorie (si trouvé)
			for (final Categorie category : categorieService.list()) {
				if (category.getCode() != null && category.getCode().equalsIgnoreCase(categoryDto.getCode())) {
					type.setCategorie(category);
					break;
				}
			}
		}

		if (type.getCategorie() == null) {
			throw new RuntimeGedeException(MSG_ERR_CANNOT_CREATE_TYPE_WITHOUT_CATEGORY);
		}

		if(cdType != null) {
			type.setCode(cdType);
		}
		if(typeLibelle != null) {
			type.setLibelle(typeLibelle);
		}
		if(integrableIdoc != null) {
			type.setIntegrableIdoc(integrableIdoc);
		}
		if(dureePurge != null) {
			type.setDureePurge(dureePurge);
		}

		type.setUserMaj(user);
		type.setDureeArchivage(30);
		type.setDureeVisibleExtranet(3650);
		type.setDateMaj(new Date());

		try {
			daoType.create(type);
		} catch (Exception e) {
			throw new CimutTypeException("Impossible de créer le type", e);
		}

		return toTypeDto(type);
	}
	
	public TypeDto update(Type type, String cmroc) throws GedeException {
		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);

		//  verification des champs et verification unicité code
		checkValid(type);

		// faut-il mettre à jour le type de document sur tous les dcuments liés à ce code
		// pour propager l'information:
		// pour l'instant non, on va meme interdire à changer ce code technique (cela afin de permettre aussi a toutes les applciations
		// eternes de continuer a fonctionner sans connaitre l'entité type

		Type typePersisted = getByIdAndCheckNotNull(type.getId());
		if (!type.getCode().equals(typePersisted.getCode()) && documentService.existsDocByIdType(type.getId())) {
			throw new BadRequestException("la modification du champ code est interdite car des documents sont déjà liés à ce type");
		}
		type.setDateMaj(new Date());

		return toTypeDto(daoType.update(type));
	}

	public TypeDto partialUpdate(Long idType, String cmroc, TypeModificationDto data, String user) throws GedeException {
		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);

		String cdType = data.getCode();
		String typeLibelle = data.getLibelle();
		Boolean integrableIdoc = data.getIntegrableIdoc();
		Integer dureePurge = data.getDureePurge();
		Type type ;


		type = getByIdAndCheckNotNull(idType);
		String initilaCdType = type.getCode();
		// verification du code type inséré
		if(!initilaCdType.equals(cdType)) {
			//  verification l'unicité code
			checkCodeExistant(cdType);
		}

		if(cdType != null) {
			type.setCode(cdType);
		}
		if(typeLibelle != null) {
			type.setLibelle(typeLibelle);
		}
		if(integrableIdoc != null) {
			type.setIntegrableIdoc(integrableIdoc);
		}
		if(dureePurge != null) {
			type.setDureePurge(dureePurge);
		}

		type.setUserMaj(user);
		type.setDateMaj(new Date());

		try {
			daoType.update(type);
		} catch (Exception e) {
			throw new CimutTypeException("Impossible de mettre à jour le type", e);
		}

		return toTypeDto(type);
	}

	public void delete(Long idType, String cmroc) throws GedeException {

		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);

		// on ne peut pas supprimer un type si des documents sont liés dessous
		if (documentService.existsDocByIdType(idType)) {
			throw new UnauthorizedException("suppression impossible du type, il existe des documents rattachés à ce type");
		}
		daoType.delete(idType);
	}

	public TypeParamsDto saveOrganismTypeParameters(String cmroc, String envir, String user, Long typeId, TypeParamsModificationDto typeParamsModificationDto)
			throws GedeException {
		AssiaClaims claims = AssiaClaims.forTenantOnly(cmroc, envir);
		return typeRestDao.saveOrganismTypeParameters(claims, user, typeId, typeParamsModificationDto);
	}


	/** PRIVATE UTILITY METHODS **/

	private void checkValid(Type typeDto) throws GedeException {
		// verification des attributs
		CommonValidator.assertStringNotBlank(typeDto.getCode(), "code");
		CommonValidator.assertStringNotBlank(typeDto.getLibelle(), "libelle");
		CommonValidator.assertObjectNotNull(typeDto.getDureeArchivage(), "dureeArchivage");
		CommonValidator.assertObjectNotNull(typeDto.getDureePurge(), "dureePurge");
		CommonValidator.assertObjectNotNull(typeDto.getDureeVisibleExtranet(), "dureeVisibleExtranet");
		CommonValidator.assertStringNotBlank(typeDto.getUserMaj(), "userMaj");

		// verification de l'unicité du code
		List<Type> listTypes = daoType.listTypeByCriteria(null, typeDto.getCode());
		if (CollectionUtils.isNotEmpty(listTypes)) {
			// s'il existe une correspondances de code parmis les typs alors c'est ko
			for (Type typePersisted : listTypes) {
				if (typePersisted.getId() != typeDto.getId() && typeDto.getCode().equals(typePersisted.getCode())) {
					throw new BadRequestException("il existe déjà un type avec ce code");
				}
			}
		}
	}

	private TypeDto toTypeDto(Type type) {
		TypeDtoMapper mapper = Mappers.getMapper(TypeDtoMapper.class);

		return mapper.typeToTypeDto(type);
	}

	private void checkCodeExistant(String cdType) throws GedeException {
		if(StringUtils.isNotBlank(cdType)) {
			// verification de l'unicité du code
			Long countType = daoType.countTypeWithCode(cdType);
			if (countType > 0) {
				throw new ConflictException("il existe déjà un type avec ce code");
			}
		}
		
	}

	private void checkEntityNotNull(Object entity) throws GedeException {
		if (null == entity) {
			throw new NotFoundException("objet non trouvé");
		}
	}

	private Type getByIdAndCheckNotNull(Long id) throws GedeException {
		Type entity= daoType.get(id);
		checkEntityNotNull(entity);
		return entity;
	}


	private void checkCmrocCimut(String cmroc) {
		if (!"0000".equals(cmroc)) {
			throw new BadRequestException("la modification de type de document n'est réalisable que par les administrateurs cimut");
		}
	}
}
