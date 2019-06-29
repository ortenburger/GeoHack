package de.transformationsstadt.geoportal.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.MessageDAO;
import de.transformationsstadt.geoportal.apiparameters.PaginationInfo;
import de.transformationsstadt.geoportal.entities.Message;
import de.transformationsstadt.geoportal.entities.User;


public class MessageServiceImpl extends GenericService<Message> implements MessageService {

	@Autowired
	MessageDAO dao;

	public Message get(Long conversationId) {
		return dao.get(conversationId);
	}

	public long create(Message msg) {
		return (long) dao.create(msg);
	}

	public void update(Message msg) {
		dao.update(msg);
		
	}

	public ArrayList<Message> getMessages(User currentUser, PaginationInfo info) {
		return dao.getMessages(currentUser, info);
	}
	
	
}
