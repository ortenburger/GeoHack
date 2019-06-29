package de.transformationsstadt.geoportal.api;

import java.time.LocalDateTime;

/**
 * 
 * API-Teil, der den Unterpfad "/GeoElements/" (/geoportal/GeoElements/) abbildet.
 * 
 * Die Operanden sind Objekte des Typs OsmReference und (optimalerweise, momentan aus organisatorischen Gründen aber nicht zwingend) mit BLI-Dimensionen
 * versehene und in Datengruppen sortierte, auf Elemente aus OpenStreetMap referenzierende (also Geo-verortete) Objekte, die Akteure, Institutionen, Unternehmen
 * usw. darstellen. Diese Objekte bilden außerdem eine "Netzwerkpartnerschaft" (peers) ab, die eine (momentan nicht weiter qualifizierte oder quantifizierte) Zusammenarbeit bedeutet.
 * 
 * 
 * @author Sebastian Bruch <s.bruch@utopiastadt.eu>
 * 
 */

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.transformationsstadt.geoportal.DAO.KeyValuePairDAO;
import de.transformationsstadt.geoportal.DAO.OsmReferenceDAO;
import de.transformationsstadt.geoportal.DAO.UserDAO;
import de.transformationsstadt.geoportal.apiparameters.KeyValuePairParameter;
import de.transformationsstadt.geoportal.apiparameters.OsmNodeParameter;
import de.transformationsstadt.geoportal.entities.KeyValuePair;
import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.Rectangle;
import de.transformationsstadt.geoportal.services.KeyValuePairService;
import de.transformationsstadt.geoportal.services.OsmReferenceService;
import de.transformationsstadt.geoportal.services.UserService;

@Path("/GeoElements/")
public class GeoElements {

	@Autowired
	OsmReferenceService osmRefService;

	@Autowired
	UserService userService;
	
	@Autowired
	KeyValuePairService kvpService;
	
