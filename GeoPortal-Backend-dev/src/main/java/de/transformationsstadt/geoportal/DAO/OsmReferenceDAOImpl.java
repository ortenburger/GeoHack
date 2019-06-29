package de.transformationsstadt.geoportal.DAO;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import de.transformationsstadt.geoportal.apiparameters.OsmNodeParameter;
import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.Rectangle;


@Repository
public class OsmReferenceDAOImpl extends GenericDao<OsmReference> implements OsmReferenceDAO{
	
	@Transactional(readOnly=false)
	@Modifying
	@Override
	public Serializable create(OsmReference ref) {
		//System.out.println("Saving "+ ref.getName() + " to database.");
		long id = (long) super.create(ref);
		return id;
		
	}
	@Transactional(readOnly=true)
	public OsmReference getOsmReferenceById(Long id) {
		return get(id);
	}
	@Transactional(readOnly=true)
	public OsmReference getFirstOsmReferenceByOsmId(long id, String type) {
		
		CriteriaBuilder builder = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = builder.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		
		Predicate andClause = 
				builder.and(
						builder.equal(root.get("type"),OsmElementType.fromString(type)),
						builder.equal(root.get("osmId"),id)
						);
				
		cq.select(root).where(andClause);
		
		List<OsmReference> results = session().createQuery(cq).setMaxResults(1).getResultList();
		if(results.size() == 1) {
			return results.get(0);
		}else {
			return null;
		}
	}
	
	
	
	// das ist eine liste, da osm-ids nicht eindeutig sind (i.E. eine Id kann von Nodes, Ways usw. belegt sein)
	@Transactional(readOnly=true)
	public List<OsmReference> getOsmReferencesByOsmId(Long id, String type) {
		CriteriaBuilder builder = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = builder.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		
		Predicate andClause = 
				builder.and(
						builder.equal(root.get("type"),OsmElementType.fromString(type)),
						builder.equal(root.get("osmId"),id)
						);
				
		cq.select(root).where(andClause);
		return session().createQuery(cq).getResultList();
	}

	// das ist eine liste, da osm-ids nicht eindeutig sind (i.E. eine Id kann von Nodes, Ways usw. belegt sein)
	@Transactional(readOnly=true)
	public OsmReference getOsmReferencesByOsmNodeParameter(OsmNodeParameter ref) {

		CriteriaBuilder builder = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = builder.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		
		Predicate andClause = 
				builder.and(
						builder.equal(root.get("type"),OsmElementType.fromString(ref.getOsmType())),
						builder.equal(root.get("osmId"),ref.getOsmId())
						);
				
		cq.select(root).where(andClause);
		return session().createQuery(cq).setMaxResults(1).getSingleResult();
	}
	@Transactional(readOnly=true)
	
	public List<OsmReference> search(String searchString,Rectangle rect) {

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = b.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		cq = cq.select(root);
		
		if(rect != null) {
			Predicate inRect = 
					b.and(
					b.and(
							b.ge(root.get("lon"), rect.getMinX()),
							b.le(root.get("lon"), rect.getMaxX())
						),
					b.and(
							b.ge(root.get("lat"), rect.getMinY()),
							b.le(root.get("lat"), rect.getMaxY())
						)
					);
			cq = cq.where(inRect);
		}
		
		cq = cq.where(
						b.or(
								b.like(b.lower(root.get("description")), ("%"+searchString.toLowerCase())+"%"),
								b.like(b.lower(root.get("name")),("%"+searchString.toLowerCase()+"%")
							)
						)
					);
		
		return session().createQuery(cq).getResultList();
		
	}
	@Transactional(readOnly=true)
	public List<OsmReference> search(String searchString) {

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = b.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		cq = cq.select(root);
		
		
		cq = cq.where(
						b.or(
								b.like(b.lower(root.get("description")), ("%"+searchString.toLowerCase())+"%"),
								b.like(b.lower(root.get("name")),("%"+searchString.toLowerCase()+"%")
							)
						)
					);
		
		return session().createQuery(cq).getResultList();
		
	}
	
