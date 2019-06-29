package de.transformationsstadt.geoportal.services;

import de.transformationsstadt.geoportal.entities.Permission;

public interface PermissionService extends ServiceInterface<Permission>{
	
	public Permission create(Permission permission);
	
	
}
