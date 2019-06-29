package de.transformationsstadt.geoportal.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.Role;

@Repository
public class RoleDAOImpl extends GenericDao<Role> implements RoleDAO{
	
	
	public Role getRole(String identifier) {
		List<Role> roles = getByField("name",identifier);
		if(roles.size() == 1) {
			return roles.get(0);
		}
		return null;	
	}

	
}