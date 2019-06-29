package de.transformationsstadt.geoportal.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.RoleDAO;
import de.transformationsstadt.geoportal.entities.Role;

@Service
@Transactional
public class RoleServiceImpl extends GenericService<Role>  implements RoleService{

	@Autowired
	RoleDAO roleDao;
	
	
	public Role getRole(String rolename) {
		return roleDao.getSingleResultByField("name",rolename);		
	}
	
	
	public Role create(Role role) {
		long id = (long) roleDao.create(role);
		role.setId(id);
		return role;
	}
	
	
	
}
