package de.transformationsstadt.geoportal.DAO;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.BliDimension;

public interface BliDimensionDAO extends DaoInterface<BliDimension> {
	public BliDimension update(BliDimension existing,BliDimension update);
}