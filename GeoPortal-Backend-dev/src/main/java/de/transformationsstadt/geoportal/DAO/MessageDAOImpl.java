package de.transformationsstadt.geoportal.DAO;

import java.util.ArrayList;


import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.apiparameters.PaginationInfo;
import de.transformationsstadt.geoportal.entities.Message;
import de.transformationsstadt.geoportal.entities.User;



@Repository 
public class MessageDAOImpl extends GenericDao<Message> implements MessageDAO{

	
	public ArrayList<Message> getMessages(User user, PaginationInfo pagination){
		return (ArrayList<Message>) getAll();
	}
}
