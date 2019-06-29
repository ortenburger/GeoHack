package de.transformationsstadt.geoportal.DAO;


import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.KeyValuePair;



@Repository
public class KeyValuePairDAOImpl extends GenericDao<KeyValuePair> implements KeyValuePairDAO{

	
	public  boolean exists(KeyValuePair kvp) {

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<KeyValuePair> cq = b.createQuery(KeyValuePair.class);
		Root<KeyValuePair> root = cq.from(KeyValuePair.class);
		cq = cq.select(root);
		
		cq = cq.where(
					b.and(
						b.equal(root.get("key"), kvp.getKey()),
						b.equal(root.get("value"),kvp.getValue())
					) 
				);
		
		return session().createQuery(cq).getResultList().size() > 0;
	}
	
	public KeyValuePair get(KeyValuePair kvp) {

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<KeyValuePair> cq = b.createQuery(KeyValuePair.class);
		Root<KeyValuePair> root = cq.from(KeyValuePair.class);
		cq = cq.select(root);
		
		cq = cq.where(
					b.and(
						b.equal(root.get("key"), kvp.getKey()),
						b.equal(root.get("value"),kvp.getValue())
					) 
				);
		return session().createQuery(cq).setMaxResults(1).getSingleResult();
				
	}
	
		
}
