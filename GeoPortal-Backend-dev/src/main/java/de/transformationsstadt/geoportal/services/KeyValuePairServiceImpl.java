package de.transformationsstadt.geoportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.KeyValuePairDAO;
import de.transformationsstadt.geoportal.entities.KeyValuePair;

@Service
@Transactional
public class KeyValuePairServiceImpl extends GenericService<KeyValuePair> implements KeyValuePairService {
	@Autowired
	KeyValuePairDAO dao;
	
	public KeyValuePair get(Long tagId) {
		return dao.get(tagId);
	}

	public KeyValuePair create(KeyValuePair kvp) {
		long id = (long) dao.create(kvp);
		kvp.setId(id);
		return kvp;
		
	}

	
	public KeyValuePair get(KeyValuePair kvp) {
		return dao.get(kvp.getId());
	}

	
	public boolean exists(KeyValuePair kvp) {

		return false;
	}

}
