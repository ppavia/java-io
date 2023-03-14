package fr.cimut.ged.entrant.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public abstract class AbstractDao<T> {

	private Logger logger = Logger.getLogger(this.getClass());

	@PersistenceContext(unitName = "MyPersistence")
	protected EntityManager entityManager;

	public void commit() {
		entityManager.getTransaction().commit();
	}

	public void rollback() {
		entityManager.getTransaction().rollback();
	}

	public T create(T t) {
		entityManager.persist(t);
		entityManager.flush();
		return t;
	}

	/**
	 * force flush
	 */
	public void flush() {
		entityManager.flush();
	}

	public T create(T t, boolean andFlush) {
		entityManager.persist(t);
		if (andFlush) {
			// le flush a été mis en place de manière abusive lors de la construction de l'appli, ca a un réel impact lorsque les actions bases sont appelées en masse
			entityManager.flush();
		}
		return t;
	}

	public T detach(T t) {
		entityManager.detach(t);
		return t;
	}

	public void add(ArrayList<T> list) {
		entityManager.persist(list);
		entityManager.flush();
	}

	public T update(T t){
			entityManager.merge(t);
			entityManager.flush();
			return t;
	}

	public void delete(Long id) {
		if (null != id) {
			T t = entityManager.find(getTClass(), id);
			if (t != null) {
				entityManager.remove(t);
				//				entityManager.flush();
			}
		}
	}

	public T delete(String id) {
		if (StringUtils.isNotBlank(id)) {
			T t = entityManager.find(getTClass(), id);
			if (t != null) {
				entityManager.remove(t);
				//				entityManager.flush();
				return t;
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<T> listAll() {
		return entityManager.createQuery("Select t from " + getTClass().getSimpleName() + " t").getResultList();
	}

	public T get(Long id) {
		return entityManager.find(getTClass(), id);
	}

	public T get(String id) {
		return entityManager.find(getTClass(), id);
	}

	protected abstract Class<T> getTClass();
	
}
