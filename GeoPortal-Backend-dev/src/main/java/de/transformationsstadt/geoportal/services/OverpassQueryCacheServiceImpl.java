package de.transformationsstadt.geoportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.OverpassQueryCacheElementDAO;
import de.transformationsstadt.geoportal.entities.OverpassQueryCacheElement;

@Service
@Transactional
public class OverpassQueryCacheServiceImpl extends GenericService<OverpassQueryCacheElement> implements OverpassQueryCacheService{

	@Autowired
	OverpassQueryCacheElementDAO dao;

	public OverpassQueryCacheElement get(String queryIdentifier) {
		return dao.get(queryIdentifier);
	}
	public boolean add(OverpassQueryCacheElement el) {
		return dao.add(el);		
	}
}
