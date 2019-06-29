package de.transformationsstadt.geoportal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.BliDimensionDAO;
import de.transformationsstadt.geoportal.entities.BliDimension;


public interface BliDimensionService extends ServiceInterface<BliDimension> {

	public BliDimension get(Long id);

	public BliDimension update(BliDimension currentDim, BliDimension dim);

	public BliDimension create(BliDimension dim);

	public List<BliDimension> getAll();

}
