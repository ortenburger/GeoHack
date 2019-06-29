package de.transformationsstadt.geoportal.apiparameters;


/**
 * Objekt, in das Objekte vom Typ DataGroup deserialisiert werden.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id",scope = DataGroupParameter.class)
public class DataGroupParameter {
	public Long id;
	public String name;
	@JsonIgnore
	public List<DataGroupParameter> dataGroups = new ArrayList<DataGroupParameter>();
}
