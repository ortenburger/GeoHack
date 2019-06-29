package de.transformationsstadt.geoportal.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.Role;


public interface RoleDAO extends  DaoInterface<Role>{
	public Role getRole(String identifier);

	public Role getSingleResultByField(String string, String rolename);
	
}
