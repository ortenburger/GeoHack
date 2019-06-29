package de.transformationsstadt.geoportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.KeyValuePairDAO;
import de.transformationsstadt.geoportal.entities.KeyValuePair;


public interface KeyValuePairService extends ServiceInterface<KeyValuePair>{
	public KeyValuePair get(KeyValuePair kvp);

	public KeyValuePair create(KeyValuePair kvp);

	public boolean exists(KeyValuePair kvp);

	public KeyValuePair get(Long tagId);

}
