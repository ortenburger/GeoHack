package de.transformationsstadt.geoportal.services;

import java.lang.reflect.ParameterizedType;

public abstract class GenericService<E> implements ServiceInterface<E>  {

	protected final Class<E> entityClass;

	@SuppressWarnings("unchecked")
	public GenericService() {
		this.entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	
}