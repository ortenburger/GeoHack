package de.transformationsstadt.geoportal.DAO;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import de.transformationsstadt.geoportal.entities.DataGroup;
import de.transformationsstadt.geoportal.entities.OsmReference;

@Repository
public class DataGroupDAOImpl extends GenericDao<DataGroup> implements DataGroupDAO{
	
	public List<DataGroup> search(String searchString) {

		CriteriaBuilder b = session().getCriteriaBuilder();
		CriteriaQuery<DataGroup> cq = b.createQuery(DataGroup.class);
		Root<DataGroup> root = cq.from(DataGroup.class);
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

	
	
	public DataGroup update(DataGroup existing,DataGroup update) {
		existing.setName(update.getName());
		existing.setDescription(update.getDescription());
		update(existing);
		return existing;
	}

}