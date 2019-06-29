package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Soll einen Lizenztext speichern. Wird bisher nicht genutzt, weil wir keine Lizenztexte haben. 
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="licenses")
public class License implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue
	private Long id;
	
	private String version;
	@Column(columnDefinition = "text")
	private String description;
	@Column(columnDefinition = "text")
	private String text;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof License) {
			return this.getId().equals( ( (License) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
}
