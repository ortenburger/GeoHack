package de.transformationsstadt.geoportal.DAO;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.Category;



public interface CategoryDAO extends DaoInterface<Category> {
	public Category update(Category existing,Category update);
}