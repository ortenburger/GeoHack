package de.transformationsstadt.geoportal.overpass;

import java.util.ArrayList;
import java.util.HashMap;

import de.transformationsstadt.geoportal.overpass.OverpassResultElement;

/**
 * Bildet die Struktur der Rückgabewerte der bisherigen Anfragen an die Overpass-API ab,
 * so dass das Ergebnis in ein solches Objekt deserialisiert werden kann.
 * Siehe auch {@link OverpassResultElement}
 * @author Sebastian Bruch
 *
 */
class OverpassResult{

	public Float version;
	public String generator;
	
	public HashMap<String,String> osm3s;
	public ArrayList<OverpassResultElement> elements;
	public HashMap<String,String> tags;
	OverpassResult(){};
}