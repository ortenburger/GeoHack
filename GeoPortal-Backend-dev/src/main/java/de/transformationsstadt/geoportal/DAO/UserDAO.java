package de.transformationsstadt.geoportal.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.User;



public interface UserDAO extends DaoInterface<User>{
	public List<User> getByName(String username);
}