package de.transformationsstadt.geoportal.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.SpringContextProvider;
import de.transformationsstadt.geoportal.DAO.OsmReferenceDAO;
import de.transformationsstadt.geoportal.apiparameters.BliDimensionParameter;
import de.transformationsstadt.geoportal.apiparameters.DataGroupParameter;
import de.transformationsstadt.geoportal.apiparameters.KeyValuePairParameter;
import de.transformationsstadt.geoportal.apiparameters.OsmNodeParameter;
import de.transformationsstadt.geoportal.entities.BliDimension;
import de.transformationsstadt.geoportal.entities.DataGroup;
import de.transformationsstadt.geoportal.entities.KeyValuePair;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.Rectangle;

@Service
@Transactional
public class OsmReferenceServiceImpl extends GenericService<OsmReference> implements OsmReferenceService {
	@Autowired
	OsmReferenceDAO dao;
	
	@Autowired
	UserService userService;
	
	@Autowired
	BliDimensionService bliService;
	
	@Autowired
	KeyValuePairService kvpService;
	
	@Autowired
	DataGroupService dataGroupService;
	
	
	public OsmReference get(Long id) {
		return dao.get(id);
	}
	
	public void update(OsmReference ref) {
		dao.update(ref);
		dao.flush();
	}
	
	public List<OsmReference> getAll() {
		return dao.getAll();
	}
	
	public List<OsmReference> getOsmReferencesByOsmId(Long id, String type) {
		return dao.getOsmReferencesByOsmId(id, type);
	}
	
	public OsmReference getFirstOsmReferenceByOsmId(Long id, String type) {
		return dao.getFirstOsmReferenceByOsmId(id, type);
	}
	
	public List<OsmReference> getByBoundingBox(Rectangle rect) {
		return dao.getByBoundingBox(rect);
	}
	
	public void removeElement(Long id) {
		dao.removeElement(id);	
	}
	
	public OsmReference create(OsmReference node) {
		long id = (long) dao.create(node);
		node.setId(id);
		dao.flush();
		return node;
		
	}
	
	public OsmReference getOsmReferencesByOsmNodeParameter(OsmNodeParameter osmnode) {
		return dao.getOsmReferencesByOsmNodeParameter(osmnode);
	}
	
	public long Count() {
		return dao.count();
	}
	
