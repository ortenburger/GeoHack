package de.transformationsstadt.geoportal.services;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.UserDAO;
import de.transformationsstadt.geoportal.apiparameters.UserParameters;
import de.transformationsstadt.geoportal.entities.User;

@Service
@Transactional
public class UserServiceImpl extends GenericService<User> implements UserService {

	@Autowired
	UserDAO userDAO;
	
	
	Subject subject;

	public UserServiceImpl() {
		//subject = SecurityUtils.getSubject();
	}

	public User getUser(String username) {
		
		List<User> list = userDAO.getByName(username);
		if(list.size() == 1) {
			return list.get(0);
		}else {
			return null;
		}
	}
	
	public User create(User user) {
		long id = (long) userDAO.create(user);
		user.setId(id);
		return user;		
	}
	
	public User getCurrentUser(){
		
		Subject subject = SecurityUtils.getSubject();
		String currentUserIdentifier = (String)subject.getPrincipal();
		System.out.println("getting user-identifier for "+currentUserIdentifier);
		return getUser(currentUserIdentifier);
		
	}
	
	public User get(long id) {
		return userDAO.get(id);
	}
 
	public void updateUser(User requestedUser, UserParameters userArg) {
		requestedUser.updateUser(userArg);
		userDAO.update(requestedUser);
	}
	
}
