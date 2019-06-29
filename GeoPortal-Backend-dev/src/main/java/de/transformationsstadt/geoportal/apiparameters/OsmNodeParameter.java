package de.transformationsstadt.geoportal.apiparameters;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.OsmReference;


/**
 * Objekt, in das {@link OsmReference}-Objekte deserialisiert werden.
 * 
 * Siehe ( {@link OsmReference} )
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
public class OsmNodeParameter {
	
	public Long id; // id, falls vorhanden
	public Long mapId; // (ein Feld, das im Frontend genutzt wird und hier nicht weiter stört)
	public Double lat;
	public Double lon;
	public String mode; // (ein Feld, das im Frontend genutzt wird und hier nicht weiter stört)
	public Date created_date; // (ein Feld, das im Frontend genutzt wird und hier nicht weiter stört)
	public Date last_change_date; // (ein Feld, das im Frontend genutzt wird und hier nicht weiter stört)
	public Long getId() {
		return id;
	}
	public String description;
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<DataGroupParameter> getDataGroups() {
		return dataGroups;
	}

	public void setDataGroups(List<DataGroupParameter> dataGroups) {
		this.dataGroups = dataGroups;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<OsmNodeParameter> getNodes() {
		return nodes;
	}

	public void setNodes(List<OsmNodeParameter> nodes) {
		this.nodes = nodes;
	}

	public String getOsmType() {
		return osmType;
	}
	public void setOsmType(String osmType) {
		this.osmType = osmType;
	}
	public Long getOsmId() {
		return osmId;
	}
	public void setOsmId(Long osmId) {
		this.osmId = osmId;
	}
	public List<KeyValuePairParameter> getOsmtags() {
		return osmtags;
	}
	public void setOsmtags(List<KeyValuePairParameter> osmtags) {
		this.osmtags = osmtags;
	}
	public List<KeyValuePairParameter> getTags() {
		return tags;
	}
	public void setTags(List<KeyValuePairParameter> tags) {
		this.tags = tags;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<BliDimensionParameter> getDimensions() {
		return dimensions;
	}
	public void setDimensions(List<BliDimensionParameter> dimensions) {
		this.dimensions = dimensions;
	}
	@JsonIgnore
	public List<DataGroupParameter> getDatagroups() {
		return dataGroups;
	}
	@JsonIgnore
	public void setDatagroups(List<DataGroupParameter> datagroups) {
		this.dataGroups = datagroups;
	}


	List<OsmNodeParameter> nodes=new ArrayList<OsmNodeParameter>();
	List<OsmNodeParameter> peers=new ArrayList<OsmNodeParameter>();
	
	public List<OsmNodeParameter> getPeers() {
		return peers;
	}
	public void setPeers(List<OsmNodeParameter> peers) {
		this.peers = peers;
	}
	String osmType;
	Long osmId;
	List<KeyValuePairParameter> osmtags = new ArrayList<KeyValuePairParameter>();
	List<KeyValuePairParameter> tags = new ArrayList<KeyValuePairParameter>();

	String name;
	List<BliDimensionParameter> dimensions = new ArrayList<BliDimensionParameter>();
	public List<DataGroupParameter> dataGroups = new ArrayList<DataGroupParameter>();
	public OsmElementType getOsmElementType() {
		if(this.osmType == null) {
			System.out.println("OsmType was null on "+this.getName());
			this.osmType = "unset";
		}
		if(this.osmType.toLowerCase().equals("node")) {
			return OsmElementType.NODE;
		}
		if(this.osmType.toLowerCase().equals("way")) {
			return OsmElementType.WAY;
		}
		if(this.osmType.toLowerCase().equals("relation")) {
			return OsmElementType.RELATION;
		}
		return OsmElementType.UNSET;
	}
	public Long getMapId() {
		return mapId;
	}
	public void setMapId(Long mapId) {
		this.mapId = mapId;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
}
