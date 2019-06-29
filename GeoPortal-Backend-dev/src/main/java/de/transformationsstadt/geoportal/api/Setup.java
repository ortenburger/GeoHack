package de.transformationsstadt.geoportal.api;


//import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;

//import javax.crypto.spec.SecretKeySpec;
import javax.persistence.PersistenceException;
//import javax.security.auth.login.AccountLockedException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.TransientObjectException;
import org.springframework.beans.factory.annotation.Autowired;

import de.transformationsstadt.geoportal.entities.BliDimension;
import de.transformationsstadt.geoportal.entities.Category;
import de.transformationsstadt.geoportal.entities.DataGroup;
import de.transformationsstadt.geoportal.entities.KeyValuePair;
import de.transformationsstadt.geoportal.entities.Permission;
import de.transformationsstadt.geoportal.entities.Role;
import de.transformationsstadt.geoportal.entities.User;
import de.transformationsstadt.geoportal.services.BliDimensionService;
import de.transformationsstadt.geoportal.services.CategoryService;
import de.transformationsstadt.geoportal.services.DataGroupService;
import de.transformationsstadt.geoportal.services.KeyValuePairService;
import de.transformationsstadt.geoportal.services.PermissionService;
import de.transformationsstadt.geoportal.services.RoleService;
import de.transformationsstadt.geoportal.services.UserService;

/**
 * 
 * API-Teil, der den Unterpfad "/setup/" (/geoportal/setup/) abbildet.
 * 
 * Hier werden die (initialen) BLI-Dimensionen, Kategorien und Unterkategorien angelegt.
 * Darüber hinaus werden hier die initialen Benutzer ( {@link User} ) , Rollen ( {@link Role} ) und Berechtigungen ( {@link Permission} ) angelegt.  
 * TODO: Aus der API nehmen, Setup-Routine schreiben.
 * 
 * @author Sebastian Bruch <s.bruch@utopiastadt.eu>
 * 
 */
@Path("/setup/")
public class Setup {
	@Autowired
	UserService userService;
	
	@Autowired
	RoleService roleService;
	
	@Autowired
	BliDimensionService bliService;
	
	@Autowired 
	CategoryService categoryService;
	
	@Autowired 
	DataGroupService dataGroupService;

	@Autowired 
	PermissionService permissionService;
	
