package de.transformationsstadt.geoportal.api;

import java.util.ArrayList;
import java.util.HashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.springframework.beans.factory.annotation.Autowired;

import antlr.collections.List;
import de.transformationsstadt.geoportal.DAO.OsmReferenceDAO;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.overpass.Overpass;
import de.transformationsstadt.geoportal.services.OsmReferenceService;
import de.transformationsstadt.geoportal.services.OverpassQueryCacheService;
@Path("/check/")
public class DBCheck {
	
	@Autowired
	OsmReferenceService osmRefService;
	
	
	protected ArrayList<String> blacklist;
	public DBCheck() {
		ArrayList<String> blacklist = new ArrayList<String>();
		blacklist.add("name");
		blacklist.add("source");
		blacklist.add("addr:country");
		blacklist.add("addr:street");
		blacklist.add("addr:housenumber");
		blacklist.add("addr:postcode");
		blacklist.add("addr:city");
		blacklist.add("email");
		blacklist.add("opening_hours");
		blacklist.add("start_date");
		this.blacklist = blacklist;
	}
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/peers/")
	public Response runPeerTest() {
		HashMap<String,String> map = new HashMap<String,String>();
		ArrayList<OsmReference> list = (ArrayList<OsmReference>) osmRefService.getAll();
		for(OsmReference i:list) {
			for(OsmReference iPeer : i.getPeers()) {
				if(!iPeer.getPeers().contains(i)) {
					map.put(("error "+i.getId()+"/"+iPeer.getId()),(i.getId() + " has " + iPeer.getId() + " as peer but " + iPeer.getId() + " not " +i.getId()));
				}
			}
		}
		return Response.status(Status.OK).entity(map).build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/osmType/")
	public Response checkOSMType() {
		ArrayList<OsmReference> list = (ArrayList<OsmReference>) osmRefService.getAll();
		ArrayList<OsmReference> defects = new ArrayList<OsmReference>();
		for(OsmReference el: list) {
			if(el.getType() == null) {
				defects.add(el);
			}
		}
		return Response.status(Status.OK).entity(defects).build();
	}
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/osmType/delete")
	public Response fixOsmType() {
		ArrayList<OsmReference> list = (ArrayList<OsmReference>) osmRefService.getAll();
		ArrayList<OsmReference> defects = new ArrayList<OsmReference>();
		for(OsmReference el: list) {
			if(el.getType() == null) {
				defects.add(el);
			}
		}
		for(OsmReference el: defects) {
			osmRefService.delete(el);		
		}
		return Response.status(Status.OK).entity(defects).build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/LonLat/")
	public Response runLonLatTest() {
		ArrayList<OsmReference> list = (ArrayList<OsmReference>) osmRefService.getAll();
		ArrayList<OsmReference> defects = new ArrayList<OsmReference>();
		for(OsmReference el: list) {
			if(el.getLon() == null || el.getLat() == null) {
				defects.add(el);
			}
			if(el.getLon() == 0 || el.getLat() == 0) {
				defects.add(el);
			}
			if(el.getLon() == -1 || el.getLat() == -1) {
				defects.add(el);
			}
			if(el.getLon().isNaN() || el.getLat().isNaN()) {
				defects.add(el);
			}
		}
		return Response.status(Status.OK).entity(defects).build();
	}
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tags")
	public Response checkTags() {
		ArrayList<OsmReference> refList = (ArrayList<OsmReference>) osmRefService.getAll();
		ArrayList<OsmReference> violators = new ArrayList<OsmReference>();
		
		for(OsmReference el: refList) {
			for(String str : this.blacklist) {
				if(el.hasTag(str)) {
					violators.add(el);
				}
			}
		}
		return Response.status(Status.OK).entity(violators).build();
	}
	
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("tags/fix")
	public Response fixTags() {
		ArrayList<OsmReference> refList = (ArrayList<OsmReference>) osmRefService.getAll();
		ArrayList<OsmReference> violators = new ArrayList<OsmReference>();
		
		for(OsmReference el: refList) {
			boolean changed=false;
			for(String str : this.blacklist) {
				if(el.hasTag(str)) {
					changed |= el.removeTag(str);
				}
			}
			if(changed) {
				osmRefService.update(el);
			}
		}
		return Response.status(Status.OK).entity("{\"op\":\"tags/fix\"}").build();
	}

	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/LonLat/delete/")
	public Response runLonLatDelete(){

		ArrayList<OsmReference> list = (ArrayList<OsmReference>) osmRefService.getAll();
		
		for(OsmReference el: list) {
			boolean delete = false;
			if(el.getLon() == null || el.getLat() == null) {
				delete = true;
			}
			if(el.getLon() == 0 || el.getLat() == 0) {
				delete = true;
			}
			if(el.getLon() == -1 || el.getLat() == -1) {
				delete = true;
			}
			if(el.getLon().isNaN() || el.getLat().isNaN()) {
				delete = true;
			}
			if(delete) {
				try {
					osmRefService.delete(el);
				}catch(Exception e) {
					System.out.println("Error deleting: "+e.getMessage());
				}

			}
		}
		return Response.status(Status.OK).entity("done").build();
	}
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/LonLat/fix/")
	public Response runLonLatFix(){

		ArrayList<OsmReference> list = (ArrayList<OsmReference>) osmRefService.getAll();
		ArrayList<OsmReference> defects = new ArrayList<OsmReference>();
		for(OsmReference el: list) {
			if(el.getLon() == null || el.getLat() == null) {
				defects.add(el);
			}
			if(el.getLon() == 0 || el.getLat() == 0) {
				defects.add(el);
			}
			if(el.getLon() == -1 || el.getLat() == -1) {
				defects.add(el);
			}
			if(el.getLon().isNaN() || el.getLat().isNaN()) {
				defects.add(el);
			}
		}
		if(defects.isEmpty()) {
			return Response.status(Status.OK).entity(defects).build();
		}
		Overpass op = new Overpass();
		for(OsmReference el: defects) {
			try {
				System.out.println("Fixing lon/lat for "+el.getName());
				OsmReference ref = op.completeElement(el);
				System.out.println("saving fix for "+el.getName());
				osmRefService.update(ref);
				System.out.println("done fixing "+el.getName());
			}catch(Exception e) {
				System.out.println("Error fixing :"+e.getMessage());
			}
			
		}
		return Response.status(Status.OK).entity(defects).build();
	}
}
