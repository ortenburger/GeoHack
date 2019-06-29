package de.transformationsstadt.geoportal.DAO;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.Permission;

@Repository
public class PermissionDAOImpl extends GenericDao<Permission> implements PermissionDAO{
	
	public Permission getPermission(String identifier) {
		return getSingleResultByField("name", identifier);
	}

}