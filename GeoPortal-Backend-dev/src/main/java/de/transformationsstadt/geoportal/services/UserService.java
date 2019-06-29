package de.transformationsstadt.geoportal.services;

import java.util.List;



import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.apiparameters.UserParameters;
import de.transformationsstadt.geoportal.entities.User;


public interface UserService extends ServiceInterface<User>{
	
	public User getUser(String username);
	public User create(User user);
	public User getCurrentUser();	
	public User get(long id);
	public void updateUser(User requestedUser, UserParameters userArg);
	
	
}
