package de.transformationsstadt.geoportal.services;

import de.transformationsstadt.geoportal.entities.Role;

public interface RoleService extends ServiceInterface<Role>{
	
	public Role getRole(String rolename);
	public Role create(Role role);
}
