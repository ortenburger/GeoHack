package de.transformationsstadt.geoportal.entities;


/**
 * Enum für die verschiedenen Typen der aus OSM stammenden Daten.
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 * 
 * 
 *
 */
public enum OsmElementType {
	UNSET,
	NODE,
	WAY,
	RELATION,
	TAG;

	
	/**
	 * Gibt zu definierten Strings das assoziierte Enum zurück.
	 * @param type {@link String}
	 * @return
	 */
	public static OsmElementType fromString(String type) {
		if(type == null) {
			return UNSET;
		}
		if(type.toLowerCase().equals("node")) {
			return NODE;
		}
		if(type.toLowerCase().equals("knoten")) {
			return NODE;
		}
		if(type.toLowerCase().equals("way")) {
			return WAY;
		}
		if(type.toLowerCase().equals("linie")) {
			return WAY;
		}
		return UNSET;
	}
	@Override
	public String toString() {
		return name().toLowerCase();
	}
}
