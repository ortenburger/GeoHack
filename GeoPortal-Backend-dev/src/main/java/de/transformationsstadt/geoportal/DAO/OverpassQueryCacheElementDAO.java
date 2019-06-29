package de.transformationsstadt.geoportal.DAO;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.OverpassQueryCacheElement;



public interface OverpassQueryCacheElementDAO extends DaoInterface<OverpassQueryCacheElement> {

	public boolean add(OverpassQueryCacheElement oqce);
	
	public OverpassQueryCacheElement get(String query);
}