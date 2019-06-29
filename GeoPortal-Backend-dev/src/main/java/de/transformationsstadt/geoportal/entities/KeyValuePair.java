package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import de.transformationsstadt.geoportal.apiparameters.KeyValuePairParameter;

@Entity
@Table(name="key_value_storage")
public class KeyValuePair implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;

	private String key;
	@Column(columnDefinition = "text")
	private String value;

	@Column(columnDefinition = "text")
	private String displayName;
	
	@Column(columnDefinition = "text")
	private String source;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public KeyValuePair() {

	}
	public KeyValuePair(KeyValuePairParameter kvpp) {
		if(kvpp == null) {
			throw new NullPointerException("Parameter is null");
		}
		
		this.key = kvpp.key;
		this.value = kvpp.value;
		this.id = kvpp.id;
	}
	public KeyValuePair(String key, String value, String displayName) {
		this.setDisplayName(displayName);
		this.setKey(key);
		this.setValue(value);
	}
	public KeyValuePair(String key, String value) {
		this.key=key;
		this.value=value;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key.trim();
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value.trim();
	}
	
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof KeyValuePair) {
			return this.getId().equals( ( (KeyValuePair) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}

}
