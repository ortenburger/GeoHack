package de.transformationsstadt.geoportal.api;

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

import de.transformationsstadt.geoportal.DAO.BliDimensionDAO;
import de.transformationsstadt.geoportal.entities.BliDimension;
import de.transformationsstadt.geoportal.services.BliDimensionService;

/**
 * API-Teil, der den Unterpfad "/BliDimensions/" (/geoportal/BliDimensions/) abbildet.
 * 
 * Die BLI-Dimensionen (Better-Life-Index, siehe ( http://www.oecdbetterlifeindex.org/de/ ) ) sollen allen im GeoPortal befindlichen Daten angehangen werden.
 * Die Operanden sind Objekte des Typs {@link BliDimension} und im wesentlichen ein gesondert behandelter Satz von Schlagworten
 * 
 * Da die BLI-Dimensionen ein integraler Bestandteil des Konzeptes des GeoPortals sind, sollen diese nur von Administratoren verändert werden können.
 * 
 * TODO: Eine verlinkbare Erklärung inwiefern die hier genutzte Definition des Better-Life-Index von der der OECD abweicht. 
 * 
 * @author Sebastian Bruch
 *
 */
@Path("/BliDimensions/")
public class BliDimensions {

	Subject subject;
	Logger logger;
	
	@Context
	WebConfig configuration;
	@Context
	UriInfo uriInfo;
	
	@Autowired
	BliDimensionService bliService;
	
	public BliDimensions() {
		this.logger = LoggerFactory.getLogger("geoportal.blidimensions");
		this.subject = SecurityUtils.getSubject();
	}
	
	/**
	 * Aktualisiert die Informationen der gegebenen BLI-Dimension (also "name" oder "description") entsprechend dem übergebenen Objekt
	 * 
	 * @param id {@link Long}
	 * @param dim {@link BliDimension}
	 * @return BliDimension 
	 */
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id: [0-9]+}/")
	public Response updateBliDimension(@PathParam("id") Long id, BliDimension dim) {
		HashMap<String,String> map=new HashMap<String,String>();
		
		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}else {
			if(!(subject.hasRole("redakteur")|| subject.hasRole("admin"))) {
				map.put("error","insufficient userlevel");
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}else {
				
				BliDimension currentDim = bliService.get(id);
				if(currentDim == null) {
					map.put("error","entity not found");
					return Response.status(Status.NOT_FOUND).entity(map).build();
				}
				BliDimension responseDim = bliService.update(currentDim, dim);
				return Response.status(Status.OK).entity(responseDim).build();
			}
		}
	}
	
	/**
	 * Fügt eine neue BLI-Dimension hinzu.
	 * 
	 * @param dim {@link BliDimension}
	 * @return {@link BliDimension} die neue Bli-Dimension
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addBliDimension(BliDimension dim) {
		HashMap<String,String> map=new HashMap<String,String>();
		
		if(!subject.isAuthenticated()) {
			map.put("error","not authorized");
			return Response.status(Status.UNAUTHORIZED).entity(map).build();
		}else {
			if(!(subject.hasRole("redakteur")|| subject.hasRole("admin"))) {
				map.put("error","insufficient userlevel");
				return Response.status(Status.FORBIDDEN).entity(map).build();
			}else {
				BliDimension responseDim = bliService.create(dim);
				
				return Response.status(Status.OK).entity(responseDim).build();
			}
		}
	}
	
	/**
	 * Gibt alle BLIDimensionen zurück.
	 * Da die BLI-Dimensionen in den meisten (mir) bekannten Anwendungsfällen nicht einzeln benötigt werden und die Datenmenge nicht groß ist, gibt es keinen Pfad, sie einzeln abzurufen.
	 * 
	 * 
	 * @return List<BliDimension> Alle Bli-Dimensionen in der Datenbank
	 */
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/all/")
	public Response getAll() {
		
		return Response.status(Status.OK).entity(bliService.getAll()).build();
		
	}
	
	
}
