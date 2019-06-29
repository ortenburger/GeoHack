package de.transformationsstadt.geoportal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.DataGroupDAO;
import de.transformationsstadt.geoportal.entities.DataGroup;


public interface DataGroupService extends ServiceInterface<DataGroup> {
	
	public DataGroup get(long id);

	public DataGroup update(DataGroup currentDataGroup, DataGroup dg);

	public DataGroup create(DataGroup dg);

	public List<DataGroup> getAll();

	public List<DataGroup> search(String query);
	
}
