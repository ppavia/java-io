package fr.cimut.ged.entrant.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.poi.ss.formula.functions.T;

import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.dto.Page;
import fr.cimut.ged.entrant.dto.TypeDto;

/**
 * Session Bean implementation class EdtDBManager
 */
@SuppressWarnings("ALL") @Stateless(mappedName = "TypeDao")
@LocalBean
public class TypeDao extends AbstractDao<Type> {

	/**
	 * Default constructor.
	 */

	public TypeDao() {

	}

	@Override
	protected Class<Type> getTClass() {
		return Type.class;
	}

	public List<Type> listTypeByCriteria(Long idCategorie, String codeType) {
		final Query query = entityManager.createNamedQuery("Type.findByCriteria");
		query.setParameter("idCategorie", idCategorie);
		query.setParameter("code", codeType);
		return query.getResultList();
	}

	public List<Type> listTypeByCodeList(Set<String> typeDocuments) {
		final Query query = entityManager.createNamedQuery("Type.findByCodeList");
		query.setParameter("codeList", typeDocuments);
		return query.getResultList();
	}

	public String getLibelleByCode(String typeDocument) {
		final Query query = entityManager.createNamedQuery("Type.findByCodeDocument");
		query.setParameter("code", typeDocument);
		return (String) query.getSingleResult();
	}
	
	public Long countTypeWithCode(String codeType) {
		final TypedQuery<Long> query = entityManager.createNamedQuery("Type.countTypeWithCode", Long.class);
		query.setParameter("code", codeType);
		return query.getSingleResult();
	}
	
	
	public List<TypeDto> listAllByPageAndSize(Integer pageNumber, Integer pageSize, String code , String libelle) {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Type> cr = cb.createQuery(Type.class);
		Root<Type> root = cr.from(Type.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>();
		  // checking if parameter name is provided, if yes, adding new predicate
		  if (code != null) {
			predicates.add(cb.like(root.<String>get("code"), "%" + code + "%"));
		  }
		  // checking if parameter code is provided, if yes, adding new predicate
		  if (libelle != null) {
			predicates.add(cb.like(root.<String>get("libelle"), "%" + libelle + "%"));
		     }
		cr.where(predicates.toArray(new Predicate[] {}));
				
		Query query = entityManager.createQuery(cr);
		if (pageNumber != null && pageSize != null) {
			query.setFirstResult((pageNumber-1) * pageSize);
			query.setMaxResults(pageSize);
		}

		List<TypeDto> results = query.getResultList();

		return results;

		
	}

}
