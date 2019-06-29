package de.transformationsstadt.geoportal.DAO;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.apiparameters.OsmNodeParameter;
import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.Rectangle;



public interface OsmReferenceDAO extends DaoInterface<OsmReference> {

	public Serializable create(OsmReference ref);
	public OsmReference getOsmReferenceById(Long id);
	public OsmReference getFirstOsmReferenceByOsmId(long id, String type);
	
	
	
	// das ist eine liste, da osm-ids nicht eindeutig sind (i.E. eine Id kann von Nodes, Ways usw. belegt sein)
	public List<OsmReference> getOsmReferencesByOsmId(Long id, String type);
	// das ist eine liste, da osm-ids nicht eindeutig sind (i.E. eine Id kann von Nodes, Ways usw. belegt sein)
	public OsmReference getOsmReferencesByOsmNodeParameter(OsmNodeParameter ref);
	
	public List<OsmReference> search(String searchString,Rectangle rect);
	
	public List<OsmReference> search(String searchString);
	
	public boolean removePeer(Long lhsId,Long rhsId);
	public void removeElement(Long id);
	

	public void removeElement(OsmReference ref);




	public List<OsmReference> getByBoundingBox(Rectangle rect);
	
	public ArrayList<String> autocomplete(String searchString,Integer _maxResults);
	
	public ArrayList<Object[]> autocompleteOsmReferences(String searchString,Integer _maxResults);
}
