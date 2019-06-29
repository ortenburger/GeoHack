package de.transformationsstadt.geoportal.apiparameters;

import de.transformationsstadt.geoportal.entities.KeyValuePair;

/**
 * Objekt, in das Daten für ein {@link KeyValuePair}-Objekt (sowas wie Tags) deserialisiert werden.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class KeyValuePairParameter {
	public Long id;
	public String key;
	public String value;
}
