package de.transformationsstadt.geoportal.apiparameters;

import java.util.ArrayList;

import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.Tag;

/**
 * Wird nicht dokumentiert. Bitte nicht benutzen.
 * 
 * @deprecated
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 * 
 * TODO: Die letzten Nutzungen dieser Klasse rauswerfen, die Klasse entfernen und OsmNodeParameter umbenennen.
 *
 */
public class OsmReferenceParameter {

	private OsmElementType type;
	private Long osmId;
	private ArrayList<Long> dataGroups = new ArrayList<Long>(); 
	private ArrayList<Long> peers = new ArrayList<Long>(); 
	private ArrayList<Long> bliDimensionIds = new ArrayList<Long>();
	ArrayList<Tag> tags = new ArrayList<Tag>();
	Double lon;
	Double lat;
	ArrayList<String> errors = new ArrayList<String>();
	public ArrayList<String> getErrors(){
		return this.errors;
	}
	public boolean isValid() {
		if(this.lon == null) {
			this.errors.add("Longitude is not set.");
		}
		if(this.lat == null) {
			this.errors.add("Latitude is not set.");
		}
		if(this.bliDimensionIds.isEmpty()) {
			this.errors.add("Every item has to be associated with at least one Bli-Dimension");
		}
		if(this.type == null || this.type == OsmElementType.UNSET) {
			this.errors.add("Type is not set.");
		}
		if(this.osmId == null) {
			this.errors.add("osmid missing.");
		}
		
		return this.errors.isEmpty();
	}
	
	public OsmElementType getType() {
		return type;
	}
	
	public void setType(OsmElementType type) {
		this.type = type;
	}
	
	public Long getOsmId() {
		return osmId;
	}
	
	public void setOsmId(Long osmId) {
		this.osmId = osmId;
	}
	
	public ArrayList<Long> getDataGroups() {
		return dataGroups;
	}
	
	public void setDataGroups(ArrayList<Long> dataGroups) {
		this.dataGroups = dataGroups;
	}
	public ArrayList<Long> getPeers() {
		return peers;
	}
	public void setPeers(ArrayList<Long> peers) {
		this.peers = peers;
	}
	public ArrayList<Long> getBliDimensionIds() {
		return bliDimensionIds;
	}
	public void setBliDimensionIds(ArrayList<Long> bliDimensionIds) {
		this.bliDimensionIds = bliDimensionIds;
	}
	public ArrayList<Tag> getTags() {
		return tags;
	}
	public void setTags(ArrayList<Tag> tags) {
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
	public void setErrors(ArrayList<String> errors) {
		this.errors = errors;
	}
}
