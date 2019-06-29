package de.transformationsstadt.geoportal.apiparameters;

/**
 * Objekt, in das Authentifizierungsdaten deserialisiert werden.
 * 
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class Credentials {

	private String username;
	private String password;
	
	public String getUsername() { return this.username; }
	public String getPassword() { return this.password; }
	
	public void setUsername(String username) { this.username = username; }
	public void setPassword(String password) { this.password = password; }
	

	public Credentials() {
		
	}

	public Credentials(String username,String password) {
		this.setUsername(username);
		this.setPassword(password);
	}
}