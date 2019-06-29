package de.transformationsstadt.geoportal.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.DAO.LogEntryDAO;
import de.transformationsstadt.geoportal.entities.LogEntry;

@Service
@Transactional
public class LogServiceImpl extends GenericService<LogEntry> implements LogService {
	@Autowired
	LogEntryDAO logDao;
	
	public void create(LogEntry le){
		logDao.create(le);
	}
	public LogEntry get(long id) {
		return logDao.get(id);
	}
}
