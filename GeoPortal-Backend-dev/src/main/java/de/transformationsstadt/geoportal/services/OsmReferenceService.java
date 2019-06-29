package de.transformationsstadt.geoportal.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.apiparameters.OsmNodeParameter;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.Rectangle;


public interface OsmReferenceService extends ServiceInterface<OsmReference> {
	public OsmReference get(Long id);
	public void update(OsmReference ref);
	public OsmReference update(OsmReference ref, OsmNodeParameter osmnode);
	public List<OsmReference> getAll();

	public List<OsmReference> getOsmReferencesByOsmId(Long id, String type);

	public OsmReference getFirstOsmReferenceByOsmId(Long id, String type);

	public List<OsmReference> getByBoundingBox(Rectangle rect);

	public void removeElement(Long id);

	public OsmReference create(OsmReference node);
	
	public OsmReference create(OsmNodeParameter node);
	
	public OsmReference getOsmReferencesByOsmNodeParameter(OsmNodeParameter osmnode);

	public long Count();
	public OsmReference getOrCreate(OsmReference osmRef);

	public List<OsmReference> search(String query, Rectangle rect);

	public List<Object[]> autocompleteOsmReferences(String query, Integer maxResults);

	public boolean removePeer(Long id, Long peerId);

	public void merge(OsmReference node);
	public void delete(OsmReference el);
	
}
