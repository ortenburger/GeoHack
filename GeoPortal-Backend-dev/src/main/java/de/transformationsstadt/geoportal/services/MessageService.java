
package de.transformationsstadt.geoportal.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.MessageDAO;
import de.transformationsstadt.geoportal.apiparameters.PaginationInfo;
import de.transformationsstadt.geoportal.entities.Message;
import de.transformationsstadt.geoportal.entities.User;


public interface MessageService extends ServiceInterface<Message>{


	public Message get(Long conversationId);

	public long create(Message msg);

	public void update(Message msg);

	public ArrayList<Message> getMessages(User currentUser, PaginationInfo info);
	
	
}