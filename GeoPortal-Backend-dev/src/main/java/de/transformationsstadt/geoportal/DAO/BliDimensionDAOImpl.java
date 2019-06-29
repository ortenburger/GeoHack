package de.transformationsstadt.geoportal.DAO;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.BliDimension;

@Repository
public class BliDimensionDAOImpl extends GenericDao<BliDimension> implements BliDimensionDAO{
	
	public BliDimension update(BliDimension existing,BliDimension update) {

		existing.setName(update.getName());
		existing.setDescription(update.getDescription());
		update(existing);
		
		return existing;
	}
	


}