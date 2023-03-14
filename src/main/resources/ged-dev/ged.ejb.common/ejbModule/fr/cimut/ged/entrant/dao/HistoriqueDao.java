package fr.cimut.ged.entrant.dao;

import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import fr.cimut.ged.entrant.beans.db.Historique;

/**
 * Session Bean implementation class EdtDBManager
 */
@SuppressWarnings("ALL") @Stateless(mappedName = "HistoriqueDao")
@LocalBean
public class HistoriqueDao extends AbstractDao<Historique> {

	/**
	 * Default constructor.
	 */

	public HistoriqueDao() {

	}

	public Historique insert(Historique historique) {
		entityManager.persist(historique);
		//		entityManager.flush();
		return historique;
	}

	/**
	 * Met à jour un document
	 * 
	 * @param id
	 * @param xml
	 * @param file
	 * @return
	 */

	@SuppressWarnings("unchecked")
	public List<Historique> getHistoriques(String id) {
		Query query = entityManager.createNamedQuery("Historique.getAllById");
		query.setParameter("id", id);
		return query.getResultList();
	}

	public int updateHistoriqueId(String oldId, String newId) {
		Query query = entityManager.createNamedQuery("Historique.updateAllById");
		query.setParameter("oldId", oldId);
		query.setParameter("newId", newId);
		return query.executeUpdate();
	}

	@Override
	protected Class<Historique> getTClass() {
		return Historique.class;
	}

}
