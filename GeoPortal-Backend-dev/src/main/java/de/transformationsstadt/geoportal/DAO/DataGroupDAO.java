package de.transformationsstadt.geoportal.DAO;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.DataGroup;


public interface DataGroupDAO extends DaoInterface<DataGroup>{

	public List<DataGroup> search(String searchString);

	
	
	public DataGroup update(DataGroup existing,DataGroup update);

}