package de.transformationsstadt.geoportal.DAO;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.OverpassQueryCacheElement;
import de.transformationsstadt.geoportal.services.OverpassQueryCacheService;


@Repository
public class OverpassQueryCacheElementDAOImpl extends GenericDao<OverpassQueryCacheElement> implements OverpassQueryCacheElementDAO{

	@Lock(LockModeType.READ)
	
	public boolean add(OverpassQueryCacheElement oqce) {
		return create(oqce)!= null;
	}
	@Lock(LockModeType.WRITE)
	
	public OverpassQueryCacheElement get(String query) {
		return getSingleResultByField("query", query);
		
	}
}