package de.transformationsstadt.geoportal.DAO;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.KeyValuePair;


public interface KeyValuePairDAO extends DaoInterface<KeyValuePair>{

	public  boolean exists(KeyValuePair kvp);
	public KeyValuePair get(KeyValuePair kvp);	
		
}
