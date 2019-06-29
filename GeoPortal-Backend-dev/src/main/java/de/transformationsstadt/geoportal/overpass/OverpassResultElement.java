package de.transformationsstadt.geoportal.overpass;

import java.util.ArrayList;

import java.util.HashMap;

/**
 * Bildet einen Teil der Unterstruktur der Rückgabewerte der bisherigen Anfragen an die Overpass-API ab,
 * so dass das Ergebnis in ein solches Objekt deserialisiert werden kann.
 * Siehe auch {@link OverpassResult}
 * 
 * @author Sebastian Bruch
 *
 */
class OverpassResultElement{
	public String type;
	public Long id;
	public Float lon;
	public Float lat;
	public ArrayList<Long> nodes;
	public HashMap<String,String> tags;
	OverpassResultElement(){}
}