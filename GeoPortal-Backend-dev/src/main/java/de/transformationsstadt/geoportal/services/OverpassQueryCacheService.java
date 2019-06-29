package de.transformationsstadt.geoportal.services;

import de.transformationsstadt.geoportal.entities.OverpassQueryCacheElement;
import de.transformationsstadt.geoportal.entities.Role;

public interface OverpassQueryCacheService extends ServiceInterface<OverpassQueryCacheElement>{
	
	public OverpassQueryCacheElement get(String queryIdentifier);
	public boolean add(OverpassQueryCacheElement el);
}