	@Autowired
	KeyValuePairService kvpService;
	
	
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
	public Response RunSetup() {
		if(userService == null) {
			return Response.status(Status.SERVICE_UNAVAILABLE).entity(null).build();
		}
		HashMap<String,String> map = new HashMap<String,String>();
		if(userService.getUser("admin")!=null) {
			map.put("error","setup ran before, users exist.");
			return Response.status(Status.CONFLICT).entity(map).build();
		}

		User admin = new User();
		admin.setUsername("admin");
		admin.setPassword("admin");
		admin.setEmail("admin@example.com");
		admin.setActive(true);
		admin.setValidated(true);

		User sam = new User();
		sam.setUsername("sam");
		sam.setPassword("sam");
		sam.setEmail("sam@example.com");
		sam.setActive(true);
		sam.setValidated(true);


		User ErNa = new User();
		ErNa.setUsername("ErNa");
		ErNa.setPassword("ErNa");
		ErNa.setEmail("ErNa@example.com");
		ErNa.setActive(true);
		ErNa.setValidated(true);

		User system = new User();
		system.setUsername("system");
		system.setPasswordHash("sollte so nicht aus der hashfunktion kommen können.");
		system.setSalt("ist mir egal.soll sich ja garnicht einloggen.");
		system.setLocked(true);
		system.setActive(false);
		system.setValidated(true);
		
		User user = new User();
		user.setUsername("user");
		user.setPassword("user");
		user.setEmail("user@example.com");
		user.setActive(true);
		user.setValidated(true);

		User redakteur = new User();
		redakteur.setUsername("redakteur");
		redakteur.setPassword("redakteur");
		redakteur.setEmail("redakteur@example.com");
		redakteur.setActive(true);
		redakteur.setValidated(true);		


		Role adminRole = new Role();
		adminRole.setName("admin");
		roleService.create(adminRole);
		
		Role userRole = new Role();
		userRole.setName("user");
		roleService.create(userRole);
		
		Role redakteurRole = new Role();
		redakteurRole.setName("redakteur");
		roleService.create(redakteurRole);
		
		

		
		Permission adminPermission = new Permission();
		adminPermission.setPermission("*:*:*");
		permissionService.create(adminPermission);
		
		
		adminRole.addPermission(adminPermission);
		
		admin.addRole(adminRole);
		sam.addRole(adminRole);
		ErNa.addRole(adminRole);
		
		Permission userPermission=new Permission();
		userPermission.setPermission("own:*:*");
		permissionService.create(userPermission);
		
		userRole.addPermission(userPermission);
		user.addRole(userRole);

		Permission redakteurPermission = new Permission();
		redakteurPermission.setPermission("*:read:*");
		
		permissionService.create(redakteurPermission);
		
		redakteurRole.addPermission(redakteurPermission);
		redakteurRole.addPermission(userPermission);
		redakteur.addRole(redakteurRole);




		ArrayList<BliDimension> BliDimensions=new ArrayList<BliDimension>();
		BliDimensions.add( new BliDimension("Einkommen","Materielle Ausstattung der Menschen (Einkommen und andere finanzielle Leistungen wie Rente oder Arbeitslosengeld)."));
		BliDimensions.add( new BliDimension("Arbeit","Die Verfügbarkeit von guten und sicheren Arbeitsplätzen."));
		BliDimensions.add( new BliDimension("Wohnbedingungen"," Wohnqualität in Wuppertal, inklusive Wohnungsgröße und -qualität, Wohnumgebung, Miethöhe und weitere Aspekte wie Leerstand und Aussehen der Straßenzüge."));
		BliDimensions.add( new BliDimension("Gesundheit"," Ein langes, gesundes Leben und die Voraussetzungen dazu wie eine gute medizinische Versorgung und gesundheitsfördernde Angebote und Umgebung (Ernährung, Bewegung, Bildungsarbeit)."));
		BliDimensions.add( new BliDimension("Work-Life-Balance",""));
		BliDimensions.add( new BliDimension("Bildung","Vielfältige hochwertige Bildungsangebote, sowohl schulische Bildung(-sabschlüsse) als auch Weiterbildungsangebote, Workshops und Ausbildungsmöglichkeiten."));
		BliDimensions.add( new BliDimension("Gemeinschaft","Die Einbindung in soziale Beziehungen mit FreundInnen, Familie, NachbarInnen und MitbürgerInnen, sowie auch Unterstützungsnetzwerke, Nachbarschaftshilfe und öffentliche Räume."));
		BliDimensions.add( new BliDimension("Engagement/Beteiligung","Die Möglichkeiten der Menschen ihre Umgebung zu gestalten, ob im Ehrenamt, in Wahlen oder durch Bürgerbeteiligung."));
		BliDimensions.add( new BliDimension("Umwelt","Eine saubere Umwelt mit frischer Luft, sauberen Gewässern und Parks, Umweltschutzprojekte und umweltfreundliche Flächennutzung."));
		BliDimensions.add( new BliDimension("Sicherheit"," Statistische und gefühlte Sicherheit vor Verbrechen und Unfällen aber auch Präventionsarbeit."));
		BliDimensions.add( new BliDimension("Zufriedenheit","Die persönliche Zufriedenheit mit dem eigenen Leben und der Umgebung, wie der Nachbarschaft, dem Quartier und der Stadt oder dem Dorf."));
		BliDimensions.add( new BliDimension("Infrastruktur","Städtische Infrastruktur z.B. für Verkehr aber auch lokale Einkaufsmöglichkeiten."));
		BliDimensions.add( new BliDimension("Freizeit und Kultur","Barrierefreier Zugang und gute Angebote zur Freizeitgestaltung wie Kunst und Kultur und auch die Zeit, diese zu nutzen."));



		ArrayList<Category> categories = new ArrayList<Category>();
		ArrayList<DataGroup> datagroups = new ArrayList<DataGroup>();
		Category cat;

		
		KeyValuePair kvp; 
		cat = new Category("Allgemeines","");
		cat.setDisplayName("Allgmeines");
		kvp = new KeyValuePair("_gdp:welfare","Gemeinnützigkeit");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		
		kvp = new KeyValuePair("_gdp:aktionsradius","Räumlicher Aktionsradius");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		
		
		cat = new Category("Ernährung","");
		cat.setDisplayName("Dieser Ort und seine Sharing- und Giving-Angebote");
		kvp = new KeyValuePair("_gpd:sustainable_nutrition","","Angebote zum Thema nachhaltige Ernährung");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		kvp = new KeyValuePair("_gpd:sustainable_nutrition_assortment","","Sortiment");
		kvpService.create(kvp);
				
		cat.addSuggestedKey(kvp);
		
		categoryService.create(cat);
		cat = new Category("Sharing & Giving","");
		cat.setDisplayName("Dieser Ort und seine Sharing- & Giving-Angebote");
		
		kvp = new KeyValuePair("_gpd:sharing_offers","","Angebote des Teilen und Schenkens");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		
		kvp = new KeyValuePair("_gpd:sharing_free_offers","","Kostenfreie Angebote");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		

		kvp = new KeyValuePair("_gpd:sharing_organisator","","Organisator*in");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		
		kvp = new KeyValuePair("_gpd:sharing_nonfree_offers","","Kostenpflichtige Angebote");
		kvpService.create(kvp);
		cat.addSuggestedKey(kvp);
		categoryService.create(cat);
		
		/*
		
		DataGroup vereine = new DataGroup("Vereine");
		DataGroup projekte = new DataGroup("Projekte");
		DataGroup initiativen= new DataGroup("Initiativen");
		DataGroup bildungseinrichtungen = new DataGroup("Bildungseinrichtungen");
		DataGroup unternehmen = new DataGroup("Unternehmen");
		DataGroup stammtische = new DataGroup("Stammtische");
		datagroups.add(vereine);
		datagroups.add(projekte);
		datagroups.add(initiativen);
		datagroups.add(bildungseinrichtungen);
		datagroups.add(unternehmen);
		datagroups.add(stammtische);

		cat = new Category("Akteure & Austausch", "Vereine, Projekte, Initiativen, Bildungseinrichtungen, Stammtische und deren Netzwerke");
		cat.addDataGroup(vereine);
		cat.addDataGroup(projekte);
		cat.addDataGroup(initiativen);
		cat.addDataGroup(bildungseinrichtungen);
		cat.addDataGroup(unternehmen);
		cat.addDataGroup(stammtische);
		categories.add(cat);
		cat = new Category("besondere Orte","das sind Orte, die ich als Besucher aufsuchen kann z. B. Veranstaltungsorte, Umweltstationen, Badeseen, aber auch wichtige Orte für das Gute Leben, wie z. B. nachhaltiger Energieversorgung; teilweise sind Akteure auch besondere Orte, wie z.B. Utopiastadt");
		DataGroup veranstOrte = new DataGroup("Veranstaltungsorte");
		datagroups.add(veranstOrte);
		cat.addDataGroup(veranstOrte);


		DataGroup infoOrte = new DataGroup("Informationsorte");
		datagroups.add(infoOrte);
		cat.addDataGroup(infoOrte);
		DataGroup sportOrte = new DataGroup("Sport- und Erholungsorte");
		datagroups.add(sportOrte);
		cat.addDataGroup(sportOrte);
		DataGroup nachhaltigerKonsumOrte = new DataGroup("Orte des nachhaltigen Konsums");
		datagroups.add(nachhaltigerKonsumOrte);
		cat.addDataGroup(nachhaltigerKonsumOrte);
		{
			// ToDo: Unterkategorien.
			DataGroup teilen = new DataGroup("Teilen und Schenken");
			datagroups.add(teilen);
			cat.addDataGroup(teilen);
			{
				DataGroup foodsharing = new DataGroup("Foodsharing");
				dataGroupService.create(foodsharing);
				datagroups.add(foodsharing);
				teilen.addDataGroup(foodsharing);
				
				DataGroup carsharing = new DataGroup("Carsharing");
				dataGroupService.create(carsharing);
				datagroups.add(carsharing);
				
				teilen.addDataGroup(carsharing);

				DataGroup fahrradverleih = new DataGroup("Fahrradverleih");
				dataGroupService.create(fahrradverleih);
				datagroups.add(fahrradverleih);
				teilen.addDataGroup(fahrradverleih);

				DataGroup secondhand = new DataGroup("Second-Hand Geschäfte und Verschenkschränke");
				dataGroupService.create(secondhand);
				datagroups.add(secondhand);
				teilen.addDataGroup(secondhand);

			}
			DataGroup ernaehrung = new DataGroup("Ernährung");
			dataGroupService.create(ernaehrung);
			datagroups.add(ernaehrung);
			cat.addDataGroup(ernaehrung);

			{
				DataGroup landwirtschaft = new DataGroup("Landwirtschaftlicher Betrieb");
				dataGroupService.create(landwirtschaft);
				datagroups.add(landwirtschaft);
				ernaehrung.addDataGroup(landwirtschaft);

				DataGroup lebensmittelgeschaeft = new DataGroup("Lebensmittelgeschäft");
				dataGroupService.create(lebensmittelgeschaeft);
				datagroups.add(lebensmittelgeschaeft);
				ernaehrung.addDataGroup(lebensmittelgeschaeft);

				DataGroup essensausgabe = new DataGroup("Essensausgabe");
				dataGroupService.create(essensausgabe);
				dataGroupService.create(essensausgabe);
				datagroups.add(essensausgabe);
				ernaehrung.addDataGroup(essensausgabe);

				{
					DataGroup imbissRestaurant = new DataGroup("Imbiss, Restaurant, Cafe");
					dataGroupService.create(imbissRestaurant);
					datagroups.add(imbissRestaurant);
					essensausgabe.addDataGroup(imbissRestaurant);

					DataGroup foodSharing2 = new DataGroup("Foodsharing");
					dataGroupService.create(foodSharing2);
					datagroups.add(foodSharing2);
					essensausgabe.addDataGroup(foodSharing2);
					DataGroup tafel = new DataGroup("Tafel");
					dataGroupService.create(tafel);
					datagroups.add(tafel);
					essensausgabe.addDataGroup(tafel);
				}
				DataGroup anbau = new DataGroup("Anbau von Lebensmitteln");
				dataGroupService.create(anbau);
				datagroups.add(anbau);
				
				cat.addDataGroup(anbau);
				{
					DataGroup urbanGardening = new DataGroup("Urban Gardening Fläche");
					dataGroupService.create(urbanGardening);
					datagroups.add(urbanGardening);
					anbau.addDataGroup(urbanGardening);

					DataGroup landwirtschaftlicherBetrieb2 = new DataGroup("Landwirtschaftlicher Betrieb");
					dataGroupService.create(landwirtschaftlicherBetrieb2);
					datagroups.add(landwirtschaftlicherBetrieb2);
					anbau.addDataGroup(landwirtschaftlicherBetrieb2);

					DataGroup kleingarten = new DataGroup("Kleingartenanlage");
					dataGroupService.create(kleingarten);
					datagroups.add(kleingarten);
					anbau.addDataGroup(kleingarten);

					DataGroup potentiellUrbanGardening = new DataGroup("potenzielle Fläche für urban Gardening");
					dataGroupService.create(potentiellUrbanGardening);
					datagroups.add(potentiellUrbanGardening);
					anbau.addDataGroup(potentiellUrbanGardening);
				}
			}
		}
		categories.add(cat);
		cat =new Category("Statistiken","statistische Daten zu z.B. durchschnittlichen Nettoeinkommen, Bevölkerungszahlen usw, diese beziehen sich auf Quartiere, Stadtteile, Kreise oder Regionen");
		categoryService.create(cat);
		categories.add(cat);
		DataGroup bevoelkerung = new DataGroup("Bevölkerung /Soziodemographische Daten");
		dataGroupService.create(bevoelkerung);
		datagroups.add(bevoelkerung);
		cat.addDataGroup(bevoelkerung);

		DataGroup einwohnerzahl = new DataGroup("Einwohnerzahl");
		dataGroupService.create(einwohnerzahl);
		datagroups.add(einwohnerzahl);
		bevoelkerung.addDataGroup(einwohnerzahl);


		DataGroup einkommen = new DataGroup("Einkommensdurchschnitt");
		dataGroupService.create(einkommen);
		datagroups.add(einkommen);
		bevoelkerung.addDataGroup(einkommen);

		DataGroup altersstruktur = new DataGroup("Altersstruktur");
		dataGroupService.create(altersstruktur);
		datagroups.add(altersstruktur);
		bevoelkerung.addDataGroup(altersstruktur);

		DataGroup subjEmpfinden = new DataGroup("subjektives Empfinden");
		dataGroupService.create(subjEmpfinden);
		datagroups.add(subjEmpfinden);
		bevoelkerung.addDataGroup(subjEmpfinden);

		DataGroup zufriedenheit = new DataGroup("Lebenszufriedenheit");
		dataGroupService.create(zufriedenheit);
		datagroups.add(zufriedenheit);
		subjEmpfinden.addDataGroup(zufriedenheit);

		DataGroup ernaehrungStat = new DataGroup("Ernährung");
		dataGroupService.create(ernaehrungStat);
		datagroups.add(ernaehrungStat);
		cat.addDataGroup(ernaehrungStat);

		DataGroup strukturKonsum = new DataGroup("Struktur der Konsumausgaben");
		dataGroupService.create(strukturKonsum);
		datagroups.add(strukturKonsum);
		ernaehrungStat.addDataGroup(strukturKonsum);


		cat = new Category("Planungen","zukuküftig entstehende Dinge (Bebauungspläne, Entwicklungspläne z.B. für Wegenetze usw.)");
		categoryService.create(cat);
		categories.add(cat);
		cat = new Category("Historisches","Denkmähler, Stolpersteine, aber auch vergangene Orte, z. B. Remscheider Tour zu vergangenen Kneipen");
		categoryService.create(cat);
		DataGroup vergangeneOrte = new DataGroup("Vergangene Orte");
		dataGroupService.create(vergangeneOrte);
		cat.addDataGroup(vergangeneOrte);
		datagroups.add(vergangeneOrte);


		categories.add(cat);
		cat = new Category("Wege & Netze","Radwege, Busrouten, aber auch Versorgungsnetze wie E-Bike-Stationen");
		DataGroup wege = new DataGroup("Wege");
		dataGroupService.create(wege);
		cat.addDataGroup(wege);
		datagroups.add(wege);

		DataGroup radwege = new DataGroup("Radwege");
		dataGroupService.create(radwege);
		wege.addDataGroup(radwege);
		datagroups.add(radwege);
		DataGroup fusswege = new DataGroup("Fusswege");
		dataGroupService.create(fusswege);
		wege.addDataGroup(fusswege);
		datagroups.add(fusswege);

		DataGroup stationen = new DataGroup("Stationen");
		dataGroupService.create(stationen);
		datagroups.add(stationen);
		cat.addDataGroup(stationen);
		

		DataGroup leihenUndMieten = new DataGroup("Leihen und Mieten");
		dataGroupService.create(leihenUndMieten);
		datagroups.add(leihenUndMieten);
		DataGroup carsharing = new DataGroup("Carsharing");
		dataGroupService.create(carsharing);
		DataGroup fahrradverleih = new DataGroup("fahrradverleih");
		dataGroupService.create(fahrradverleih);
		DataGroup sonstiges = new DataGroup("Sonstiges");
		dataGroupService.create(sonstiges);
		datagroups.add(carsharing);
		datagroups.add(fahrradverleih);
		datagroups.add(sonstiges);
		leihenUndMieten.addDataGroup(carsharing);
		leihenUndMieten.addDataGroup(fahrradverleih);
		leihenUndMieten.addDataGroup(sonstiges);

		

		categories.add(cat);
		cat = new Category("Veranstaltungen","wurde immer wieder gewünscht, hier wäre eine intelligente Schnittstelle sinnvoller als das Eintragen über das Portal, diese Kategorie hat aber aktuell keine hohe Priorität");
		categoryService.create(cat);
		categories.add(cat);
		cat = new Category("Umgebung","hier sind Grunflächen, Bäume, Wasserläufe, landwirtschaftliche Flächen gemeint; also Dinge die unsere Umgebung ausmachen, aber keine direkten Orte sind");
		categoryService.create(cat);
		DataGroup gruenflaechen = new DataGroup("Grünflächen und Vegetation");
		dataGroupService.create(gruenflaechen);
		datagroups.add(gruenflaechen);
		cat.addDataGroup(gruenflaechen);

		DataGroup parks=new DataGroup("Parks");
		DataGroup waelder=new DataGroup("Wälder");
		DataGroup felder=new DataGroup("Felder");
		DataGroup seenUndGewaesser=new DataGroup("Seen und Gewässer");
		dataGroupService.create(parks);
		dataGroupService.create(waelder);
		dataGroupService.create(felder);
		dataGroupService.create(seenUndGewaesser);
		gruenflaechen.addDataGroup(parks);
		gruenflaechen.addDataGroup(waelder);
		gruenflaechen.addDataGroup(felder);
		gruenflaechen.addDataGroup(seenUndGewaesser);

		datagroups.add(parks);
		datagroups.add(waelder);
		datagroups.add(felder);
		datagroups.add(seenUndGewaesser);


		DataGroup landwFl=new DataGroup("Landwirtschaftliche Flächen");
		dataGroupService.create(landwFl);
		cat.addDataGroup(landwFl);
		datagroups.add(landwFl);
		categories.add(cat);
		 */
		try {
			/*
			PermissionDAO.create(userPermission);
			PermissionDAO.create(redakteurPermission);
			PermissionDAO.create(adminPermission);
			RoleDAO.create(userRole);
			RoleDAO.create(redakteurRole);
			RoleDAO.create(adminRole);
			*/
			
			userService.create(system);
			userService.create(user);
			userService.create(redakteur);
			userService.create(admin);
			userService.create(sam);
			userService.create(ErNa);
			for (BliDimension bliDimension : BliDimensions) {
				bliService.create(bliDimension);
			}
			int i=0;

			for(DataGroup _dg: datagroups) {
				try {
				dataGroupService.create(_dg);
				}catch(TransientObjectException toe) {
					for(DataGroup subDataGroup : _dg.getDataGroups()){
						if(subDataGroup.getId() == null) {
							System.out.println("Transient: "+subDataGroup.getName());
						}
					}
				}
				catch(Exception e) {
					System.out.println("Exception:" +e.getMessage());
					System.out.println("Element: "+_dg.getName());

					for(DataGroup subDataGroup : _dg.getDataGroups()){
						if(subDataGroup.getId() == null) {
							System.out.println("Transient: "+subDataGroup.getName());
						}
					}
				}
				
			}

			for(Category _cat: categories) {
				try{
					if(_cat.getId() == null) {
						categoryService.create(_cat);
					}else {
						categoryService.update(_cat);
					}
				}catch(PersistenceException cve) {

					map.put(Integer.toString(++i),"error creating "+_cat.getName());
				}
			}

		}catch(PersistenceException cve) {
			map.put("error","persistence-error. "+cve.getMessage());
			throw cve;
		}



		return Response.status(Status.OK).entity(categoryService.getAll()).build();
	}
}