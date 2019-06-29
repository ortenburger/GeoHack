package de.transformationsstadt.geoportal.DAO;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.Category;



@Repository
public class CategoryDAOImpl extends GenericDao<Category> implements CategoryDAO {
	
	public Category update(Category existing,Category update) {

		existing.setName(update.getName());
		existing.setDescription(update.getDescription());
		update(existing);
		return existing;
	}

}