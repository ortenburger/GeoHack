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
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.transformationsstadt.geoportal.DAO.DataGroupDAO;
import de.transformationsstadt.geoportal.apiparameters.DataGroupParameter;

/**
 * DataGroup
 * 
 * Diese Klasse/JPA-Entity definiert eine Datengruppe im GeoPortal.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="data_groups")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class DataGroup implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private String name;


	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="datagroup_tags",
			joinColumns = { @JoinColumn(name = "group_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="tag_id") }
			)
	
	private Set<Tag> tags = new HashSet<Tag>();
	
	
	public Set<Tag> getTags() {
		return tags;
	}

	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="datagroup_subgroups",
			joinColumns = { @JoinColumn(name = "group_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="subgroup_id") }
			)
	@JsonManagedReference(value="dataGroups")
	private Set<DataGroup> dataGroups = new HashSet<DataGroup>();
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="datagroup_subgroups",
			joinColumns = { @JoinColumn(name = "subgroup_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="group_id") }
			)
	@JsonBackReference(value="dataGroups")
	private Set<DataGroup> parentDataGroups = new HashSet<DataGroup>();
	
	

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="osm_reference_datagroups",
			joinColumns = { @JoinColumn(name = "group_id") }, 
			inverseJoinColumns = { @JoinColumn( name = "osm_reference_id") }
			)
	private Set<OsmReference> elements = new HashSet<OsmReference>(); 
	
	public Set<OsmReference> getElements() {
		return elements;
	}

	public void setElements(Set<OsmReference> elements) {
		this.elements = elements;
	}

	public void setParentDataGroups(Set<DataGroup> parentDataGroups) {
		this.parentDataGroups = parentDataGroups;
	}

	public Set<DataGroup> getParentDataGroups(){
		return parentDataGroups;
	}
	
	@JsonManagedReference
	public Set<DataGroup> getDataGroups() {
		return dataGroups;
	}

	public void setDataGroups(Set<DataGroup> dataGroups) {
		this.dataGroups = dataGroups;
	}
	public void addDataGroup(DataGroup dg) {
		this.dataGroups.add(dg);
	}
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="category_datagroups",
			joinColumns = { @JoinColumn(name = "group_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="cat_id") }
			)

	private List<Category> categories= new ArrayList<Category>();
	
	public List<Category> getCategories() {
		return categories;
	}
	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}

	@Column(columnDefinition = "text")
	private String description;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="datagroup_bli",
			joinColumns = { @JoinColumn(name = "group_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="bli_id") }
			)
	@JsonManagedReference(value="datagroupBliDimensions")
	private Set<BliDimension> bliDimensions = new HashSet<BliDimension>();
	
	
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

	


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}



	public Set<BliDimension> getBliDimensions() {
		return bliDimensions;
	}


	public void setBliDimensions(Set<BliDimension> bliDimensions) {
		this.bliDimensions = bliDimensions;
	}
	public DataGroup() {};
	public DataGroup(String str) {
		this.name=str;
	}

	public DataGroup(DataGroupParameter dgp) {
		if(dgp == null) {
			throw new NullPointerException("DataGroupParemeter is null");
		}
		if(dgp.id != null) {
			this.id = dgp.id;
		}else {
			throw new NullPointerException("id is null for DataGroup "+dgp.name);
		}
		if(dgp.name == null|| dgp.name.isEmpty()) {
			throw new RuntimeException("Name is empty.");
		}
		this.name = dgp.name;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof DataGroup) {
			return this.getId().equals( ( (DataGroup) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		if(this.id == null) {
			return -1;
		}
		return Long.hashCode(this.getId());
		
	}

	public void removeElements() {
		if(this.elements!=null) {
			this.elements.clear();
		}
		for(DataGroup dg: this.dataGroups) {
			dg.removeElements();
		}
		
	}
}
