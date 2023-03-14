package fr.cimut.ged.entrant.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import fr.cimut.ged.entrant.beans.db.Categorie;

/**
 * Session Bean implementation class EdtDBManager
 */
@Stateless(mappedName = "CategorieDao")
@LocalBean
public class CategorieDao extends AbstractDao<Categorie> {

	/**
	 * Default constructor.
	 */

	public CategorieDao() {

	}

	@Override
	protected Class<Categorie> getTClass() {
		return Categorie.class;
	}

}
