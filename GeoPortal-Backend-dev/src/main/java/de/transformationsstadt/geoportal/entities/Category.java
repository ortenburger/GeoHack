package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Kategorien
 * 
 * Bilden die oberste Hierarchieebene für DatenGruppen.
 * 
 *  * Diese Klasse/JPA-Entity definiert eine Kategorie im GeoPortal.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="categories")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class Category implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	Long id;
	
	@Column(unique=true)
	private String name; 
	
	
	private String displayName;
	
	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="category_datagroups",
			joinColumns= {@JoinColumn(name="cat_id") },
			inverseJoinColumns= {@JoinColumn(name="group_id")}
			)
	@JsonManagedReference(value="categoryDataGroups")
	private List<DataGroup> dataGroups = new ArrayList<DataGroup>(); // in der Kategorie enthaltene Datengruppen
	

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="category_key_suggestions",
			joinColumns= {@JoinColumn(name="cat_id") },
			inverseJoinColumns= {@JoinColumn(name="group_id")}
			)
	@JsonManagedReference(value="suggested_keys")
	private List<KeyValuePair> suggestedKeys = new ArrayList<KeyValuePair>();
	
	
	
	public List<KeyValuePair> getSuggestedKeys() {
		return suggestedKeys;
	}
	public void setSuggestedKeys(List<KeyValuePair> suggestedKeys) {
		this.suggestedKeys = suggestedKeys;
	}
	public void addSuggestedKey(KeyValuePair kvp) {
		if(!this.suggestedKeys.contains(kvp)) {
			this.suggestedKeys.add(kvp);
		}
		
	}
	public void removeSuggestedKey(KeyValuePair kvp) {
		if(this.suggestedKeys.contains(kvp)) {
			this.suggestedKeys.remove(kvp);
		}
	}
	public List<DataGroup> getDataGroups() {
		return dataGroups;
	}
	@JsonIgnore
	public void setDataGroups(List<DataGroup> dataGroups) {
		this.dataGroups = dataGroups;
	}
	public void addDataGroup(DataGroup dg) {
		this.dataGroups.add(dg);
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="category_tags",
			joinColumns = { @JoinColumn(name = "cat_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="tag_id") }
			)
	@JsonIgnore
	private Set<Tag> tags = new HashSet<Tag>();
	@JsonIgnore
	public Set<Tag> getTags() {
		return tags;
	}
	@JsonIgnore
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="category_bli",
			joinColumns = { @JoinColumn(name = "cat_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="bli_id") }
			)
	@JsonIgnore
	private Set<BliDimension> bliDimensions = new HashSet<BliDimension>();
	@JsonIgnore
	public void setDescription(String description) {
		this.description = description;
	}
	@JsonIgnore
	public void setBliDimensions(Set<BliDimension> bliDimensions) {
		this.bliDimensions = bliDimensions;
	}
	@Column(columnDefinition = "text")
	private String description;
	
	public String getDescription() {
		return description;
	}

	public Long getId() {
		return id;
	}

	/**
	 * Setzt die id des Objektes. Achtung: die ID wird durch JPA/Hibernate vergeben.
	 * Ist die id null, wird außerdem an mancher Stelle davon ausgegangen, dass der Datensatz noch nicht persistiert wurde.
	 * @param id Long
	 */
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Set<BliDimension> getBliDimensions() {
		return bliDimensions;
	}
	
	public Category(String name,String description) {
		this.name = name;
		this.description = description;
	}

	public Category() {}
	
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Category) {
			return this.getId().equals( ( (Category) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}
	public void setDisplayName(String displayname) {
		this.displayName = displayname;
	}
	public String getDisplayName() {
		return this.displayName;
	}
}
