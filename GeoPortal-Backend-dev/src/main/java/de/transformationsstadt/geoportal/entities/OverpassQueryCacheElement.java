package de.transformationsstadt.geoportal.entities;


import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * Klasse um Anfrage/Antwort-Paare an die/aus der Overpass-API zu speichern.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="overpass_cache")
public class OverpassQueryCacheElement implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1878292367047938861L;

	@Id
	@GeneratedValue
	Long id;
	
	@Column(unique=true)
	String query;
	@Column(columnDefinition="TEXT")
	String result;
	
    @Version
    private long version;

	LocalDateTime created;
	
	LocalDateTime expires;
	
	public void setExpires(LocalDateTime expires) {
		this.expires = expires;
	}
	
	public LocalDateTime getExpires() {
		return this.expires;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof OverpassQueryCacheElement) {
			return this.getId().equals( ( (OverpassQueryCacheElement) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
	

	
	public long getVersion() {
		return version;
	}

	public void setVersion(long version) {
		this.version = version;
	}
}
