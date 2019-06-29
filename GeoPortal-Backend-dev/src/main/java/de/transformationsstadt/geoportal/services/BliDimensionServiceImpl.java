package de.transformationsstadt.geoportal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.BliDimensionDAO;
import de.transformationsstadt.geoportal.entities.BliDimension;

@Service
@Transactional
public class BliDimensionServiceImpl  extends GenericService<BliDimension> implements BliDimensionService{
	@Autowired
	BliDimensionDAO dao;
	
	public BliDimension get(Long id) {
		return dao.get(id);
	}
	
	public BliDimension update(BliDimension currentDim, BliDimension dim) {
		currentDim.setName(dim.getName());
		currentDim.setDescription(dim.getDescription());
		dao.update(currentDim);
		return currentDim;
	}
	
	public BliDimension create(BliDimension dim) {
		long id = (long) dao.create(dim);
		dim.setId(id);
		return dim;
	}
	
	public List<BliDimension> getAll() {
		return dao.getAll();	
	}

}
