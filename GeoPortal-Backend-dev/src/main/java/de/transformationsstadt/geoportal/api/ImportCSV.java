package de.transformationsstadt.geoportal.api;

import java.io.FileInputStream;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import de.transformationsstadt.geoportal.entities.BliDimension;
import de.transformationsstadt.geoportal.entities.KeyValuePair;
import de.transformationsstadt.geoportal.entities.LogEntry;
import de.transformationsstadt.geoportal.entities.OsmElementType;
import de.transformationsstadt.geoportal.entities.OsmReference;
import de.transformationsstadt.geoportal.entities.User;
import de.transformationsstadt.geoportal.overpass.Overpass;
import de.transformationsstadt.geoportal.services.BliDimensionService;
import de.transformationsstadt.geoportal.services.KeyValuePairService;
import de.transformationsstadt.geoportal.services.LogService;
import de.transformationsstadt.geoportal.services.OsmReferenceService;
import de.transformationsstadt.geoportal.services.UserService;

/**
 * 
 * API-Teil, der den Unterpfad "/runimport/" (/geoportal/runimport/) abbildet.
 * 
 * Diese Funktion importiert die unter "data.csv" (muss im CLASSPATH liegen) hinterlegten Daten um das Geoportal mit 
 * einem ersten Datensatz zu Bootstrappen.
 * 
 * TODO: Aus der API nehmen, Setup-Routine schreiben.
 * 
 * @author Sebastian Bruch <s.bruch@utopiastadt.eu>
 * 
 */

@Path("/runimport/")
@Component
public class ImportCSV {
	@Autowired
	UserService userService;
	
	@Autowired 
	BliDimensionService bliService;
	
	@Autowired
	OsmReferenceService osmRefService;
	
	@Autowired
	LogService logService;
	
	@Autowired
	KeyValuePairService kvpService;
	