	public OsmReference getOrCreate(OsmReference osmRef) {	
		OsmReference result = dao.getFirstOsmReferenceByOsmId(osmRef.getOsmId(),osmRef.getType().toString());
		if(result == null) {
			System.out.println("Element not in database: "+osmRef.getName()+". Creating.");
			return create(osmRef);
		}
		System.out.println("Element already in database: "+result.getName());
		return result;
	}
	
	
	public List<OsmReference> search(String query, Rectangle rect) {
		return dao.search(query,rect);
	}

	
	public List<Object[]> autocompleteOsmReferences(String query, Integer maxResults) {
		return dao.autocompleteOsmReferences(query, maxResults);
	}

	
	public boolean removePeer(Long id, Long peerId) {
		
		return false;
	}

	
	@Override
	public void merge(OsmReference node) {
		dao.merge(node);
		
	}
	
	
	/**
	 * Erzeugt das Objekt aus der für die Deserialisierung genutzte Klasse {@link OsmNodeParameter}
	 * 
	 * Achtung: bisher nicht-persistierte Tags ({@link KeyValuePair}) werden dabei persistent.
	 * 
	 * @param p {@link OsmNodeParameter}
	 */
	public OsmReference update(OsmReference ref,OsmNodeParameter p) {
		if(ref == null) {
			return null;
		}
		
		dao.merge(fromOsmNodeParameter(ref,p,false));
		
		for(OsmReference peer :ref.getPeers()) {
			if(!peer.hasPeer(ref)) {
				peer.addPeer(ref);
				update(peer);
			}
		}
		return ref;
	}
	
	
	public OsmReference fromOsmNodeParameter(OsmReference ref,OsmNodeParameter p,boolean create) {
		if(create) {
			ref.setOsmId(p.getOsmId());
			ref.setType(p.getOsmElementType());
			ref.setCreated(new Date());
			ref.setCreatedBy(userService.getCurrentUser());
		}else {
			ref.setLastChanged(new Date());
			ref.setChangedBy(userService.getCurrentUser());	
		}
		if(ref.getChangedBy() == null) {
			System.out.println("User null");
		}
		if(ref.getCreatedBy() == null) {
			System.out.println("Creator null");
		}
		if(userService == null) {
			System.out.println("userService null");
		}
		
		ref.setName(p.getName());
		
		ref.setLon(p.getLon());
		ref.setLat(p.getLat());
		
		ref.setDescription(p.description);
		ref.getBliDimensions().clear();
		for(BliDimensionParameter dim: p.getDimensions()) {
			BliDimension d = bliService.get(dim.id);
			ref.addBliDimension(d);
		}
		
		ref.getTags().clear();
		
		for(KeyValuePairParameter kvpp:p.getTags()) {
			KeyValuePair kvp = new KeyValuePair(kvpp);
			if(kvp.getId() == null){
				kvp = kvpService.create(kvp);
			}
			ref.addTag(kvp);
		}

		ref.getDataGroups().clear();
		for(DataGroupParameter dgp:p.getDataGroups()) {
			if(dgp.id != null) {
				ref.addDataGroup(new DataGroup(dgp));
			}
		}

		for(DataGroupParameter dgp: p.getDatagroups()) {
			/**
			 * ToDo: Batchen.
			 */
			
			DataGroup dg = dataGroupService.get(dgp.id);
			if(dg != null) {
				ref.addDataGroup(dg);
			}
		}
		ref.getPeers().clear();
		for(OsmNodeParameter np: p.getPeers()) {
			/**
			 * ToDo: Batchen.
			 */
			if(np.id != null) {
				OsmReference peer = get(np.id);
				ref.addPeer(peer);
			}
		}
		return ref;
	}
	@Override
	public OsmReference create(OsmNodeParameter p) {
		OsmReference ref = new OsmReference();
		ref.setName(p.getName());
		ref.setOsmId(p.getOsmId());
		ref.setType(p.getOsmElementType());
		ref.setCreatedBy(userService.getCurrentUser());
		ref.setCreated(new Date());
		ref.setDescription(p.description);
		ref.setLon(p.getLon());
		ref.setLat(p.getLat());
		for(BliDimensionParameter dim: p.getDimensions()) {
			BliDimension d = bliService.get(dim.id);
			ref.addBliDimension(d);
		}
		

		for(KeyValuePairParameter kvpp:p.getTags()) {
			KeyValuePair kvp = new KeyValuePair(kvpp);
			if(kvp.getId() == null){
				kvp = kvpService.create(kvp);
			}
			ref.addTag(kvp);
		}

		for(DataGroupParameter dgp:p.getDataGroups()) {
			if(dgp.id != null) {
				ref.addDataGroup(new DataGroup(dgp));
			}
		}

		for(DataGroupParameter dgp: p.getDatagroups()) {
			/**
			 * ToDo: Batchen.
			 */
			
			DataGroup dg = dataGroupService.get(dgp.id);
			if(dg != null) {
				ref.addDataGroup(dg);
			}
		}

		for(OsmNodeParameter np: p.getPeers()) {

			if(np.getId() != null) {
				OsmReference peer = get(np.getId());
				if(peer != null) {
					ref.addPeer(peer);
				}else {
					System.out.println("Peer cannot be added. is null. "+np.getId()+"/"+np.getName());
				}
			}
		}
		return create(ref);
		
	}

	@Override
	@Transactional(readOnly = false)
	public void delete(OsmReference el) {
		for(OsmReference peer: el.getPeers()) {
			peer.removePeer(el);
			update(peer);
		}
		for(OsmReference peer: el.getPeers()) {
			update(peer);
		}
		dao.delete(el.getId());
		
	}

	
	
}
