package de.transformationsstadt.geoportal.apiparameters;

import de.transformationsstadt.geoportal.entities.BliDimension;

/**
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 * Parameter für die API, der die Informationen beinhaltet, die ein Objekt vom Typ {@link BliDimension} definiert.
 * Dient der Deserialisierung von Eingabedaten.
 */
public class BliDimensionParameter {
	public Long id;
	public String name;
	public String description;
}