	Overpass overpass;
	private ArrayList<OsmReference> elements;
	private String file = "data.csv";
	private ArrayList<BliDimension> bliDimensions;
	@GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON+";charset=utf-8")
	public Response runImport() throws Exception {
		if(osmRefService.Count()>0) {
			return Response.status(Status.CONFLICT).entity("{\"error\":\"import ran before.\"}").build();
		}

		LogEntry log = new LogEntry("system-user","GET","/runimport/","",0);
		logService.create(log);
		elements = new ArrayList<OsmReference>();
		bliDimensions = new ArrayList<BliDimension>();
		bliDimensions = (ArrayList<BliDimension>) bliService.getAll();
		HashMap<String,String> map = new HashMap<String,String>();
		String filename = getClass().getClassLoader().getResource(file).getPath().toString();

		filename = filename.replaceAll("/C:","");
		User system = userService.getUser("system");
		if(system == null) {
			throw new Exception("system-user not found.");
		}
		map.put("user", system.getUsername());
		map.put("filename", filename);
		this.overpass = new Overpass();


		try (
				FileInputStream fis = new FileInputStream(filename);
				InputStreamReader isr = new InputStreamReader(fis,StandardCharsets.UTF_8);
				CSVReader csvReader = new CSVReaderBuilder(isr).build(); 
				//CSVReader csvReader = new CSVReader(isr);



				) {
			// Reading Records One by One in a String array
			String[] nextRecord;
			map.put("tryblock","begin");

			int i=0;
			int recordsValid=0;
			int recordsInvalid=0;

			while ((nextRecord = csvReader.readNext()) != null) {
				map.put("whileloop","begin");

				String record = createObject(nextRecord,system);
				if(record != null) {
					map.put("successful_record"+i,record);
					recordsValid++;
				}else{
					map.put("failed_record"+i,nextRecord[3]);
					recordsInvalid++;
				}
				i++;
			}
			map.put("whileloop","done");


			//map.clear();
			map.put("recordsInvalid",Integer.toString(recordsInvalid));
			map.put("recordsValid",Integer.toString(recordsValid));
		}
		return Response.status(Status.OK).entity(this.elements).build();
	}


	public String createObject(String[] cells,User creater) throws Exception {
		String name = cells[1].trim();
		Long osm_id =-1l;
		String osmType = cells[2].trim();
		OsmReference osmRef = new OsmReference();
		
		try {
			osm_id = Long.parseLong(cells[3].replaceAll("[^\\d.]", ""));
		}catch(NumberFormatException nfe) {
			return "Number formatting went front for "+cells[3];
		}
		OsmReference dbEntry = osmRefService.getFirstOsmReferenceByOsmId(osm_id,osmType.toLowerCase());
		if(dbEntry != null) {
			osmRef = dbEntry;
			//System.out.println("Already in database, id: "+osmRef.getId());
		}
		String organisationsForm = cells[4].trim();
		
		//String gehoertZu = cells[5].trim();
		//String gehoertZuOsmType=cells[6].trim();
		//String gehoertZuOsmId=cells[7].trim();
		
		String description = cells[8].trim();
		String website = cells[9].trim();
		String street = cells[10].trim();
		String number = cells[11].trim();
		String zipCode = cells[12].trim();
		//String stadtteil = cells[11];
		String city = cells[14].trim();
		String country = cells[15].trim();

		String email = cells[16].trim();
		String openingHours = cells[17].trim();
		String dimensionen = cells[18].trim();
		//String tags = cells[19].trim();
		String gruendung = cells[20].trim();
		//String reichweite = cells[16];

		String hauptamtliche = cells[57].trim();
		String ehrenamtliche = cells[58].trim();
		String aktionsradius = cells[59].trim();
		String quelle = cells[60].trim();
		String erhebungsmethode = cells[61].trim();
		osmRef.setOsmId(osm_id);
		osmRef.setName(name);
		osmRef.setDescription(description);
		osmRef.setCreatedBy(creater);
		osmRef.setChangedBy(creater);

		osmRef.setLon((Double)(-1.0));
		osmRef.setLat((Double)(-1.0));
		osmRef.setCreated(new Date());
		HashSet<KeyValuePair> values = new HashSet<KeyValuePair>();


		if(!website.isEmpty()) {
			KeyValuePair kvp =new KeyValuePair("contact:website",website);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!street.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("addr:street",street);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!number.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("addr:housenumber",number);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);

		}
		if(!zipCode.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("addr:postcode",zipCode);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!city.isEmpty()) {			
			KeyValuePair kvp = new KeyValuePair("addr:city",city);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!country.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("addr:country",country);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!email.isEmpty()) {
			
			//KeyValuePair kvp = new KeyValuePair("addr:email",email); 
			//KeyValuePairDAO.create(kvp);
			//values.add(kvp);
		}
		if(!openingHours.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("opening_hours",openingHours);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
			//values.add(new KeyValuePair("opening_hours",openingHours));
		}
		if(!gruendung.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("start_date",gruendung);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
			
		}
		if(!organisationsForm.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("_gpd:organisationsForm",organisationsForm);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
			
		}
		if(!hauptamtliche.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("_gpd:hauptamtliche",hauptamtliche);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!ehrenamtliche.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("_gpd:ehrenamtliche",ehrenamtliche);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);

		}
		if(!aktionsradius.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("_gpd:aktionsradius",aktionsradius);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!quelle.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("_gpd:quelle",quelle);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}
		if(!erhebungsmethode.isEmpty()) {
			KeyValuePair kvp = new KeyValuePair("_gpd:erhebungsmethode",erhebungsmethode);
			kvp.setSource("import");
			kvpService.create(kvp);
			values.add(kvp);
		}



		osmRef.setTags(values);
		osmRef.setType(OsmElementType.fromString(osmType));

		for(String str : dimensionen.split("\n")) {
			BliDimension dim = getBli(str.trim());
			if(dim != null) {
				osmRef.addBliDimension(dim);
			}
		}

		if(osmRef.getId() == null) {

			osmRef=osmRefService.create(osmRef);
		}
		for(int i=21;i<56;i+=3) {
			String partnerName=cells[i].trim();
			String partnerType=cells[i+1].trim();
			try {
				if(cells[i+2].replaceAll("[^\\d.]", "").isEmpty()) {
					continue;
				}
				
				
				Long partnerosmId = Long.parseLong(cells[i+2].replaceAll("[^\\d.]", ""));
				if(osmRef.getOsmId().equals(partnerosmId) &&  osmRef.getType().equals(osmRef.getType())){
					System.out.println("... self-references are not allowed..");
					continue;
				}
				OsmReference peer = new OsmReference();
				
				peer.setName(partnerName);
				peer.setOsmId(partnerosmId);
				peer.setLon((double)-1f);
				peer.setLat((double)-1f);
				peer.setType(OsmElementType.fromString(partnerType));
				OsmReference peerFromDB = osmRefService.getFirstOsmReferenceByOsmId(partnerosmId, partnerType.toLowerCase());
				if(peerFromDB != null) {
					peer = peerFromDB;
					
					partnerName = peer.getName();
				}
				if(!partnerName.isEmpty() && !partnerType.isEmpty()) {
					peer.setCreatedBy(creater);
					peer.setCreated(new Date());
					peer.addPeer(osmRef);
					if((peer.getLon() == -1f || peer.getLat() == -1f) && peer.getType() != OsmElementType.UNSET) {
						//overpass.completeElement(peer);
					}

					if(peer.getId() == null) {
						peer = osmRefService.create(peer);
					}else {
						osmRefService.update(peer);
					}

					osmRef.addPeer(peer);
				}
				
			}catch(NumberFormatException nfe) {
			}
		}
		try {
			}catch(org.springframework.dao.DataIntegrityViolationException dive) {
				
				System.out.println("");
				System.out.println("");
				System.out.println("Error updating "+osmRef.getName() + " ("+osmRef.getType()+":"+osmRef.getOsmId()+")"+dive.getMessage());
				if(dive.getCause() instanceof ConstraintViolationException) {
					System.out.println("Nested: "+dive.getCause().getMessage());
				}
				System.out.println("");
				System.out.println("");
				
			}
		

		if((osmRef.getLon() == -1f || osmRef.getLat() == -1f)&& osmRef.getType() != OsmElementType.UNSET) {
			try {
				//overpass.completeElement(osmRef);
			}catch(Exception e) {
				System.out.println("Error reading from overpass: "+osmRef.getName());
			}
				
		}
		osmRefService.update(osmRef);
		this.elements.add(osmRef);

		return Long.toString(osm_id);
	}

	BliDimension getBli(String name) {
		Map<String,String> bliMap = new HashMap<String,String>();
		bliMap.put("Arbeit", "Arbeit");
		bliMap.put("Wohnen", "Wohnbedingungen");
		bliMap.put("Gesundheit", "Gesundheit");
		bliMap.put("Bildung", "Bildung");
		bliMap.put("Gemeinschaft", "Gemeinschaft");
		bliMap.put("Engagement", "Engagement/Beteiligung");
		bliMap.put("Umwelt", "Umwelt");
		bliMap.put("Sicherheit", "Sicherheit");
		bliMap.put("Zufriedenheit", "Zufriedenheit");
		bliMap.put("Infrastruktur", "Infrastruktur");
		bliMap.put("Kultur und Freizeit", "Freizeit und Kultur");
		bliMap.put("Gesundheit", "Gesundheit");

		for(BliDimension dim: this.bliDimensions) {
			if(dim.getName().equals(name) || dim.getName().equals(bliMap.get(name))){
				return dim;
			}
		}
		return null;
	}
}

