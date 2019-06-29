package de.transformationsstadt.geoportal.api;

import java.util.ArrayList;
import java.util.HashMap;

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

import antlr.collections.List;
import de.transformationsstadt.geoportal.DAO.CategoryDAO;
import de.transformationsstadt.geoportal.entities.BliDimension;
import de.transformationsstadt.geoportal.entities.Category;
import de.transformationsstadt.geoportal.entities.DataGroup;
import de.transformationsstadt.geoportal.services.CategoryService;


/**
 * API-Teil, der den Unterpfad "/Categories/" (/geoportal/Categories/) abbildet.
 * 
 * Bilden die oberste Hierarchieebene für DatenGruppen.
 * 
 * TODO: Prüfen, ob Kategorien nicht einfach als DataGroup-Objekte abgebildet werden können, die keine parent-Elemente haben. 
 * 
 * @author Sebastian Bruch
 *
 */
@Path("/Categories/")
public class Categories {
	@Autowired
	CategoryService categoryService;
	Subject subject;
	Logger logger;
	
	@Context
	WebConfig configuration;
	@Context
	UriInfo uriInfo;
	public Categories() {
		this.logger = LoggerFactory.getLogger("geoportal.categories");
		this.subject = SecurityUtils.getSubject();
	}
	
	/**
	 * Ändert eine Kategorie entsprechend der Parameter
	 * @param id {@link Long} id der Kategorie
	 * @param cat {@link Category} die neuen Daten der Kategorie
	 * @return {@link Category} die neue Kategorie.
	 */
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/")
	public Response updateCategory(@PathParam("id") Long id, Category cat) {
		HashMap<String,String> map=new HashMap<String,String>();
		
		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}else {
			if(!(subject.hasRole("redakteur")|| subject.hasRole("admin"))) {
				map.put("error","insufficient userlevel");
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}else {
				Category currentCat = categoryService.get(id);
				if(currentCat == null) {
					map.put("error","entity not found");
					return Response.status(Status.NOT_FOUND).entity(map).build();
				}
				Category responseCat = categoryService.update(currentCat, cat);
				return Response.status(Status.OK).entity(responseCat).build();
			}
		}
	}
	
	/**
	 * Liest die in der Kategorie befindlichen Datengruppen aus
	 * 
	 * @param id {@link Long}
	 * @return List<{@link DataGroup}> die Datengruppen
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/DataGroups/")
	public Response getDataGroups(@PathParam("id") Long id) {
		Category currentCat = categoryService.get(id);
		if(currentCat == null) {
			HashMap<String,String> map= new HashMap<String,String>();
			map.put("error","entity not found");
			return Response.status(Status.NOT_FOUND).entity(map).build();
		}

		return Response.status(Status.OK).entity(currentCat.getDataGroups()).build();
	}
	
	
	/**
	 * Legt eine neue Kategorie an.
	 * @param cat {@link Category}
	 * @return Die neue Kategorie
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addCategory(Category cat) {
		HashMap<String,String> map=new HashMap<String,String>();
		
		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}else {
			if(!(subject.hasRole("redakteur")|| subject.hasRole("admin"))) {
				map.put("error","insufficient userlevel");
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}else {
				Category responseCat = categoryService.create(cat);
				
				return Response.status(Status.OK).entity(responseCat).build();
			}
		}
	}

	/**
	 * Liest alle Kategorien (inklusive der Datengruppen, ohne die Elemente) aus.
	 * @return List<{@link Category}>
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/")
	public Response getAllFlat() {
		ArrayList<Category> list = categoryService.getAll();
		for(Category cat: list) {
			for(DataGroup dg: cat.getDataGroups()) {
				dg.removeElements();
			}
		}
		return Response.status(Status.OK).entity(list).build();
	}
	/**
	 * Liest alle Kategorien (inklusive der Datengruppen) aus.
	 * @return List<{@link Category}>
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/deep/")
	public Response getAll() {
		ArrayList<Category> list = categoryService.getAll();
		return Response.status(Status.OK).entity(list).build();
	}
	
	
}
