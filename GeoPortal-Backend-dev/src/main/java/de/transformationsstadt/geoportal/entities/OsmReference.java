package de.transformationsstadt.geoportal.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.apache.shiro.SecurityUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import de.transformationsstadt.geoportal.SpringContextProvider;
import de.transformationsstadt.geoportal.DAO.BliDimensionDAO;
import de.transformationsstadt.geoportal.DAO.DataGroupDAO;
import de.transformationsstadt.geoportal.DAO.KeyValuePairDAO;
import de.transformationsstadt.geoportal.DAO.OsmReferenceDAO;

import de.transformationsstadt.geoportal.apiparameters.BliDimensionParameter;
import de.transformationsstadt.geoportal.apiparameters.DataGroupParameter;
import de.transformationsstadt.geoportal.apiparameters.KeyValuePairParameter;
import de.transformationsstadt.geoportal.apiparameters.OsmNodeParameter;
import de.transformationsstadt.geoportal.apiparameters.OsmReferenceParameter;
import de.transformationsstadt.geoportal.services.BliDimensionService;
import de.transformationsstadt.geoportal.services.DataGroupService;
import de.transformationsstadt.geoportal.services.KeyValuePairService;
import de.transformationsstadt.geoportal.services.OsmReferenceService;
import de.transformationsstadt.geoportal.services.UserService;
import de.transformationsstadt.geoportal.services.UserServiceImpl;

/**
 * Stellt eine Referenz auf ein Element aus der OSM-Datenbank dar ( https://www.openstreetmap.org/ )
 * 
 * Zur Referenzierung des entsprechenden OSM-Elementes werden hier die ID und der Typ gespeichert.
 * Darüber hinaus werden, um die Verarbeitung zu ermöglichen, Längen- und Breitengrad gespeichert (bzw. abgeleitet)
 *   
 * Die übrigen Daten stammen nicht (oder nicht zwingend) aus OSM.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */

@Entity
@Table(name="osm_references",uniqueConstraints= {@UniqueConstraint(columnNames= {"osmId","type"})})
public class OsmReference implements Serializable{
	@Autowired
	@Transient
	UserService userService;

	@Autowired
	@Transient
	BliDimensionService bliService;
	
	@Autowired
	@Transient
	KeyValuePairService keyValueService;
	

	@Autowired
	@Transient
	DataGroupService dataGroupService;
	

	@Autowired
	@Transient
	OsmReferenceService osmReferenceService;
	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * TODO:
	 * Licenses und Quelle sinnvoll abbilden.
	 */
	

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@CreationTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	private String name;

	@Column(columnDefinition = "text")
	private String description;

	private User createdBy;

	private Long osmId;

	@UpdateTimestamp
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastChanged;

	private User changedBy;

