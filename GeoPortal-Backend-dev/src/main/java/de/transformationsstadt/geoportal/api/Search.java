package de.transformationsstadt.geoportal.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.glassfish.jersey.servlet.WebConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import de.transformationsstadt.geoportal.DAO.DataGroupDAO;
import de.transformationsstadt.geoportal.entities.DataGroup;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.Rectangle;
import de.transformationsstadt.geoportal.entities.SearchResult;
import de.transformationsstadt.geoportal.overpass.Overpass;
import de.transformationsstadt.geoportal.services.DataGroupService;
import de.transformationsstadt.geoportal.services.OsmReferenceService;
import de.transformationsstadt.geoportal.services.OverpassQueryCacheService;

/**
 * 
 * API-Teil, der den Unterpfad "/search/" (/geoportal/search/) abbildet.
 * 
 * Durchsucht die im GeoPortal befindlichen Daten nach verschiedenen Kriterien 
 * (im Moment ein einfaches String-Matching auf Namen und Beschreibung die sich auf Bli-Dimensionen und Koordinaten einschränken lassen)
 * 
 * 
 * @author Sebastian Bruch <s.bruch@utopiastadt.eu>
 * 
 */

@Path("/search/")
public class Search {
	@Autowired
	OsmReferenceService osmRefService;
	@Autowired
	DataGroupService dataGroupService;
	
	Subject subject;
	Logger logger;

	@Context
	WebConfig configuration;
	@Context
	UriInfo uriInfo;
	public Search() {
		this.logger = LoggerFactory.getLogger("geoportal.search");
		this.subject = SecurityUtils.getSubject();
	}

	
	/**
	 * Sucht (case-insensitive) nach allen Einträgen, in deren Namen oder Beschreibung der übergebene String als Substring vorkommt.
	 * 
	 * Die Suche lässt sich durch die (optionale) Angabe von Koordinaten (lon/lat) auf ein Rechteck und BLI-Dimensionen einschränken.
	 *  
	 * @param query {@link String} der Suchstring
	 * @param dimensions Array<Int> Liste von IDs von BLI-Dimensionen (werden oder-verknüpft)
	 * @param minX unterer Längengrad des Rechtecks
	 * @param minY unterer Breitengrad des Rechtecks
	 * @param maxX oberer Längengrad des Rechtecks
	 * @param maxY oberer Breitengrad des Rechtecks
	 * @return {@link SearchResult}
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
	public Response search(		@QueryParam("q") String query,	@QueryParam("dimensions") final List<Long> dimensions,@QueryParam("minX") Double minX,@QueryParam("minY") Double minY,@QueryParam("maxX") Double maxX,@QueryParam("maxY") Double maxY) {
		if(query == null || query.isEmpty()) {
			return Response.status(Status.BAD_REQUEST).entity(null).build();
		}
		Rectangle rect = null;
		if (minX != null && maxX != null && minY != null || maxY != null) {
			rect = new Rectangle(minX,minY,maxX,maxY);
		}
		SearchResult result = new SearchResult();
		
		List<OsmReference> referenceResult = osmRefService.search(query,rect);
		List<DataGroup> datagroupResult = dataGroupService.search(query);
		
		if(dimensions != null && dimensions.size() > 0) {
			List<OsmReference> filteredResult = new ArrayList<OsmReference>();
			for(OsmReference ref: referenceResult) {
				boolean pass=false;
				for(Long dimId: dimensions) {
					if(ref.hasDimension(dimId)) {
						pass=true;
					}
				}
				if(pass) {
					filteredResult.add(ref);
				}
			}
			referenceResult = filteredResult;
		}
		
		result.dataGroups = datagroupResult;
		result.geoElements = referenceResult;

		return Response.status(Status.OK).entity(result).build();
	}
	
	@GET
	@Path("/autocomplete/")
	@Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
	public Response autocomplete(@QueryParam("q") String query, @QueryParam("maxResults") Integer maxResults,	@QueryParam("dimensions") final List<Long> dimensions,@QueryParam("minX") Double minX,@QueryParam("minY") Double minY,@QueryParam("maxX") Double maxX,@QueryParam("maxY") Double maxY) {
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		if(query == null || query.length() < 4) {
			map.put("error", "query-string has to have at least 4 characters.");
			return Response.status(Status.BAD_REQUEST).entity(map).build();
		}
		
		Overpass overpass = new Overpass();
		ArrayList<Object[]> list = (ArrayList<Object[]>) osmRefService.autocompleteOsmReferences(query,maxResults);
		for(Object[] ref: list) {
			String name = ref[0].toString();
			Double lon = new Double(ref[1].toString());
			Double lat = new Double(ref[2].toString());
			
			map.put(name,overpass.getCityName(lat,lon));
		}
		return Response.status(Status.OK).entity(map).build();
	}	
}
