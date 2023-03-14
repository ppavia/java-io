package fr.cimut.ged.entrant.dao;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import fr.cimut.ged.entrant.beans.db.Json;

/**
 * Session Bean implementation class EdtDBManager
 */
@Stateless(mappedName = "JsonDao")
@LocalBean
public class JsonDao extends AbstractDao<Json> {

	/**
	 * Default constructor.
	 */

	public JsonDao() {

	}

	public void deleteJsonById(String id) {
		Json json = get(id);
		if (json != null) {
			entityManager.remove(json);
		}
	}

	@Override
	protected Class<Json> getTClass() {
		return Json.class;
	}

}