	@Enumerated
	private OsmElementType type;

	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="osm_reference_datagroups",
			joinColumns = { @JoinColumn(name = "osm_reference_id") }, 
			inverseJoinColumns = { @JoinColumn( name = "group_id") }
			)
	@JsonManagedReference
	private Set<DataGroup> dataGroups = new HashSet<DataGroup>(); 

	@ManyToMany(fetch=FetchType.EAGER, targetEntity = OsmReference.class)
	@JoinTable(name="reference_peers", 
	                joinColumns={@JoinColumn(name="first_peer_id")}, 
	                inverseJoinColumns={@JoinColumn(name="second_peer_id")})
	@JsonManagedReference
	@JsonIgnoreProperties({"peers"})
	private Set<OsmReference> peers = new HashSet<OsmReference>();
	
	
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(
			name="osm_bli",
			joinColumns = { @JoinColumn(name = "osm_reference_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="bli_id") }
			)
	@JsonInclude
	private Set<BliDimension> bliDimensions = new HashSet<BliDimension>();


	@ManyToMany(fetch=FetchType.EAGER,cascade = CascadeType.ALL)
	@JoinTable(
			name="osm_reference_kvp",
			joinColumns = { @JoinColumn(name = "osm_reference_id") }, 
			inverseJoinColumns = { @JoinColumn( name ="kvp_id") }
			)
	@JsonManagedReference
	private Set<KeyValuePair> tags = new HashSet<KeyValuePair>();


	Double lon;


	Double lat;


	protected void setup() {
		this.setCreated(new Date());
		this.setLastChanged(new Date());

	}
	
	public OsmReference() {
		this.lon = (double) -1f;
		this.lat = (double) -1f;
	}
	

	public OsmReference(OsmNodeParameter parameter) {
		if(this.getId() != null) {
			this.setCreated(new Date());
		}else {
			this.setLastChanged(new Date());
		}
		this.setLat(parameter.getLat());
		this.setLon(parameter.getLon());
		this.setOsmId(parameter.getOsmId());
		this.setType(parameter.getOsmElementType());


	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OsmElementType getType() {
		return type;
	}


	public void setType(OsmElementType type) {
		this.type = type;
	}

	public Set<DataGroup> getDataGroups() {
		return dataGroups;
	}

	public void setDataGroups(Set<DataGroup> dataGroups) {
		this.dataGroups = dataGroups;
	}

	/**
	 * Gibt die Netzwerkpartner (vom Typ {@link OsmReference} zurück).
	 * Dabei wird *nicht* rekursiv serialisiert (d.h. die Netzwerkpartner der Netzwerkpartner werden nicht serialisiert)
	 * @return
	 */
	@JsonIgnoreProperties({"peers"})
	public Set<OsmReference> getPeers(){
		return this.peers;
	}
	
	/**
	 * Setzt die Liste der Netzwerkpartner
	 * @param peers Set<{@link OsmReference}>
	 */
	public void setPeers(Set<OsmReference> peers) {
		this.peers = peers;
	}

	/**
	 * Setzt die Tags des Elementes
	 * @return
	 */
	public Set<KeyValuePair> getTags() {
		return this.tags;
	}
	
	public boolean hasTag(String str) {
		for(KeyValuePair kvp : this.tags) {
			if(kvp.getKey().equals(str)) {
				return true;
			}
		}
		return false;
	}
	public boolean removeTag(String str) {
		KeyValuePair removalCandidate = null;
		for(KeyValuePair kvp : this.tags) {
			if(kvp.getKey().equals(str)) {
				removalCandidate=kvp;
			}
		}
		if(removalCandidate == null) {
			return false;
		}
		return this.tags.remove(removalCandidate);
	}
	
	public boolean hasTag(KeyValuePair tag){
		if(this.tags == null) {
			return false;
		}
		return this.tags.contains(tag);
		
	}
	public void addTag(KeyValuePairParameter kvpp) {
		KeyValuePair kvp = new KeyValuePair(kvpp);
		this.addTag(kvp);
	}
	public void addTag(KeyValuePair kvp) {
		this.tags.add(kvp);
	}

	public void setTags(Set<KeyValuePair> tags) {
		this.tags = tags;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	/**
	 * Gibt das Datum zurück, an dem das Element angelegt/in der Datenbank gespeichert wurde.
	 * @return
	 */
	public Date getCreated() {
		return created;
	}
	/**
	 * Setzt das Datum, an dem das Element angelegt/in der Datenbank gespeichert wurde.
	 * @return
	 */
	public void setCreated(Date created) {
		this.created = created;
	}
	
	@JsonIgnoreProperties({ "accountCreated","account_created", "active","description","email","eulaVersion","firstname","lastLogin","last_login","login_count","eula_version","lastname","locked","loginCount","password_hash","roles","salt","tags","updating","validated"})
	public User getCreatedBy() {
		return createdBy;
	}
	
	
	public void setCreatedBy(User created_by) {
		this.createdBy = created_by;
	}
	
	public Date getLastChanged() {
		return lastChanged;
	}

	public void setLastChanged(Date date) {
		this.lastChanged = date;
	}
	@JsonIgnoreProperties({ "accountCreated","account_created","active","description","email","eulaVersion","firstname","lastLogin","last_login","login_count","eula_version","lastname","locked","loginCount","password_hash","roles","salt","tags","updating","validated"})
	public User getChangedBy() {
		return changedBy;
	}
	
	public void setChangedBy(User changed_by) {
		this.changedBy = changed_by;
	}

	public Set<BliDimension> getBliDimensions() {
		return bliDimensions;
	}

	public void setBliDimensions(Set<BliDimension> bliDimensions) {
		this.bliDimensions = bliDimensions;
	}
	
	/**
	 * Fügt eine bisher nicht mit diesem Eintrag assoziierte BLI-Dimension hinzu. 
	 * @param dim {@link BliDimension}
	 */
	public void addBliDimension(BliDimension dim) {

		if(dim == null) {
			return;
		}
		
		if(this.bliDimensions == null) {
			this.bliDimensions = new HashSet<BliDimension>();
		}
		this.bliDimensions.add(dim);
	}
	
	public Long getOsmId() {
		return osmId;
	}
	public void setOsmId(Long osmId) {
		this.osmId = osmId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	/*
	public Set<KeyValuePair> getValues() {
		return values;
	}
	public void setValues(Set<KeyValuePair> values) {
		this.values = values;
	}*/


	
	
	public void addDataGroup(DataGroup dataGroup) {
		if(dataGroup != null) {
			this.dataGroups.add(dataGroup);
		}
	}

	/**
	 * Fügt einen Netzwerkpartner hinzu
	 * 
	 * Nicht existierende {@link OsmReference}-Objekte werden hier persistent.
	 * @param newPeer
	 */
	public void addPeer(OsmReference peer) {
		
		if(this.peers == null) {
			this.peers=new HashSet<OsmReference>();
		}
		
		if(peer.getId() != null && this.getId() != null) {
			if(peer.getId().equals(this.getId())) {
				return;
			}
		}else {
			if( peer.getOsmId() != null && getId() != null && peer.getType().equals(getType()) ){
				return;
			}
		}
		
		if(peer != null) {
			if(!this.peers.contains(peer)) {
				this.peers.add(peer);
			}
		}
		
	}

	/*
	 * Entfernt einen Netzwerkpartner
	 */
	public void removePeer(OsmReference peer) {
		if(this.peers == null) {
			return;
		}
		if(this.peers.contains(peer)) {
			this.peers.remove(peer);
		}
	}
	public boolean hasPeer(long id) {
		if(this.peers==null) {
			return false;
		}
		for(OsmReference peer:this.peers) {
			if(peer.getId().equals(id)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasPeer(OsmReference ref) {
		if(this.peers == null) {
			return false;
		}
		return this.peers.contains(ref);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof OsmReference) {
			return this.getId().equals(((OsmReference)obj).getId());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return Long.hashCode(this.getId());	
	}

	public void removeTag(KeyValuePair kvp) {
		if(this.tags == null) {
			return;
		}
		this.tags.remove(kvp);
		
	}

	public boolean hasDimension(long dimId) {
		if(this.bliDimensions == null || this.bliDimensions.size()==0) {
			return false;
		}
		for(BliDimension dim: this.bliDimensions) {
			if(dim.getId().equals(dimId)) {
				return true;
			}
		}
		return false;
	}
	public boolean hasTag(Long tagId) {

		if(this.tags == null) {
			return false;
		}else {
			for(KeyValuePair kvp: this.tags) {
				if(kvp.getId() == tagId) {
					return true;
				}
			}
			return false;
		}
		
	}

	public void flatten() {
		peers.clear();
		dataGroups.clear();
		description = "";
	}
}