	@Transactional(readOnly=false)
	@Modifying
	public boolean removePeer(Long lhsId,Long rhsId) {
		/*
		Session session = HibernateUtil.getCurrentSession();
		boolean success=false;
		session.getTransaction().begin();
		try {
			OsmReference lhs=(OsmReference)session.get(OsmReference.class, lhsId);
			OsmReference rhs=(OsmReference)session.get(OsmReference.class, rhsId);
			if(lhs != null && rhs != null) {
				rhs.getPeers().remove(lhs);
				lhs.getPeers().remove(rhs);
				session.merge(lhs);
				session.flush();
				success=true;
			}
			session.getTransaction().commit();

		}catch(NoResultException e){
			session.getTransaction().rollback();
		}
		return success;
		*/
		return false;
	}


	@Transactional(readOnly=false)
	@Modifying
	public void removeElement(Long id) {
		delete(id);
	}

	@Transactional(readOnly=false)
	@Modifying
	public void removeElement(OsmReference ref) {
		removeElement(ref.getId());
	}




	@Transactional(readOnly=true)
	public List<OsmReference> getByBoundingBox(Rectangle rect) {

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = b.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		cq = cq.select(root);
	
		Predicate inRect = 
				b.and(
				b.and(
						b.ge(root.get("lon"), rect.getMinX()),
						b.le(root.get("lon"), rect.getMaxX())
					),
				b.and(
						b.ge(root.get("lat"), rect.getMinY()),
						b.le(root.get("lat"), rect.getMaxY())
					)
				);
		cq = cq.where(inRect);
		
		return session().createQuery(cq).getResultList();
		
	}
	
	public ArrayList<String> autocomplete(String searchString,Integer _maxResults) {
		Integer maxResults=5;
		if(_maxResults != null) {
			maxResults = _maxResults;
		}
		if(maxResults > 30) {
			maxResults=30;
		}

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<OsmReference> cq = b.createQuery(OsmReference.class);
		Root<OsmReference> root = cq.from(OsmReference.class);
		cq = cq.select(root);
		
		
		cq = cq.where(
						b.or(
								b.like(b.lower(root.get("description")), ("%"+searchString.toLowerCase())+"%"),
								b.like(b.lower(root.get("name")),("%"+searchString.toLowerCase()+"%")
							)
						)
					);
		ArrayList<String> strList = new ArrayList<String>();
		ArrayList<OsmReference> list = (ArrayList<OsmReference>) session().createQuery(cq).setMaxResults(maxResults).getResultList();
		for(OsmReference ref : list) {
			strList.add(ref.getName());
		}
		return strList;
	}
	@Transactional(readOnly=true)
	public ArrayList<Object[]> autocompleteOsmReferences(String searchString,Integer _maxResults) {
		Integer maxResults=5;
		if(_maxResults != null) {
			maxResults = _maxResults;
		}
		if(maxResults > 30) {
			maxResults=30;
		}

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = b.createTupleQuery();
		Root<OsmReference> root = cq.from(OsmReference.class);
		cq.multiselect(root.get("name"),root.get("lon"),root.get("lat"));
		
		
		cq = cq.where(
						b.or(
								b.like(b.lower(root.get("description")), ("%"+searchString.toLowerCase())+"%"),
								b.like(b.lower(root.get("name")),("%"+searchString.toLowerCase()+"%")
							)
						)
					);
		ArrayList<Tuple> tuples = (ArrayList<Tuple>) session().createQuery(cq).setMaxResults(maxResults).getResultList();
		ArrayList<Object[]> objects = new ArrayList<Object[]>();
		for(Tuple tupel: tuples) {
			objects.add(new Object[] { (Object) tupel.get(0),(Object) tupel.get(1), (Object) tupel.get(2) } );
		}
		
		return objects;
	}
}
