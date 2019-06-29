package de.transformationsstadt.geoportal.api;

import java.util.HashMap;

/**
 * API-Teil, der den Unterpfad "/DataGroups/" (/geoportal/DataGroups/) abbildet.
 * 
 * DataGroups dienen der Zuordnung und Strukturierung von den im Geoportal befindlichen Daten. Datengruppen können rekursiv angelegt werden 
 * (d.h. es kann Unterdatengruppen existieren), sollten aber eine flache Hierarchie behalten.
 * 
 * Eine Datengruppe sollte letztendlich immer einer Kategorie zugeordnet werden.
 * 
 * Die Änderungen der Datengruppen (nicht der enthaltenden Elemente) ist privilegierten Benutzerkontexten vorbehalten.
 * 
 * 
 * TODO: Zyklen erkennen.
 * TODO: Kategorien über der Wurzeldatengruppe sicherstellen.
 * 
 * @author Sebastian Bruch
 *
 */
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
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
import de.transformationsstadt.geoportal.services.DataGroupService;
@Path("/DataGroups/")
public class DataGroups { 

	Subject subject;
	Logger logger;
	
	@Autowired
	DataGroupService datagroupService;
	
	@Context
	WebConfig configuration;
	@Context
	UriInfo uriInfo;
	public DataGroups() {
		this.logger = LoggerFactory.getLogger("geoportal.categories");
		this.subject = SecurityUtils.getSubject();
	}
	
	/**
	 * Ändert eine Datengruppe entsprechend der Parameter.
	 * 
	 * 
	 * @param id {@link Long}
	 * @param dg {@link DataGroup}
	 * @return
	 */
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/")
	public Response updateDataGroup(@PathParam("id") Long id, DataGroup dg) {
		HashMap<String,String> map=new HashMap<String,String>();
		
		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}else {
			if(!(subject.hasRole("redakteur")|| subject.hasRole("admin"))) {
				map.put("error","insufficient userlevel");
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}else {
				DataGroup currentDataGroup = datagroupService.get(id);
				if(currentDataGroup == null) {
					map.put("error","entity not found");
					return Response.status(Status.NOT_FOUND).entity(map).build();
				}
				DataGroup responseDG = datagroupService.update(currentDataGroup, dg);
				return Response.status(Status.OK).entity(responseDG).build();
			}
		}
	}
	
	/**
	 * Legt eine neue Datengruppe an
	 * @param dg {@link DataGroup} 
	 * @return {@link DataGroup} die angelegte Datengruppe.
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addDatagroup(DataGroup dg) {
		HashMap<String,String> map=new HashMap<String,String>();
		
		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}else {
			if(!(subject.hasRole("redakteur")|| subject.hasRole("admin"))) {
				map.put("error","insufficient userlevel");
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}else {
				DataGroup responseDG = datagroupService.create(dg);
				
				return Response.status(Status.OK).entity(responseDG).build();
			}
		}
	}
	
	
	/**
	 * Gibt alle Datengruppen zurück.
	 * 
	 * @return List<{@link DataGroup}> Die Datengruppen.
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/")
	public Response getAll() {
		
		return Response.status(Status.OK).entity(datagroupService.getAll()).build();
	}
	
	
}
