package de.transformationsstadt.geoportal.entities;

import java.util.List;

import de.transformationsstadt.geoportal.api.Search;


/**
 * Objekt in dem ein Suchergebnis (API-Pfad /search/, {@link Search}) bestehend aus List<{@link OsmReference}> und List<{@link DataGroup}> und zur Deserialisierung abgespeichert werden kann.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class SearchResult {
	public List<DataGroup> dataGroups;
	public List<OsmReference> geoElements;

}
