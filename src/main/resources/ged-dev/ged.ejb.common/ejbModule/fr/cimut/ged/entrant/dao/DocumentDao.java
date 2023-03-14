package fr.cimut.ged.entrant.dao;

import fr.cimut.ged.entrant.beans.db.Document;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;
import java.util.List;

/**
 * Session Bean implementation class EdtDBManager
 */
@SuppressWarnings("JpaQueryApiInspection") @Stateless(mappedName = "EdtDBManager")
@LocalBean
public class DocumentDao extends AbstractDao<Document> {

	/**
	 * Default constructor.
	 */
	public DocumentDao() {}

	/**
	 * Met a jour un document
	 *
	 * @param document
	 * @return
	 */
	@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
	public Document updateWithNewTransaction(Document document) {
		entityManager.merge(document);
		entityManager.flush();
		return document;
	}

	/**
	 * Recupere un document depuis son eddocId
	 * 
	 * @param eddocId
	 * @return
	 */

	public Document getByEddocId(String eddocId) {
		String[] ids = eddocId.split("_");
		Query query = entityManager.createNamedQuery("Document.findByEddocId");
		query.setParameter("idstar", Long.parseLong(ids[0]));
		query.setParameter("tsstar", Long.parseLong(ids[1]));
		List<Document> results = query.getResultList();
		if (results.size() == 1) {
			return results.get(0);
		} else {
			return null;
		}
	}

	public List<Document> listDocByEddocIdList(List<Long> idstars, List<Long> tsstars) {
		Query query = entityManager.createNamedQuery("Document.findByEddocIds");
		query.setParameter("idstarList", idstars);
		query.setParameter("tsstarList", tsstars);
		return query.getResultList();

	}


	/**
	 * Recherche des documents par l'idstar uniquement
	 * @param idstars
	 * @return
	 */
	public List<Document> selectEddocsByIdstar(List<Long> idstars) {
		Query query = entityManager.createNamedQuery("Document.findByEddocIdstar");
		query.setParameter("idstarList", idstars);
		return query.getResultList();
	}


	@Override
	protected Class<Document> getTClass() {
		return Document.class;
	}

	public long countDocByIdType(Long idType) {
		Query query = entityManager.createNamedQuery("Document.countDocByIdType");
		query.setParameter("idType", idType);
		return (Long) query.getSingleResult();
	}

	/**
	 * supprime un document depuis son eddocId
	 * 
	 * @param eddocId
	 * @return le nombre de suppression
	 */

	public int deleteByEddocId(String eddocId) {
		String[] ids = eddocId.split("_");
		Query query = entityManager.createNamedQuery("Document.deleteByEddocId");
		query.setParameter("idstar", Long.parseLong(ids[0]));
		query.setParameter("tsstar", Long.parseLong(ids[1]));
		return query.executeUpdate();
	}

}
