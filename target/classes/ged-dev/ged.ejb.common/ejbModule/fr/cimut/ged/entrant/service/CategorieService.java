package fr.cimut.ged.entrant.service;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import fr.cimut.ged.entrant.dao.TypeDao;
import org.apache.commons.collections4.CollectionUtils;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.UnauthorizedException;

import fr.cimut.ged.entrant.beans.db.Categorie;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.dao.AbstractDao;
import fr.cimut.ged.entrant.dao.CategorieDao;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.utils.CommonValidator;

/**
 * service de gestion des categories
 * 
 * @author jlebourgocq
 *
 */
@Stateless(mappedName = "Categorie")
//@Interceptors({ DebugServiceInterceptor.class })
public class CategorieService extends AbstractService<Categorie> {

	@EJB
	CategorieDao daoCategorie;

	@EJB
	TypeDao typeDao;

	/**
	 * 
	 * @param code
	 * @return
	 */
	public Categorie create(Categorie categorieDto, String cmroc) throws GedeException {
		checkValid(categorieDto);
		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);
		categorieDto.setDateMaj(new Date());
		return daoCategorie.create(categorieDto);

	}

	/**
	 * verification du contenu de la categorie
	 * 
	 * @param categorieDto
	 */
	private void checkValid(Categorie categorieDto) throws GedeException {
		// verification des attributs
		CommonValidator.assertStringNotBlank(categorieDto.getCode(), "code");
		CommonValidator.assertStringNotBlank(categorieDto.getLibelle(), "libelle");
		CommonValidator.assertStringNotBlank(categorieDto.getUserMaj(), "userMaj");

		// verification de l'unicité du code
		List<Categorie> listCategories = list();
		if (CollectionUtils.isNotEmpty(listCategories)) {
			// s'il existe une correspondances de code parmis les categories alors c'est ko
			for (Categorie catPersisted : listCategories) {
				if (catPersisted.getId() != categorieDto.getId() && categorieDto.getCode().equals(catPersisted.getCode())) {
					throw new BadRequestException("il existe déjà une categorie avec ce code");
				}
			}
		}
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public Categorie update(Categorie categorieDto, String cmroc) throws GedeException {
		checkValid(categorieDto);
		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);
		categorieDto.setDateMaj(new Date());
		return daoCategorie.update(categorieDto);

	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public List<Categorie> list() {
		return daoCategorie.listAll();
	}

	public void delete(Long idCategorie, String cmroc) throws GedeException {
		// seul 0000 peut faire des modifs
		checkCmrocCimut(cmroc);
		// on ne peut pas supprimer une categoris si des types sont referencés dessous
		List<Type> ListType = typeDao.listTypeByCriteria(idCategorie, null);
		if (CollectionUtils.isNotEmpty(ListType)) {
			throw new UnauthorizedException("suppression impossible de la categorie, il existe des types de document rattachés à cette categorie");
		}
		super.delete(idCategorie);
	}

	@Override
	protected AbstractDao<Categorie> getDao() {
		return daoCategorie;
	}

}
