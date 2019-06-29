package de.transformationsstadt.geoportal.overpass;

import java.time.LocalDateTime;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import antlr.collections.List;
import de.transformationsstadt.geoportal.SpringContextProvider;
import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.OverpassQueryCacheElement;
import de.transformationsstadt.geoportal.services.OverpassQueryCacheService;
/**
 * Ermöglicht das (serverseitige) anreichern von {@link OsmReference}-Objekten aus der Overpass-API. 
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
@Component
public class Overpass {
	
	OverpassQueryCacheService _overpassCache;
	private OverpassQueryCacheService overpassCache() {
		if(this._overpassCache == null) {
			this._overpassCache = SpringContextProvider.getBean(OverpassQueryCacheService.class);
		}
		return _overpassCache;
	}
	public Overpass() {
	}
	private static final String OVERPASS_API = "http://www.overpass-api.de/";
	
	public String getCityName(OsmReference ref) {
		return this.getCityName(ref.getLat(),ref.getLon());
	}
	
	public String getCityName(Double lat,Double lon){

		String queryIdentifier = "cityname"+lat.toString()+""+lon.toString();
		OverpassQueryCacheElement cachedElement = overpassCache().get(queryIdentifier);
		//System.out.println("reading "+queryIdentifier);
		
		
		if(cachedElement != null) {
			//System.out.println("returning cached element");
			return cachedElement.getResult();
		}
		//System.out.println("element not in cache.");
		
		String queryString = "[out:json];(is_in("+lat+","+lon+");)->._;(area._[\"boundary\"=\"administrative\"][\"admin_level\"=\"6\"];);out meta qt;";
		Client client = ClientBuilder.newClient();
		OverpassResult result = null;
		
		try {
			result = client.target(OVERPASS_API).path("api/interpreter").queryParam("data", queryString).request(MediaType.APPLICATION_JSON).get(OverpassResult.class);
		}catch(BadRequestException bre) {
			//System.out.println("Overpass-error for query ["+queryString+"]: "+bre.getMessage());
			return "err";
		}
		
		String name=null;
		
		if(result != null && result.elements != null) {
			if(!result.elements.isEmpty()) {
				OverpassResultElement element = result.elements.get(0);
				
				if(element.tags != null) {
					for(Map.Entry<String, String> entry : element.tags.entrySet()) {
						if(entry.getKey() == "name") {
							name = entry.getValue();
						}
					}
				}
			}
		}
		
		if(name != null) {
			OverpassQueryCacheElement el = new OverpassQueryCacheElement();
			el.setQuery(queryIdentifier);
			el.setResult(name);
			//LocalDate expires = LocalDate.now().plus(2, ChronoUnit.WEEKS);
			LocalDateTime expires = LocalDateTime.now();
			expires = expires.plusSeconds(15);
			el.setExpires(expires);
			//System.out.println("writing to db: "+queryIdentifier);
			overpassCache().add(el);
			return name;
		}

		System.out.println("Overpass-error for query ["+queryString+"]: gave an empty result.");
		
		return "N/A";
	}
	/**
	 * Fragt zu einem Objekt vom Typ {@link OsmReference} die Overpass-API ab und reichert liest die Koordinaten dazu aus
	 * 
	 * Bisher sind Objekte vom Typ "node" und "way" vorgesehen.
	 * 
	 * @param ref
	 * @return {@link OverpassResult}
	 * @throws Exception
	 */
	public OverpassResult getDataForElement(OsmReference ref) throws Exception {
		String typeString = "";
		String queryString = "";
		
		
		if(ref.getType() == OsmElementType.NODE) {
			typeString="node";
			queryString = "[out:json];"+typeString+"("+Long.toString(ref.getOsmId())+");out body;";
		}
		if(ref.getType() == OsmElementType.WAY) {
			typeString="way";
			queryString = "[out:json];"+typeString+"("+Long.toString(ref.getOsmId())+");(._;>;);out body;";
		}
		if(queryString.isEmpty()) {
			throw new RuntimeException("querystring is empty.");
		}
		OverpassResult result = null;
		
		ObjectMapper objectMapper = new ObjectMapper();
		if(overpassCache() == null) {
			
			throw new RuntimeException("overpassCache was null.");
		}
		OverpassQueryCacheElement cacheElement = overpassCache().get(queryString);
		boolean cached = false;
		boolean shouldCache=true;
		if(cacheElement!=null) {
			try {
				result = objectMapper.readValue(cacheElement.getResult(),OverpassResult.class);
				cached=true;
				shouldCache=false;
			}catch (Exception e) {
				result = null;
			}
		}
		if(result==null) {
			Client client = ClientBuilder.newClient();
			try {
				result = client.target(OVERPASS_API).path("api/interpreter").queryParam("data", queryString).request(MediaType.APPLICATION_JSON).get(OverpassResult.class);
			}catch(BadRequestException bre) {
				throw new Exception("Got a Bad request with query ["+queryString+"]");
			}
		}
		
		if(cacheElement == null) {
			cacheElement = new OverpassQueryCacheElement();
			cacheElement.setQuery(queryString);
			try {
				cacheElement.setResult( objectMapper.writeValueAsString(result) );
			} catch (JsonProcessingException e) {
				shouldCache=false;
				throw new Exception("error processing json from overpass.");
			}
		}
		cacheElement.setCreated(LocalDateTime.now());
		if(shouldCache) {
			overpassCache().add(cacheElement);
		}
		if(cached) {
			//ref.setDescription("cached");
		}

		return result;

	}
	/**
	 * 
	 * @param ref
	 * @return
	 * @throws Exception
	 */
	public OsmReference completeElement(OsmReference ref) throws Exception {
		
		OverpassResult data = this.getDataForElement(ref);
		if(data == null) {
			//System.out.println("Data empty.");
		}
		Float lon=0f;
		Float lat=0f;
		Integer elements=0;
		for(OverpassResultElement element : data.elements) {
			if(element.lat != null && element.lon != null) {
				lon+=element.lon;
				lat+=element.lat;
				elements++;
			}
		}
		if(elements != 0) {
			lon=lon/elements;
			lat=lat/elements;
			ref.setLon((lon.doubleValue()));
			ref.setLat((lat.doubleValue()));
		}else {
			//System.out.println("no elements from overpass-api for "+ref.getName());
		}

		return ref;
	}
}
