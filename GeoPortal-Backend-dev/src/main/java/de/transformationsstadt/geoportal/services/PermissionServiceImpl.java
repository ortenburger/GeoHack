package de.transformationsstadt.geoportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.PermissionDAO;
import de.transformationsstadt.geoportal.entities.Permission;
import de.transformationsstadt.geoportal.entities.Role;

@Service
@Transactional
public class PermissionServiceImpl extends GenericService<Permission>  implements PermissionService {
	@Autowired
	PermissionDAO dao;

	public Permission create(Permission permission) {
		long id = (long) dao.create(permission);
		permission.setId(id);
		return permission;
	}
	
}
