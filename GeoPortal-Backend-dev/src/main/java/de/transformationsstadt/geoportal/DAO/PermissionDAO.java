package de.transformationsstadt.geoportal.DAO;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.Permission;


public interface PermissionDAO extends DaoInterface<Permission>{

	public Permission getPermission(String identifier);

}