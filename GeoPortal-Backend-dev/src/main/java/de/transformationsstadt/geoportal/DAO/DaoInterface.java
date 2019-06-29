package de.transformationsstadt.geoportal.DAO;

import java.io.Serializable;
import java.util.List;

public interface DaoInterface<E extends Serializable> {
		
	

	// crud
	Serializable create(E e);
	E get(Serializable id);
	E update(E e);
	E merge(E e);
	void delete(Serializable id);

	List<E> getAll();

	long count();

	Serializable saveOrUpdate(E e);

	void clear();
	void flush();
	
	public List<E> getByField(String field,String value);
	public E getSingleResultByField(String field,String value);
}
