package de.transformationsstadt.geoportal.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.DataGroupDAO;
import de.transformationsstadt.geoportal.entities.DataGroup;

@Service
@Transactional
public class DataGroupServiceImpl extends GenericService<DataGroup> implements DataGroupService  {
	@Autowired
	DataGroupDAO dao;
	
	public DataGroup get(long id) {
		return dao.get(id);
	}
	
	public DataGroup update(DataGroup currentDataGroup, DataGroup dg) {
		dg.setId(currentDataGroup.getId());
		dao.update(dg);
		return dg;
	}
	
	public DataGroup create(DataGroup dg) {
		long id = (long) dao.create(dg);
		dg.setId(id);
		return dg;
	}
	
	public List<DataGroup> getAll() {
		return dao.getAll();
	}
	
	public List<DataGroup> search(String query) {
		return dao.search(query);
	}
	
}
