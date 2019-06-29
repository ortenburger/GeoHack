package de.transformationsstadt.geoportal.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.User;

@Repository
public class UserDAOImpl extends GenericDao<User> implements UserDAO {
	
	public List<User> getByName(String username)
	{
		return getByField("username",username);
	}
}
