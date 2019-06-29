package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Verschlagwortung von Entitäten.
 * 
 * Wird bisher nicht genutzt, das machen jetzt die {@link KeyValuePair}-Elemente
 * 
 * @deprecated
 * 
 * @author Sebastian Bruch
 *
 */

@Entity
@Table(name="tags")
@JsonIdentityInfo(
		  generator = ObjectIdGenerators.PropertyGenerator.class, 
		  property = "id")
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)

public class Tag implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	Long id;
	
	@NotNull
	@Column(unique=true)
	String name;
	/*
	@JsonManagedReference(value="alias_of")
	private Tag alias_of;
	*/
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
			name="datagroup_tags",
			joinColumns = { @JoinColumn(name = "tag_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="group_id") }
			)
	@JsonIgnore
	private Set<DataGroup> datagroups = new HashSet<DataGroup>();
	
	@JsonIgnore
	public Set<DataGroup> getDatagroups() {
		return datagroups;
	}
	
	@JsonIgnore
	public void setDatagroups(Set<DataGroup> datagroups) {
		this.datagroups = datagroups;
	}

	public Tag() {
		
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Tag) {
			return this.getId().equals( ( (Tag) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
}
