package fr.cimut.ged.entrant.service;

import org.jboss.resteasy.spi.BadRequestException;

import fr.cimut.ged.entrant.dao.AbstractDao;
import fr.cimut.ged.entrant.exceptions.GedeException;
import fr.cimut.ged.entrant.exceptions.NotFoundException;

/**
 * classe mère de toutes les implémentation de service
 * 
 * @author jlebourgocq
 *
 */
public abstract class AbstractService<T> {

	public void checkEntityNotNull(Object entity) throws GedeException {
		if (null == entity) {
			throw new NotFoundException("objet non trouvé");
		}
	}

	public T getByIdAndCheckNotNull(Long id) throws GedeException {
		T entity = getDao().get(id);
		checkEntityNotNull(entity);
		return entity;
	}

	/**
	 * 
	 * @param code
	 * @return
	 */
	public void delete(Long id) throws GedeException {
		if (null != id) {
			getDao().delete(id);
		}
	}

	public void checkCmrocCimut(String cmroc) {
		if (!"0000".equals(cmroc)) {
			throw new BadRequestException("la modification de type de document n'est réalisable que par les administrateurs cimut");
		}
	}

	protected abstract AbstractDao<T> getDao();
	
	public T getById(Long id) throws GedeException {
		T entity = getDao().get(id);
		return entity;
	}

}
