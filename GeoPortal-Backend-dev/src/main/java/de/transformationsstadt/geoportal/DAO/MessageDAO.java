package de.transformationsstadt.geoportal.DAO;

import java.util.ArrayList;


import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.apiparameters.PaginationInfo;
import de.transformationsstadt.geoportal.entities.Message;
import de.transformationsstadt.geoportal.entities.User;




public interface MessageDAO extends DaoInterface<Message>{


	
	public ArrayList<Message> getMessages(User user, PaginationInfo pagination);
}
