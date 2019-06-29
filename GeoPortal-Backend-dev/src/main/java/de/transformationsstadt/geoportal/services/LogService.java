package de.transformationsstadt.geoportal.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.entities.LogEntry;


public interface LogService extends ServiceInterface<LogEntry> {
	public void create(LogEntry le);
	public LogEntry get(long id);
}
