package de.transformationsstadt.geoportal.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.CategoryDAO;
import de.transformationsstadt.geoportal.entities.Category;

public interface CategoryService extends ServiceInterface<Category>{
	public Category get(Long id);
	public Category update(Category currentCat, Category cat);
	public Category create(Category cat);
	public ArrayList<Category> getAll();
	public void update(Category cat);

}
