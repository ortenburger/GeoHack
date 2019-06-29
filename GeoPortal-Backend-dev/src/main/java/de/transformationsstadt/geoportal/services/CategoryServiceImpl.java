package de.transformationsstadt.geoportal.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.CategoryDAO;
import de.transformationsstadt.geoportal.entities.Category;

@Service
@Transactional
public class CategoryServiceImpl extends GenericService<Category> implements CategoryService {
	@Autowired
	CategoryDAO dao;
	
	public Category get(Long id) {
		return dao.get(id);
	}
	
	public Category update(Category currentCat, Category cat) {
		cat.setId(currentCat.getId());
		dao.update(cat);
		return cat;
		
	}
	
	public Category create(Category cat) {
		long id = (long) dao.create(cat);
		cat.setId(id);
		return cat;
	}
	
	public ArrayList<Category> getAll() {
		return (ArrayList<Category>)dao.getAll();
	}
	
	public void update(Category cat) {
		dao.update(cat);
	}

}