	Subject subject;
	Logger logger;
	public GeoElements() {
		this.logger = LoggerFactory.getLogger("geoportal.accounts");
		//subject = SecurityUtils.getSubject();
	}
	/**
	 * Gibt ein Objekt vom Typ {@link OsmReference} zurück, welches über die Id und den Typ  
	 *
	 * @param id Die id (in OSM) des adressierten Elementes
	 * @param type Der Typ des adressierten Elementes (siehe {@link OsmElementType}
	 * @return {@link OsmReference}
	 */
	@GET
	@Path("/byOsmId/{id: [0-9]+}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getElementByOsmId(@PathParam("id") Long id, @QueryParam("type") String type) {
		List<OsmReference> elements = osmRefService.getOsmReferencesByOsmId(id,type);
		OsmReference element = null;
		if(elements.size() == 1) {
			element = elements.get(0);
		}else {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "request not unique due to missing ?type= (NODE,WAY,...)");
			return Response.status(Status.CONFLICT).entity(map).build();
		}
		if(element == null) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}else {
			return Response.status(Status.OK).entity(element).build();
		}
	}
	@GET
	@Path("/byOsmId/nodes/{id: [0-9]+}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNodeByOsmId(@PathParam("id") Long id) {
		OsmReference element = osmRefService.getFirstOsmReferenceByOsmId(id,"node");
		if(element == null) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}else {
			return Response.status(Status.OK).entity(element).build();
		}
	}
	
	@GET
	@Path("/byOsmId/ways/{id: [0-9]+}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getWayByOsmId(@PathParam("id") Long id) {
		OsmReference element = osmRefService.getFirstOsmReferenceByOsmId(id,"node");
		if(element == null) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}else {
			return Response.status(Status.OK).entity(element).build();
		}
	}
	/**
	 * Gibt das {@link OsmReference}-Objekt mit der entsprechenden (lokalen) id zurück.
	 * @param id Long
	 * @return {@link OsmReference}
	 */
	@GET
	@Path("/{id: [0-9]+}/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getElementById(@PathParam("id") Long id) {
		OsmReference element = osmRefService.get(id);
		if(element == null) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}else {
			return Response.status(Status.OK).entity(element).build();
		}
	}

	/**
	 * Gibt alle Elemente zurück, die im Rechteck (minX,maxX) x (minY,maxY) liegen.
	 *  
	 * @param minX unterer Längengrad des Rechtecks
	 * @param minY unterer Breitengrad des Rechtecks
	 * @param maxX oberer Längengrad des Rechtecks
	 * @param maxY oberer Breitengrad des Rechtecks
	 * @return List<OsmReference>
	 */
	@GET
	@Path("/byBoundingBox/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getElementsByBoundingBox(@QueryParam("minX") Double minX,@QueryParam("minY") Double minY,@QueryParam("maxX") Double maxX,@QueryParam("maxY") Double maxY) {

		if(minX == null || maxX == null || minY == null || maxY == null) {
			return Response.status(Status.BAD_REQUEST).entity(null).build();
		}

		Rectangle rect = new Rectangle(minX,minY,maxX,maxY);
		List<OsmReference> list = osmRefService.getByBoundingBox(rect);
		return Response.status(Status.OK).entity(list).build();
	}



	/**
	 * Gibt alle tags ( vom Typ {@link KeyValuePair} ) zu dem Element mit der entsprechenden (lokalen) Id zurück
	 * @param id Die Id des adressierten Elementes
	 * @return List<KeyValuePair>
	 */
	@GET
	@Path("/{id: [0-9]+}/tags/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getElementTags(@PathParam("id") Long id) {
		OsmReference element = osmRefService.get(id);
		if(element == null) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}else {
			return Response.status(Status.OK).entity(element.getTags()).build();
		}
	}

	/*
	 * Entfernt das Tag ( vom Typ  {@link KeyValuePair} ) mit id "tagId" aus der Liste der mit dem "elementId" adressierten OsmReference-Objekt.
	 *   
	 * @param elementId Die Id der adressierten OsmReference
	 * @param tagId Die Id des adressierten KeyValuePair
	 * @return List<KeyValuePair> die nach der operation verbleibenden Tags
	 */
	@DELETE
	@Path("/{elementId: [0-9]+}/tags/{tagId: [0-9]+}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response removeTagFromElement(@PathParam("elementId") Long elementId,@PathParam("tagId") Long tagId) {
		OsmReference element = osmRefService.get(elementId);

		if(element == null) {
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("error", "not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}else {
			KeyValuePair kvp = kvpService.get(tagId);
			if(kvp == null) {
				return Response.status(Status.NOT_FOUND).entity("{\"error\":\"tag does not exist\"}").build();
			}

			if(!element.hasTag(kvp)) {
				return Response.status(Status.NOT_FOUND).entity("{\"error\":\"tag not found on element\"}").build();
			}else {
				element.removeTag(kvp);
				osmRefService.update(element);
			}
			return Response.status(Status.OK).entity(element.getTags()).build();
		}
	}



	/**
	 * Legt ein neues Objekt vom Typ {@link OsmReference} an.
	 * 
	 * @param osmnode {@link OsmNodeParameter}
	 * @return {@link OsmReference}
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addItem(OsmNodeParameter osmnode) {
		System.out.println("Lon: "+osmnode.getLon());
		OsmReference node = osmRefService.getFirstOsmReferenceByOsmId(osmnode.getOsmId(), osmnode.getOsmType());
		if(node == null) {
			node = osmRefService.create(osmnode);
			for(OsmReference peer:node.getPeers()) {
				peer.addPeer(node);
				osmRefService.update(peer);
			}
		}else {
			return Response.status(Status.CONFLICT).entity(node).build();
		}
	
		return Response.status(Status.OK).entity(node).build();
	}

	/**
	 * Fügt dem mit "id" referenzierten Element vom Typ ( {@link OsmReference} ) einen Netzwerkpartner hinzu.
	 * 
	 * Peers ist dabei eine Liste von {@link KeyValuePair} im Format { osmId : osmType }.
	 * Momentan muss das referenzierte Element in der Datenbank bekannt sein.
	 * 
	 * TODO: Netzwerkpartner anlegen, wenn er in der Datenbank nicht bekannt ist.
	 * 
	 * @param id 
	 * @param peers 
	 * @return {@link OsmReference} Das veränderte Element inklusive der neuen Netzwerkpartner.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/addPeer/")
	public Response makePeer(@PathParam("id") Long id, ArrayList<KeyValuePairParameter> peers) {
		OsmReference lhs = osmRefService.get(id);
		HashMap<String,String> map = new HashMap<String,String>();
		if(lhs==null) {
			map.put("error", "no element with id "+Long.toString(id));
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}
		
		Long osmId=null;
		String osmType="";
		
		for(KeyValuePairParameter peer:peers) {
			osmId=Long.parseLong(peer.key);
			osmType=peer.value;
			if( OsmElementType.fromString(osmType) == OsmElementType.UNSET) {
				map.put("error", "osm_type not set.");
				return Response.status(Status.BAD_REQUEST).entity(map).build();
			}
			if(peer.key == null) {
				map.put("error", "id not found.");
				return Response.status(Status.NOT_FOUND).entity(map).build();
			}
		}

		for(KeyValuePairParameter peer: peers){
			osmId=Long.parseLong(peer.key);
			osmType=peer.value;
			ArrayList<OsmReference> tmp = (ArrayList<OsmReference>)osmRefService.getOsmReferencesByOsmId(osmId, osmType);
			for(OsmReference ref: tmp) {
				if(ref.getId().equals(id)){
					map.put("error", "cannot peer up an element with itself.");
					return Response.status(Status.BAD_REQUEST).entity(map).build();
				}
				
				if(!lhs.hasPeer(ref)) {
					lhs.addPeer(ref);
				}
				if(!ref.hasPeer(lhs)) {
					ref.addPeer(lhs);
					osmRefService.update(ref);
				}

			}

		}
		osmRefService.update(lhs);
		return Response.status(Status.OK).entity(lhs).build();
	}

	/**
	 * Entfernt eine Netzwerkpartnerschaft zwischen den beiden adressierten Elementen.
	 * 
	 * @deprecated
	 * 
	 * @param id
	 * @param peerId
	 * @return {@link OsmReference} Das veränderte Element inklusive der neuen Netzwerkpartner.
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/peer/{peerId: [0-9]+}")
	public Response removePeer(@PathParam("id") Long id, @PathParam("peerId") Long peerId) {
		
		OsmReference element = osmRefService.get(id);
		if(element == null) {
			return Response.status(Status.NOT_FOUND).entity("{\"error\":\"element with id "+id+" not found\"}").build();
		}
		if(!element.hasPeer(peerId)) {
			return Response.status(Status.NOT_FOUND).entity("{\"error\":\"element "+peerId+" not peer of "+id+"\"}").build();
		}
		OsmReference peer = osmRefService.get(peerId);
		if(peer == null) {
			return Response.status(Status.NOT_FOUND).entity("{\"error\":\"element with id "+peerId+" not found\"}").build();
		}
		
		
		element.removePeer(peer);
		peer.removePeer(element);
		osmRefService.update(element);
		osmRefService.update(peer);
		return Response.status(Status.OK).entity(element).build();
	}


	/**
	 * Entfernt eine Netzwerkpartnerschaft zwischen den beiden adressierten Elementen.
	 * 
	 * @param id 
	 * @param peerId
	 * @return {@link OsmReference} Das veränderte (durch die erste Id adressierte) Element inklusive der neuen Netzwerkpartner.
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/peers/{peerId: [0-9]+}")
	public Response removePeerById(@PathParam("id") Long id, @PathParam("peerId") Long peerId) {
		OsmReference lhs = osmRefService.get(id);
		OsmReference rhs = osmRefService.get(peerId);
		if(lhs == null) {
			return Response.status(Status.NOT_FOUND).entity("{\"lhs found\":\""+Long.toString(id)+"\"}").build();
		}
		if(rhs == null) {
			return Response.status(Status.NOT_FOUND).entity("{\"rhs found\":\""+Long.toString(peerId)+"\"}").build();
		}
		if(!lhs.hasPeer(rhs)) {
			return Response.status(Status.NOT_FOUND).entity("{\"not in peer-list\":\""+Long.toString(peerId)+"\"}").build();
		}
		osmRefService.removePeer(id,peerId);
		return Response.status(Status.NOT_FOUND).entity(lhs).build();
	}


	/**
	 * Löscht ein Element
	 * 
	 * @param id 
	 * @return null
	 */
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/")
	public Response removeELement(@PathParam("id") Long id) {
		if(osmRefService.get(id) != null) {
			osmRefService.removeElement(id);
			return Response.status(Status.OK).entity(null).build();
		}else {
			return Response.status(Status.NOT_FOUND).entity("{\"error\":\"not found\"}").build();
		}

	}

	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/")
	public Response updateItem(@PathParam("id") Long id,OsmNodeParameter osmnode) {
		if(id == null) {
			return Response.status(Status.BAD_REQUEST).entity("{error:\"Path-Parameter \"id\" not given\"}").build();
		}
		
		Subject subject = SecurityUtils.getSubject();
		System.out.println((String)subject.getPrincipal());
		OsmReference existingNode = osmRefService.get(id);
		if(existingNode == null) {
			return Response.status(Status.NOT_FOUND).entity("{error:\"I know of no item with this id\"}").build();
		}else {
			OsmReference node = osmRefService.update(existingNode, osmnode);
			if(node != null) {
				return Response.status(Status.OK).entity(node).build();
			}else {
				return Response.status(Status.BAD_REQUEST).entity(null).build();
			}
		}
	}



	/**
	 * Gibt alle (!) Elemente aus der Datenbank zurück.
	 * 
	 * TODO: Für produktiven Einsatz sperren.
	 * @return List<OsmReference>
	 */
	@GET
	@Path("/all/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getAll() {
		ArrayList<OsmReference> elements = (ArrayList<OsmReference>) osmRefService.getAll();
		return Response.status(Status.OK).entity(elements).build();
	}


	/**
	 * Gibt die EdgeList zurück
	 * @param header
	 * @param response
	 * @param showIsolated
	 * @return
	 */
	@GET
	@Path("/EdgeList/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public Response getEdgeList(@Context HttpHeaders header, @Context HttpServletResponse response, @PathParam("showIsolated") String showIsolated)
	{
		response.setHeader("Content-Disposition","attachment; filename=\"edgelist.csv\"");
		List<KeyValuePair> pairs = new ArrayList<KeyValuePair>();
		ArrayList<OsmReference> elements = (ArrayList<OsmReference>) osmRefService.getAll();
		for(OsmReference ref: elements) {
			if(ref.getPeers().size() == 0) {
				pairs.add(new KeyValuePair(ref.getName(),""));
			}
			for(OsmReference peer: ref.getPeers()) {
				KeyValuePair kvp = new KeyValuePair(ref.getName(),peer.getName());
				pairs.add(kvp);
			}
		}
		String result ="";
		for(KeyValuePair kvp: pairs) {
			if(kvp.getValue().isEmpty()) {
				if(showIsolated != null && showIsolated == "true")
					result += kvp.getKey()+"\r\n";
			}else {
				result += kvp.getKey()+";"+kvp.getValue()+"\r\n";
			}
		}

		return Response.status(Status.OK).entity(result).build();
	}
}
