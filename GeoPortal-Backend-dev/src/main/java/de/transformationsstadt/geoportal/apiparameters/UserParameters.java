package de.transformationsstadt.geoportal.apiparameters;

import java.util.HashSet;
import java.util.Set;

import de.transformationsstadt.geoportal.entities.OsmReference;

/**
 * Objekt, in das {@link User}-Objekte deserialisiert werden.
 * 
 * Siehe ( {@link User} )
 * @author Sebastian Bruch < s.bruch@utopiastadt.eu >
 *
 */
public class UserParameters {

	public String username;
	public String email;
	public String password;
	public String firstname;
	public String lastname;
	public String description;
	public String eulaVersion;
	public Set<String> errors = new HashSet<String>();
	public UserParameters() {}
	public Set<String> getErrors(){
		return this.errors;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEulaVersion() {
		return eulaVersion;
	}
	public void setEulaVersion(String eulaVersion) {
		this.eulaVersion = eulaVersion;
	}
	public void setErrors(Set<String> errors) {
		this.errors = errors;
	}
	public boolean isValid() {

		this.errors.clear();
		if(username == null || username.isEmpty()) {
			this.errors.add("username is empty");
		}
		if(password == null || password.isEmpty()) {
			this.errors.add("password is empty");
		}
		if(email == null || email.isEmpty()) {
			this.errors.add("email is empty");
		}

		return this.errors.isEmpty();
	}
}
