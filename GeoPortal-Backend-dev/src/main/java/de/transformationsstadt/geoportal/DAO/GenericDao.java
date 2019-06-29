package de.transformationsstadt.geoportal.DAO;

import java.lang.reflect.ParameterizedType;
import java.io.Serializable;
import java.util.List;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;


public abstract class GenericDao<E extends Serializable> implements DaoInterface<E>  {

	protected final Class<E> entityClass;

	@Autowired
    private SessionFactory sessionFactory;


	@SuppressWarnings("unchecked")
	public GenericDao() {
		this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	@Override
	@Modifying
	@Transactional(readOnly=false)
	public Serializable create(E e) {
		Serializable id = session().save(e);
		//System.out.println("Saving "+this.entityClass.getName()+ ". id: "+id);
		return id;
	}

	@Transactional(readOnly=true)
	public List<E> getByField(String field,String value) {

		CriteriaBuilder builder = session().getCriteriaBuilder();
		CriteriaQuery<E> cq = builder.createQuery(entityClass);
		Root<E> root = cq.from(entityClass);
		cq.select(root).where(builder.like(root.get(field), value));
		return session().createQuery(cq).getResultList();
	}
	
	@Transactional(readOnly=true)
	public E getSingleResultByField(String field,String value) {

		CriteriaBuilder builder = session().getCriteriaBuilder();
		CriteriaQuery<E> cq = builder.createQuery(entityClass);
		Root<E> root = cq.from(entityClass);
		cq.select(root).where(builder.like(root.get(field), value));
		List<E> list = session().createQuery(cq).setMaxResults(1).getResultList();
		if(list.size() == 1) {
			return list.get(0);
		}
		return null;
	}
	
	@Override
	@Transactional(readOnly=true)
	public E get(Serializable id) {
		return (E) session().get(this.entityClass, id);	
	}

	@SuppressWarnings("unchecked")
	@Override
	@Modifying
	@Transactional(readOnly=false)
	public E merge(E e) {
		return (E) session().merge(e);
	}

	@Override
	@Modifying
	@Transactional(readOnly=false)
	public E update(E e) {
		session().update(e);
		return e;
	}

	@Override
	@Modifying
	@Transactional(readOnly=false)
	public void delete(Serializable id) {
		session().delete(get(id));
	}

	@Override
	@Transactional(readOnly=true)
	public List<E> getAll() {

		CriteriaBuilder builder = session().getCriteriaBuilder();
		CriteriaQuery<E> cq = builder.createQuery(this.entityClass);
		Root<E> root = cq.from(entityClass);
		cq.select(root);
		
		return session().createQuery(cq).getResultList();
	}

	@Override
	@Transactional(readOnly=true)
	public long count() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	@Modifying
	@Transactional(readOnly=false)
	public Serializable saveOrUpdate(E e) {
		session().saveOrUpdate(e);
		return null;
	}

	@Override
	public void clear() {
		session().clear();
	}

	@Override
	public void flush() {
		session().flush();
		
	}
    public Session session() {
        return sessionFactory.getCurrentSession();
    }
	
}
