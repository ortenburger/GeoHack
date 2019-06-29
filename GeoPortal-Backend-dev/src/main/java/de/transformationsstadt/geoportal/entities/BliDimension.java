package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.util.HashSet;
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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;


/**
 * BLI-Dimension
 * 
 * 
 * Die BLI-Dimensionen (Better-Life-Index, siehe ( http://www.oecdbetterlifeindex.org/de/ ) ) sollen allen im GeoPortal befindlichen Daten angehangen werden.
 * Die Operanden sind Objekte des Typs {@link BliDimension} und im wesentlichen ein gesondert behandelter Satz von Schlagworten
 * 
 * Da die BLI-Dimensionen ein integraler Bestandteil des Konzeptes des GeoPortals sind, sollen diese nur von Administratoren verändert werden können.
 * 
 * TODO: Eine verlinkbare Erklärung inwiefern die hier genutzte Definition des Better-Life-Index von der der OECD abweicht. 
 * 
 * Diese Klasse/JPA-Entity definiert eine Dimension des Indexes.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Entity
@Table(name="bli_dimensions")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class BliDimension implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	@Column(unique=true)
	private String name;
	@Column(columnDefinition = "text")
	private String description;

	private String slug;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="bli_tags",
			joinColumns = { @JoinColumn(name = "bli_id") }, 
			inverseJoinColumns = { @JoinColumn( name =" tag_id") }
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

	/**
	 * 
	 * @return Long
	 */
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

	public BliDimension() {};
	public BliDimension(String name, String description) {
		this.name = name;
		this.description = description;
	}
	

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="datagroup_bli",
			joinColumns = { @JoinColumn(name = "bli_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="group_id") }
			)
	@JsonBackReference(value="datagroupBliDimensions")
	private Set<DataGroup> dataGroups = new HashSet<DataGroup>();
	
	

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof BliDimension) {
			return this.getId().equals( ( (BliDimension) obj ).getId() );
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());
		
	}

	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}

}
