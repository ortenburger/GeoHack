package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name="changelogs")
public class LogEntry implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	Long id;
	
	@CreationTimestamp
	Date created;
	String userName;
	String method;
	String path;
	Integer statusCode;
	public LogEntry() {
		
	}
	public LogEntry(String user, String method, String path, String data, int status) {
		this.method = method;
		this.path = path;
		this.userName = user;
		this.statusCode = status;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Date getTimestamp() {
		return created;
	}
	
	public void setTimestamp(Date timestamp) {
		this.created = timestamp;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public void setUserName(String user) {
		this.userName = user;
	}

	public Integer getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}
}
